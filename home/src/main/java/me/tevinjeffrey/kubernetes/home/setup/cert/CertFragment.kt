package me.tevinjeffrey.kubernetes.home.setup.cert

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_cert.*
import me.tevinjeffrey.kubernetes.base.extensions.clicksWithThrottle
import me.tevinjeffrey.kubernetes.base.extensions.observe
import me.tevinjeffrey.kubernetes.base.support.BaseFragment
import me.tevinjeffrey.kubernetes.home.R

class CertFragment : BaseFragment() {
  private val viewModel by lazy<CertViewModel> { viewModel() }

  override fun layoutId() = R.layout.fragment_cert

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    observe(viewModel.clientCertResult) {
      clientKeyBtn.isEnabled = it!!
    }

    observe(viewModel.clientKeyResult) {
      clusterCACrtBtn.isEnabled = it!!
    }


    observe(viewModel.clusterCACertResult) {

    }

    clientKeyBtn.isEnabled = false
    clusterCACrtBtn.isEnabled = false

    clientCrtBtn.clicksWithThrottle {
      performFileSearch(CLIENT_CERT_REQUEST_CODE)
    }

    clientKeyBtn.clicksWithThrottle {
      performFileSearch(CLIENT_KEY_REQUEST_CODE)
    }

    clusterCACrtBtn.clicksWithThrottle {
      performFileSearch(CLUSTER_CA_REQUEST_CODE)
    }
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
