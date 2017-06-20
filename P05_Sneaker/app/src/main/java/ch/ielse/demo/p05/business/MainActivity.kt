package ch.ielse.demo.p05.business

import android.Manifest
import android.os.Bundle
import android.widget.TextView
import ch.ielse.demo.p05.BaseActivity
import ch.ielse.demo.p05.BusEvent
import ch.ielse.demo.p05.R
import ch.ielse.demo.p05.api.ApiManager
import ch.ielse.demo.p05.api.SimpleObserver
import ch.ielse.demo.p05.db.PersonInfo
import ch.ielse.demo.p05.handlePermissionRequestResult
import ch.ielse.demo.p05.view.Sneaker
import com.tbruyelle.rxpermissions2.RxPermissions


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        isTransparentBackground = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val tLogin = findViewById(R.id.t_login)
        tLogin.setOnClickListener {
            val username = "18888888888"
            val password = "123456"
            val userType = "2"
            ApiManager.loginTest(this@MainActivity, username, password, userType, object : SimpleObserver<PersonInfo, PersonInfo>() {
                override fun onSuccess(o: PersonInfo) {
                    (findViewById(R.id.t_result) as TextView).apply {
                        text = "personInfo userId ${o.userId!!}"
                    }
                }
            }.disable(tLogin).sneaker(this@MainActivity, "正在登录中..", "登录成功.."))
        }

        findViewById(R.id.t_camera).setOnClickListener {
            RxPermissions(this@MainActivity).requestEach(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .all { permission -> handlePermissionRequestResult(permission) }
                    .subscribe { allGranted ->
                        if (allGranted!!) {
                            Sneaker.with(this@MainActivity).setMessage("相机所需全部权限已经允许.")
                                    .setIcon(R.mipmap.qrh).setAutoHide(true).create().show()
                        }
                    }
        }
    }

    override fun handleBusEventImpl(event: BusEvent) {
        super.handleBusEventImpl(event)
    }

//    fun setJPushAlias(alias: String) {
//        Observable.interval(0, 60, TimeUnit.SECONDS).flatMap {
//            Observable.create(ObservableOnSubscribe<Int> { e ->
//                JPushInterface.setAliasAndTags(applicationContext, alias, null) { i, _, _ ->
//                    // 0 -> Set tag and alias success
//                    // 6002 -> Failed to set alias and tags due to timeout. Try again after 60 s.
//                    // other -> Failed
//                    Const.logd("JPushInterface.setAliasAndTags callback response code " + i)
//                    e.onNext(i)
//                }
//            })
//        }.takeWhile { code ->
//            6002 == code
//        }.subscribe()
//    }
}
