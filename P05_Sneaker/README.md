#Sneaker

#简单使用
![image](https://github.com/iielse/DemoProjects/blob/master/P05_Sneaker/previews/222.gif)

```
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
```

`Const.setStatusBarDarkTheme` 可以设置状态栏内容颜色风格 (仅支持小米和魅族) 。如果有更好的方案，请怒戳我，真心求带


#状态变化
![image](https://github.com/iielse/DemoProjects/blob/master/P05_Sneaker/previews/111.gif)

```
sneaker = Sneaker.with(activity).setMessage(message)
                .setLoading(true)
                .create()
sneaker!!.show()
```

初始可以调用 `setLoading(true)` 一般用于网络等延时性质请求

之后调用`notifyStateChanged(@DrawableRes final int resultResId, final String message)` 变化状态


注：
```
Sneaker.setDisplayConfigs(getApplicationContext(),
                R.dimen.edge, R.color.normal, R.dimen.normal, R.dimen.title_height);
```
最好在`Application`初始化的时候调用`setDisplayConfigs` 设置Sneaker的一些UI细节


可以具体看源码demo，这个项目应该是可以运行的，这个项目应该是可以运行的，这个项目应该是可以运行的


#特别感谢
https://github.com/Hamadakram/Sneaker