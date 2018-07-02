package me.tevinjeffrey.kubernetes.home.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_settings.fab
import kotlinx.android.synthetic.main.fragment_settings.list
import kotlinx.android.synthetic.main.fragment_settings.spinner
import kotlinx.android.synthetic.main.fragment_settings.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_settings.toolbar
import me.tevinjeffrey.kubernetes.base.extensions.clicksWithThrottle
import me.tevinjeffrey.kubernetes.base.extensions.observe
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.base.support.BaseFragment.SnackbarType.NEGATIVE
import me.tevinjeffrey.kubernetes.base.support.BaseFragment.SnackbarType.POSITIVE
import me.tevinjeffrey.kubernetes.home.R
import me.tevinjeffrey.kubernetes.home.settings.adapter.CertItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.EndpointItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.HeaderItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.InsecureToggleItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.ProxyToggleItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.SpaceItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.SpinnerAdapter
import me.tevinjeffrey.kubernetes.home.settings.adapter.TokenItem

class SettingsFragment : BaseFragment() {

  private val adapter = GroupAdapter<ViewHolder>()

  private val certViewModel by lazy<CertViewModel> { viewModel() }
  private val endpointViewModel by lazy<EndpointViewModel> { viewModel() }
  private val settingsViewModel by lazy<SettingsViewModel> { viewModel() }
  private val authViewModel by lazy<AuthViewModel> { viewModel() }

  override fun layoutId() = R.layout.fragment_settings

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    swipeRefreshLayout.isEnabled = false
    swipeRefreshLayout.setColorSchemeResources(
        me.tevinjeffrey.kubernetes.base.R.color.colorPrimary,
        me.tevinjeffrey.kubernetes.base.R.color.grey_900
    )

    (list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    list.adapter = adapter
    val itemClicks = MutableLiveData<Item>()
    adapter.setOnItemClickListener { item, view ->
      itemClicks.postValue(item as Item)
    }

    setupToolbar()
    setupEndpoint(itemClicks)
    setupAuth(itemClicks)
    setupCertificates(itemClicks)
  }

  private fun setupToolbar() {
    toolbar.inflateMenu(R.menu.settings)
    toolbar.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.action_add -> {
          settingsViewModel.newClusterConfig()
          true
        }
        R.id.action_delete -> {
          val selectedConfig = settingsViewModel.spinnerLiveData.value?.current
          settingsViewModel.deleteCurrentConfig()
          showSnackbar("Deleted ${selectedConfig?.name}")
          true
        }
        R.id.action_reset -> {
          val selectedConfig = settingsViewModel.spinnerLiveData.value?.current
          settingsViewModel.resetCurrentConfig()
          showSnackbar("Reset ${selectedConfig?.name}")
          true
        }
        R.id.action_edit -> {
          val currentName = settingsViewModel.spinnerLiveData.value?.current?.name.orEmpty()
          MaterialDialog.Builder(requireContext())
              .title(getString(R.string.edit_config_name))
              .inputType(InputType.TYPE_CLASS_TEXT)
              .input(getString(R.string.hint_edit_config_name), currentName) { _, input ->
                if (currentName != input.toString()) {
                  settingsViewModel.updateConfigName(input.toString())
                }
              }
              .show()
          true
        }
        else -> false
      }
    }

    fab.clicksWithThrottle {
      settingsViewModel.checkConnection()
      showLoading(true)
    }

    val spinnerArrayAdapter = SpinnerAdapter(requireContext(),
        R.layout.simple_spinner_item,
        android.R.layout.simple_spinner_dropdown_item,
        mutableListOf())
    spinner.adapter = spinnerArrayAdapter
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      var initialLoad = true
      override fun onNothingSelected(p0: AdapterView<*>?) {}
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        val clusterConfigs = settingsViewModel.spinnerLiveData.value?.all.orEmpty()
        if (clusterConfigs.size > position && !initialLoad) {
          val selectedCluster = clusterConfigs.find { it == clusterConfigs[position] }?.clusterId
          selectedCluster ?: return
          settingsViewModel.setCurrentCluster(selectedCluster)
        }
        initialLoad = false
      }
    }

    observe(settingsViewModel.spinnerLiveData) {
      it ?: return@observe
      spinnerArrayAdapter.itemList.clear()
      spinnerArrayAdapter.itemList.addAll(it.all)
      spinnerArrayAdapter.notifyDataSetChanged()
      spinner.setSelection(it.all.indexOf(it.current))
    }

    observe(settingsViewModel.message) {
      showSnackbar(it.orEmpty(), POSITIVE)
      showLoading(false)
    }

    observe(settingsViewModel.error) {
      showSnackbar(it!!.message.orEmpty(), NEGATIVE)
      showLoading(false)
    }
  }

  private fun setupEndpoint(itemClicks: LiveData<Item>) {
    // Setup adapter section
    val masterUrlItem = EndpointItem(getString(R.string.master_url))

    val insecureConnectionsToggle = InsecureToggleItem(getString(R.string.allow_insecure)) {
      certViewModel.updateAllowInsecure(it)
    }

    insecureConnectionsToggle.body = getString(R.string.body_allow_insecure)

    val proxyToggle = ProxyToggleItem(getString(R.string.proxy_connections)) {
      endpointViewModel.updateShouldProxy(it)
    }

    proxyToggle.body = getString(R.string.body_proxy_url)


    val endpointSection = Section()
    endpointSection.setHeader(HeaderItem(getString(R.string.endpoint)))
    endpointSection.addAll(listOf(masterUrlItem, insecureConnectionsToggle, proxyToggle))
    adapter.add(endpointSection)

    // Observe config changes in the database/
    observe(endpointViewModel.masterUrlResult) {
      masterUrlItem.body = it ?: getString(R.string.body_master_url)
      masterUrlItem.notifyChanged()
    }

    observe(certViewModel.allowInsecureResult) {
      insecureConnectionsToggle.isChecked = (it ?: true)
      insecureConnectionsToggle.notifyChanged()
    }

    observe(endpointViewModel.proxyUrlResult) {
      proxyToggle.body = it ?: getString(R.string.body_proxy_url)
      proxyToggle.notifyChanged()
    }

    observe(endpointViewModel.shouldProxyResult) {
      proxyToggle.isChecked = it ?: true
      proxyToggle.notifyChanged()
    }

    observe(endpointViewModel.error) {
      showSnackbar(it!!.message.orEmpty())
    }

    observe(itemClicks) { item ->
      when (item) {
        masterUrlItem -> {
          makeInputDialog(
              R.string.master_url_title,
              R.string.hint_master_url,
              settingsViewModel.spinnerLiveData.value?.current?.server.orEmpty(),
              InputType.TYPE_TEXT_VARIATION_URI,
              getString(R.string.body_master_url)) {
            endpointViewModel.updateMasterUrl(it)
          }
        }
        proxyToggle -> {
          makeInputDialog(
              R.string.proxy_url,
              R.string.hint_proxy_url,
              settingsViewModel.spinnerLiveData.value?.current?.proxyUrl.orEmpty(),
              InputType.TYPE_TEXT_VARIATION_URI,
              getString(R.string.body_proxy_url)) {
            endpointViewModel.updateProxyUrl(it)
          }
        }
      }
    }
  }

  private fun setupCertificates(itemClicks: LiveData<Item>) {
    // Setup adapter section
    val clientCertItem = CertItem(
        getString(R.string.client_certificate),
        CLIENT_CERT_REQUEST_CODE
    )

    val clientKeyItem = CertItem(
        getString(R.string.client_private_key),
        CLIENT_KEY_REQUEST_CODE
    )

    val clusterCACertItem = CertItem(
        getString(R.string.cluster_ca_certificate),
        CLUSTER_CA_REQUEST_CODE
    )

    val certSection = Section()
    certSection.setHeader(HeaderItem(getString(R.string.certificates)))
    certSection.addAll(
        listOf(
            clientCertItem,
            clientKeyItem,
            clusterCACertItem,
            SpaceItem()
        )
    )
    adapter.add(certSection)

    // Observe config changes in the the database.
    observe(certViewModel.clientCertResult) {
      if (it != null) {
        clientCertItem.body = it.toString()
      } else {
        clientCertItem.body = getString(R.string.body_client_certificate)
      }
      clientCertItem.notifyChanged()
    }

    observe(certViewModel.clientKeyResult) {
      if (it != null) {
        clientKeyItem.body = it.toString()
      } else {
        clientKeyItem.body = getString(R.string.body_client_private_key)
      }
      clientKeyItem.notifyChanged()
    }

    observe(certViewModel.clusterCACertResult) {
      if (it != null) {
        clusterCACertItem.body = it.toString()
      } else {
        clusterCACertItem.body = getString(R.string.body_cluster_ca_certificate)
      }
      clusterCACertItem.notifyChanged()
    }

    observe(certViewModel.allowInsecureResult) {
      TransitionManager.beginDelayedTransition(list)
      clientCertItem.isEnabled = !(it ?: true)
      clientCertItem.notifyChanged()
      clientKeyItem.isEnabled = !(it ?: true)
      clientKeyItem.notifyChanged()
      clusterCACertItem.isEnabled = !(it ?: true)
      clusterCACertItem.notifyChanged()
    }

    observe(certViewModel.error) {
      showSnackbar(it!!.message.orEmpty())
    }

    observe(itemClicks) { item ->
      when (item) {
        is CertItem -> {
          performFileSearch(item.requestCode)
        }
        is InsecureToggleItem -> {
          item.isChecked = !item.isChecked
          item.notifyChanged()
        }
      }
    }
  }

  private fun setupAuth(itemClicks: LiveData<Item>) {
    // Setup adapter section.
    val tokenItem = TokenItem(getString(R.string.bearer_token), BEARER_TOKEN_REQUEST_CODE)
    tokenItem.body = getString(R.string.body_bearer_token)
    val authSection = Section()
    authSection.setHeader(HeaderItem(getString(R.string.auth)))
    authSection.addAll(listOf(tokenItem))
    adapter.add(authSection)

    // Observe config changes in the the database.
    observe(authViewModel.tokenResult) {
      if (it != null) {
        tokenItem.body = it.toString()
      } else {
        tokenItem.body = getString(R.string.body_bearer_token)
      }
      tokenItem.notifyChanged()
    }

    observe(authViewModel.error) {
      showSnackbar(it!!.message.orEmpty())
    }

    observe(itemClicks) { item ->
      when (item) {
        is TokenItem -> {
          performFileSearch(item.requestCode)
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode to resultCode) {
      CLIENT_CERT_REQUEST_CODE to RESULT_OK -> {
        certViewModel.updateClientCert(data?.data ?: return)
      }
      CLIENT_KEY_REQUEST_CODE to RESULT_OK -> {
        certViewModel.updateClientKey(data?.data ?: return)
      }
      CLUSTER_CA_REQUEST_CODE to RESULT_OK -> {
        certViewModel.updateClusterCa(data?.data ?: return)
      }
      BEARER_TOKEN_REQUEST_CODE to RESULT_OK -> {
        authViewModel.updateToken(data?.data ?: return)
      }
    }
  }

  private fun performFileSearch(requestCode: Int) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "*/*"
    startActivityForResult(intent, requestCode)
  }

  private fun showLoading(isLoading: Boolean) {
    swipeRefreshLayout.isEnabled = isLoading
    swipeRefreshLayout.isRefreshing = isLoading
  }

  private fun makeInputDialog(
      @StringRes title: Int,
      @StringRes hint: Int,
      prefill: String,
      inputType: Int,
      body: String = "",
      callback: (String) -> Unit) {
    MaterialDialog.Builder(requireContext())
        .title(title)
        .content(body)
        .inputType(inputType)
        .input(getString(hint), prefill) { _, input ->
          callback(input.toString())
        }
        .show()
  }
}
