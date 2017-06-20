package ch.ielse.demo.p05.api.interceptor

import ch.ielse.demo.p05.Const
import ch.ielse.demo.p05.hasChinese
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 为请求数据包添加包含设备信息的Headers
 */
class AddDeviceInfoInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder().apply {
            addHeader("OS", "Android ${android.os.Build.VERSION.RELEASE}")
            addHeader("Version", Const.appVersionCode.toString())
            if (!android.os.Build.BRAND.hasChinese()) {
                addHeader("Brand", "${android.os.Build.BRAND} ${if (!android.os.Build.MODEL.hasChinese()) android.os.Build.MODEL else ""}")
            }
        }
        Const.logd("request add Head [OS-Android ${android.os.Build.VERSION.RELEASE}][Version-${Const.appVersionCode}][Brand-${android.os.Build.BRAND}]")
        return chain.proceed(requestBuilder.build())
    }
}
