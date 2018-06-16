package me.tevinjeffrey.kubernetes.home.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.annotation.StringRes
import androidx.recyclerview.widget.SimpleItemAnimator
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings.*
import me.tevinjeffrey.kubernetes.base.extensions.observe
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.home.R
import me.tevinjeffrey.kubernetes.home.settings.adapter.CertItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.EndpointItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.HeaderItem
import me.tevinjeffrey.kubernetes.home.settings.adapter.ToggleItem

class SettingsFragment : BaseFragment() {

  private val adapter = GroupAdapter<ViewHolder>()

  private val certViewModel by lazy<CertViewModel> { viewModel() }
  private val endpointViewModel by lazy<EndpointViewModel> { viewModel() }

  override fun layoutId() = R.layout.fragment_settings

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    list.adapter = adapter
    (list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    toolbar.title = getString(R.string.settings)
    val launchDialog = setupEndpoint()
    setupCertificates()

    adapter.setOnItemClickListener { item, _ ->
      when (item) {
        is CertItem -> {
          performFileSearch(item.requestCode)
        }
        is EndpointItem -> {
          launchDialog(item)
        }
      }
    }
  }

  private fun setupEndpoint(): (EndpointItem) -> Unit {
    val masterUrlItem = EndpointItem(getString(R.string.master_url))
    val proxyUrlItem = EndpointItem(getString(R.string.proxy_url))

    val proxyToggle = ToggleItem("Proxy Connections", {
      endpointViewModel.updateShouldProxy(it)
    })

    val endpointSection = Section()
    endpointSection.setHeader(HeaderItem(getString(R.string.endpoint)))
    endpointSection.addAll(listOf(masterUrlItem, proxyToggle, proxyUrlItem))
    adapter.add(endpointSection)

    observe(endpointViewModel.masterUrlResult) {
      masterUrlItem.body = it.orEmpty()
      masterUrlItem.notifyChanged()
    }

    observe(endpointViewModel.proxyUrlResult) {
      proxyUrlItem.body = it.orEmpty()
      masterUrlItem.notifyChanged()
    }

    observe(endpointViewModel.shouldProxyResult) {
      proxyUrlItem.isEnabled = it ?: true
      proxyUrlItem.notifyChanged()
      proxyToggle.isChecked = it ?: true
      proxyToggle.notifyChanged()
    }

    observe(endpointViewModel.error) {
      Snackbar.make(view!!, it!!.message.orEmpty(), Snackbar.LENGTH_LONG).show()
    }

    return { item ->
      when (item) {
        masterUrlItem -> {
          makeInputDialog(
              R.string.master_url_title,
              R.string.hint_master_url,
              item.body,
              InputType.TYPE_TEXT_VARIATION_URI) {
            endpointViewModel.updateMasterUrl(it)
          }
        }
        proxyUrlItem -> {
          makeInputDialog(
              R.string.proxy_url,
              R.string.hint_proxy_url,
              item.body,
              InputType.TYPE_TEXT_VARIATION_URI) {
            endpointViewModel.updateProxyUrl(it)
          }
        }
      }
    }
  }

  private fun makeInputDialog(
      @StringRes title: Int,
      @StringRes hint: Int,
      prefill: String,
      inputType: Int,
      callback: (String) -> Unit) {
    MaterialDialog.Builder(requireContext())
        .title(title)
        .inputType(inputType)
        .input(getString(hint), prefill) { _, input ->
          callback(input.toString())
        }
        .show()
  }

  private fun setupCertificates() {
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

    val insecureConnectionsToggle = ToggleItem("Allow insecure connections", {
      certViewModel.updateAllowInsecure(it)
    })

    val certSection = Section()
    certSection.setHeader(HeaderItem(getString(R.string.certificates)))
    certSection.addAll(listOf(insecureConnectionsToggle, clientCertItem, clientKeyItem, clusterCACertItem))

    adapter.add(certSection)

    observe(certViewModel.clientCertResult) {
      updateCertItem(it, clientCertItem)
    }

    observe(certViewModel.clientKeyResult) {
      updateCertItem(it, clientKeyItem)
    }

    observe(certViewModel.clusterCACertResult) {
      updateCertItem(it, clusterCACertItem)
    }

    observe(certViewModel.allowInsecureResult) {
      clientCertItem.isEnabled = !(it ?: true)
      clientCertItem.notifyChanged()
      clientKeyItem.isEnabled = !(it ?: true)
      clientKeyItem.notifyChanged()
      clusterCACertItem.isEnabled = !(it ?: true)
      clusterCACertItem.notifyChanged()
      insecureConnectionsToggle.isChecked = (it ?: true)
      insecureConnectionsToggle.notifyChanged()
    }

    observe(certViewModel.error) {
      Snackbar.make(view!!, it!!.message.orEmpty(), Snackbar.LENGTH_LONG).show()
    }
  }

  private fun updateCertItem(it: FileInfo?, certItem: CertItem) {
    val location = it?.location ?: getString(R.string.not_set)
    certItem.location = location
    certItem.notifyChanged()
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
    }
  }

  private fun performFileSearch(requestCode: Int) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "*/*"
    startActivityForResult(intent, requestCode)
  }

  companion object {
    const val CLIENT_CERT_REQUEST_CODE = 42
    const val CLIENT_KEY_REQUEST_CODE = 43
    const val CLUSTER_CA_REQUEST_CODE = 44
  }
}
