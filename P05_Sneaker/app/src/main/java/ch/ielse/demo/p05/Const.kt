package ch.ielse.demo.p05

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import ch.ielse.demo.p05.App
import ch.ielse.demo.p05.view.AlertDialog
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern


object Const {
    val DEBUG: Boolean
        get() {
            return App.get().debug
        }

    val LOG = true
    val TAG = "xx"

    val serverUrl: String
        get() {
            val releaseUrl = ""
            val debugUrl = ""
            return if (DEBUG) debugUrl else releaseUrl
        }


    fun logd(text: String, vararg args: Any) {
        var content = text
        if (LOG && !TextUtils.isEmpty(content)) {
            if (args.size > 0) content = String.format(content, *args)
            Log.i(TAG, content)
        }
    }

    fun loge(text: String, vararg args: Any) {
        var content = text
        if (LOG && !TextUtils.isEmpty(content)) {
            if (args.size > 0) content = String.format(content, *args)
            Log.e(TAG, content)
        }
    }

    fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(App.get(), text, duration).show()
    }

    fun spWrite(fileName: String, key: String, value: Any) {
        val sp = App.get().getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Double -> editor.putString(key, value.toDouble().toString())
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> throw IllegalArgumentException("spWrite unhandled class type $value")
        }
        editor.apply()
    }

    fun spReadString(fileName: String, key: String, defaultValue: String = ""): String =
            App.get().getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, defaultValue)

    fun spReadInt(fileName: String, key: String, defaultValue: Int = 0): Int {
        return App.get().getSharedPreferences(fileName, Context.MODE_PRIVATE).getInt(key, defaultValue)
    }

    fun spReadBoolean(fileName: String, key: String, defaultValue: Boolean = false): Boolean {
        return App.get().getSharedPreferences(fileName, Context.MODE_PRIVATE).getBoolean(key, defaultValue)
    }

    fun hasNetwork(): Boolean {
        val connectivity = App.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (null != info && info.isConnected) {
            if (info.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    fun isWifiEnvironment(): Boolean {
        val connectivity = App.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (null != info) {
            return ConnectivityManager.TYPE_WIFI == connectivity.activeNetworkInfo.type
        }
        return false
    }

    val appProcessName: String?
        get() {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(FileReader("/proc/" + android.os.Process.myPid() + "/cmdline"))
                return reader.readLine()?.trim()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            } finally {
                try {
                    reader?.close()
                } catch (ignored: IOException) {
                }
            }
            return null
        }


    val appVersionCode: Int
        get() {
            try {
                val packInfo = App.get().packageManager.getPackageInfo(App.get().packageName, 0)
                return packInfo.versionCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return -1
        }


    fun dp2px(dp: Float): Int {
        val dm = DisplayMetrics()
        (App.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.getMetrics(dm)
        return (dp * dm.density + 0.5f).toInt()
    }

    /**
     * 判断颜色所属风格是否为暗色风格
     */
    fun isDarkColorTheme(styleColor: Int): Boolean {
        val r = Color.red(styleColor)
        val g = Color.green(styleColor)
        val b = Color.blue(styleColor)
        val grayLevel = r * 0.299f + g * 0.587f + b * 0.114f
        return grayLevel < 192 // default -> 192
    }

    /**
     * 状态栏内容颜色
     */
    fun setStatusBarDarkTheme(darkTheme: Boolean, activity: Activity): Boolean {
        if ("Xiaomi".equals(Build.BRAND, ignoreCase = true)) {
            val clazz = activity.window.javaClass
            try {
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                var darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                extraFlagField.invoke(activity.window, if (darkTheme) darkModeFlag else 0, darkModeFlag)
                return true
            } catch (e: Exception) {
                loge("Xiaomi setStatusBarDarkIcon: failed")
                return false
            }

        } else if ("MeiZu".equals(Build.BRAND, ignoreCase = true)) {
            val window = activity.window
            if (window != null) {
                try {
                    val lp = window.attributes
                    val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                    val meiZuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                    darkFlag.isAccessible = true
                    meiZuFlags.isAccessible = true
                    val bit = darkFlag.getInt(null)
                    var value = meiZuFlags.getInt(lp)
                    if (darkTheme) {
                        value = value or bit
                    } else {
                        value = value and bit.inv()
                    }
                    meiZuFlags.setInt(lp, value)
                    window.attributes = lp
                    return true
                } catch (e: Exception) {
                    loge("MeiZu setStatusBarDarkIcon: failed")
                    return false
                }

            }
        }
        return false
    }

    val statusBarHeight: Int
        get() {
            var statusHeight = -1
            try {
                val clazz = Class.forName("com.android.internal.R\$dimen")
                val `object` = clazz.newInstance()
                val height = Integer.parseInt(clazz.getField("status_bar_height").get(`object`).toString())
                statusHeight = App.get().resources.getDimensionPixelSize(height)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return statusHeight
        }
}

fun String.hasChinese(): Boolean {
    return Pattern.compile("[\u4e00-\u9fa5]").matcher(this).find()
}

fun Activity.hideSoftKeyboard() {
    val view = window.peekDecorView()
    if (view != null) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


fun Activity.handlePermissionRequestResult(permission: com.tbruyelle.rxpermissions2.Permission): Boolean {
    if (permission.granted) {
        // `permission.name` is granted !
        Const.logd("${permission.name} granted !")
        return true
    } else if (permission.shouldShowRequestPermissionRationale) {
        // Denied permission without ask never again
        Const.logd("${permission.name} denied without never ask again !")
    } else {
        // Denied permission with ask never again
        // Need to go to the settings
        Const.loge("${permission.name} denied never ask again !")
        val deniedPermissionName: String?
        when (permission.name) {
            Manifest.permission.ACCESS_COARSE_LOCATION -> deniedPermissionName = "位置信息"
            Manifest.permission.CAMERA -> deniedPermissionName = "相机"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> deniedPermissionName = "存储空间"
            Manifest.permission.READ_CONTACTS -> deniedPermissionName = "通讯录"
            Manifest.permission.CALL_PHONE -> deniedPermissionName = "电话"
            Manifest.permission.RECORD_AUDIO -> deniedPermissionName = "麦克风"
            else -> deniedPermissionName = "一些"
        }

        AlertDialog.Builder(this).setTitle("权限提示")
                .setMessage("我们需要的 $deniedPermissionName 权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则部分功能无法正常使用！")
                .setPositiveButton("去设置", { dialog, _ ->
                    dialog.dismiss()
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", App.get().packageName, null)
                        startActivity(this)
                    }
                })
                .setNegativeButton("取消", { dialog, _ ->
                    dialog.dismiss()
                })
                .create().show()
    }
    return false
}