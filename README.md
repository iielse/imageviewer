# ImageWatcher
大图查看，它能够
* 动画顺畅切换到查看状态，同样动画顺畅退出查看界面
* 左右滑动多图查看
* 仿微信下拽退出

![image](https://github.com/iielse/DemoProjects/blob/master/previews/111.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/previews/222.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/previews/333.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/previews/444.gif)

### 示例下载
在 previews文件夹下 app-debug.apk
[app-debug.apk](https://github.com/iielse/DemoProjects/blob/master/previews/app-debug.apk)


对比之前`1.0.3`,

* 修复-宽高计算错误导致起始图片位置显示错误。
* 优化-取消了无意义的旋转，提示下拽体验(放大且图片已显示顶端时亦可下拽)。
* 优化-支持显示本地图片。
* 新增-支持长图显示(beta)。 使用的网络图片，被屏蔽了请自己换地址，或提醒我。
* 新增-自定义loadingUI
* 新增-自定义indexUI


#### 集成

[![](https://jitpack.io/v/iielse/ImageWatcher.svg)](https://jitpack.io/#iielse/ImageWatcher)

Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency
```
dependencies {
    implementation 'com.github.iielse:ImageWatcher:1.1.0'
}
```

### 初始化API简介
| name | description |
|:---|:---|
| *setLoader | *图片地址加载的实现者 |
| setTranslucentStatus | 当没有使用透明状态栏，传入状态栏的高度 |
| setErrorImageRes| 图片加载失败时显示的样子 |
| setOnPictureLongPressListener | 长按回调 |
| setIndexProvider | 自定义页码UI |
| setLoadingUIProvider | 自定义加载UI |
| setOnStateChangedListener | 开始显示和退出显示时的回调 |
| isOpened | 初始化默认状态 |

### 初始化配置

`Activity.onCreate()`
```
vImageWatcher = ImageWatcherHelper.with(this) // 一般来讲，ImageWatcher尺寸占据全屏
    .setLoader(new GlideImageWatcherLoader()) // demo中有简单实现
    .setIndexProvider(new DotIndexProvider()) // 自定义
    .create();
```
`Activity.onBackPressed()`
```
if (!vImageWatcher.handleBackPressed()) {
    super.onBackPressed();
}
```

### 使用

```
ImageView clickedImage = 被点击的ImageView;
SparseArray<ImageView> mapping = new SparseArray<>(); // 这个请自行理解，
mapping.put(0, clickedImage);
List<Uri> dataList = 被显示的图片们;

vImageWatcher.show(clickedImage, mapping, dataList);
```


具体看源码demo示例。项目可运行。

欢迎提出问题/想法。

楼主也许可能会更新，比如这次 /斜眼笑。

