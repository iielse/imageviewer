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
略。

### 更新历史

`1.1.5`
* fix 预览图显示 gif 导致错乱
* 调整回调api

`1.1.4`
* 添加不带imageView的show方法 设置起始位置

`1.1.3`
* 新增-不需要传ImageView的show方法。
* 修复-下拉返回不灵

`1.1.2`

* 修复-图片切换中多指触碰导致页面停滞
* 优化-fling边缘手感
* 优化-更积极的释放内存 `detachAffirmative` 水有点小深 /小纠结 请尽量使用helper操作
* 新增-gif支持(原始图需为静态) demo5

`1.1.1`

* 修复-自定义LoadingUI位置错误。
* 修复-点击图片回调返回参数错误。
* 优化-实现fling手势，提升手感。

`1.1.0`

* 修复-宽高计算错误导致起始图片位置显示错误。
* 优化-取消了无意义的旋转，提升下拽体验(放大且图片已显示顶端时亦可下拽)。
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
    implementation 'com.github.iielse:ImageWatcher:x.y.z'  
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

### 初始化配置

`Activity.onCreate()`
```
ImageWatcherHelper iwHelper = ImageWatcherHelper.with(this, new SimpleLoader());  // SimpleLoader demo中有简单实现
```
`Activity.onBackPressed()`
```
if (!iwHelper.handleBackPressed()) {
    super.onBackPressed();
}
```

### 使用

```
ImageView clickedImage = 被点击的ImageView;
SparseArray<ImageView> mapping = new SparseArray<>(); // 这个请自行理解，
mapping.put(0, clickedImage);
List<Uri> dataList = 被显示的图片们;

iwHelper.show(clickedImage, mapping, dataList);
```


具体看源码demo示例。项目可运行。

帆迎提出问题/想法。

楼主也许可能会更新，比如这次 /斜眼笑。

