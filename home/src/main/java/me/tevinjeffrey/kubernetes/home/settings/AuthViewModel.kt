package me.tevinjeffrey.kubernetes.home.settings

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import me.tevinjeffrey.kubernetes.base.extensions.maybe
import me.tevinjeffrey.kubernetes.base.support.BaseViewModel
import me.tevinjeffrey.kubernetes.base.support.FileOpener
import me.tevinjeffrey.kubernetes.base.support.SingleLiveEvent
import me.tevinjeffrey.kubernetes.db.ConfigDatabase
import me.tevinjeffrey.kubernetes.db.observeClusterValue
import me.tevinjeffrey.kubernetes.home.settings.adapter.TokenInfo
import java.lang.IllegalStateException
import java.nio.charset.Charset
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val fileOpener: FileOpener,
    configDatabase: ConfigDatabase
) : BaseViewModel() {

  val tokenResult: MutableLiveData<TokenInfo> = MutableLiveData()
  val usernameResult: MutableLiveData<String> = MutableLiveData()
  val passwordResult: MutableLiveData<String> = MutableLiveData()

  val error: SingleLiveEvent<Throwable> = SingleLiveEvent()

  private val configDao = configDatabase.configDao()

  init {
    configDatabase.observeClusterValue(tokenResult, { tokenInfo(it.token) })
  }

  fun updateToken(uri: Uri) {
    disposable += findToken(uri)
        .flatMap {
          maybe {
            configDao.updateToken(it.data)
          }
        }
        .subscribeBy(
            onSuccess = { },
            onError = { error.value = it }
        )
  }

  private fun findToken(uri: Uri): Maybe<TokenInfo> {
    return Maybe.create<TokenInfo> {
      val source = fileOpener.openFile(uri)
      val data = source.readString(Charset.defaultCharset())
      source.close()

      val tokenInfo = try {
        tokenInfo(data)
      } catch (e: Exception) {
        null
      }

      if (tokenInfo != null) {
        it.onSuccess(tokenInfo)
      } else {
        it.onError(IllegalStateException("Please select a valid token."))
      }
    }
  }

  private fun tokenInfo(data: String?): TokenInfo? {
    data ?: return null
    if (data.isEmpty()) return null

    val jwt = JWT(data.orEmpty())
    return if (jwt.issuer != null && jwt.subject != null) {
      TokenInfo(data.orEmpty(), jwt.issuer.orEmpty(), jwt.subject.orEmpty())
    } else {
      null
    }
  }
}
