package ch.ielse.demo.p05.api.interceptor

import android.content.Context
import ch.ielse.demo.p05.App
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This Interceptor add all received Cookies to the app DefaultPreferences.
 * Your implementation on how to save the Cookies on the Preferences MAY VARY.
 *
 *
 * Created by tsuharesu on 4/1/15.
 */
class ReceivedCookiesInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = originalResponse.headers("Set-Cookie").toSet()
            App.get().getSharedPreferences("api", Context.MODE_PRIVATE).edit()
                    .putStringSet("PREF_COOKIES", cookies)
                    .apply()
        }
        return originalResponse
    }
}