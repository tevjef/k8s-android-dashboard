package me.tevinjeffrey.kubernetes.home.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings.*
import me.tevinjeffrey.kubernetes.base.extensions.observe
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.home.R

class SettingsFragment : BaseFragment() {
  private val viewModel by lazy<SettingsViewModel> { viewModel() }

  override fun layoutId() = R.layout.fragment_settings

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    setupCertificates()
  }

  private fun setupCertificates() {
    val clientCertItem = SettingsItem(
        getString(R.string.client_certificate),
        CLIENT_CERT_REQUEST_CODE
    )

    val clientKeyItem = SettingsItem(
        getString(R.string.client_private_key),
        CLIENT_KEY_REQUEST_CODE
    )

    val clusterCACertItem = SettingsItem(
        getString(R.string.cluster_ca_certificate),
        CLUSTER_CA_REQUEST_CODE
    )

    val certSection = Section()
    certSection.setHeader(HeaderItem(getString(R.string.certificates)))
    certSection.addAll(listOf(clientCertItem, clientKeyItem, clusterCACertItem))

    val adapter = GroupAdapter<ViewHolder>()
    adapter.add(certSection)

    adapter.setOnItemClickListener { item, _ ->
      when (item) {
        is SettingsItem -> {
          performFileSearch(item.requestCode)
        }
      }
    }

    list.adapter = adapter

    observe(viewModel.clientCertResult) {
      updateCertItem(it, clientCertItem)
    }

    observe(viewModel.clientKeyResult) {
      updateCertItem(it, clientKeyItem)
    }

    observe(viewModel.clusterCACertResult) {
      updateCertItem(it, clusterCACertItem)
    }

    observe(viewModel.error) {
      Snackbar.make(view!!, it!!.message.orEmpty(), Snackbar.LENGTH_LONG).show()
    }

    viewModel.loadData()
  }

  private fun updateCertItem(it: FileInfo?, certItem: SettingsItem) {
    val location = it?.location ?: getString(R.string.not_set)
    certItem.location = location
    certItem.notifyChanged()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode to resultCode) {
      CLIENT_CERT_REQUEST_CODE to RESULT_OK -> {
        viewModel.updateClientCert(requireContext(), data?.data ?: return)
      }
      CLIENT_KEY_REQUEST_CODE to RESULT_OK -> {
        viewModel.updateClientKey(requireContext(), data?.data ?: return)
      }
      CLUSTER_CA_REQUEST_CODE to RESULT_OK -> {
        viewModel.updateClusterCa(requireContext(), data?.data ?: return)
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
