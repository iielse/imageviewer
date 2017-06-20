package ch.ielse.demo.p05.api.interceptor

import android.content.Context
import ch.ielse.demo.p05.App
import ch.ielse.demo.p05.Const
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences MAY VARY.
 *
 *
 * Created by tsuharesu on 4/1/15.
 */
class AddCookiesInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val preferences = App.get().getSharedPreferences("api", Context.MODE_PRIVATE)
                .getStringSet("PREF_COOKIES", HashSet<String>()) as HashSet<String>
        preferences.forEach { cookie ->
            builder.addHeader("Cookie", cookie)
            Const.logd("OkHttp Adding Header: " + cookie) // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }
        return chain.proceed(builder.build())
    }
}