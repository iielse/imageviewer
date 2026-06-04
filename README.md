# ImageViewer

一个面向 Android 的图片/视频预览库，支持从缩略图到原图的平滑过渡动画，并兼容普通图、长图、GIF、视频和分页数据源。

## 特性

- 缩略图到大图的无缝过渡动画
- 普通图缩放、拖拽浏览
- 长图分块加载
- 视频预览与播放控制
- 下拉/上滑拖拽关闭
- 分页数据加载
- 预览数据删除
- 预览页 UI 自定义扩展
- RTL 适配

## 效果图

![ImageViewer Demo](https://github.com/iielse/res/blob/master/imageviewer/1.gif)

## 引入

JitPack: [![](https://jitpack.io/v/iielse/imageviewer.svg)](https://jitpack.io/#iielse/imageviewer)

```gradle
implementation("com.github.iielse:imageviewer:x.y.z")
```

## 运行环境

- `minSdk 23`
- `compileSdk 36`
- `Java 17`

## 快速开始

最简单的调用方式：

```kotlin
fun show(view: View) {
    val dataList: List<Photo> = TODO()
    val clickedData: Photo = TODO()

    val builder = ImageViewerBuilder(
        context = view.context,
        dataProvider = SimpleDataProvider(clickedData, dataList),
        imageLoader = SimpleImageLoader(),
        transformer = SimpleTransformer(),
    )

    builder.show()
}
```

### `ImageLoader` 示例

普通图使用 Glide，长图先下载到本地文件再交给 `SubsamplingScaleImageView`：

```kotlin
class SimpleImageLoader : ImageLoader {
    override fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val url = (data as? MyData)?.url ?: return
        Glide.with(view)
            .load(url)
            .placeholder(view.drawable)
            .into(view)
    }

    override fun load(
        subsamplingView: SubsamplingScaleImageView,
        data: Photo,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val url = (data as? MyData)?.url ?: return
        val cache = subsamplingCache.get(url)

        if (cache != null) {
            subsamplingView.setImage(cache)
            return
        }

        subsamplingView.lifecycleOwner.lifecycleScope.launch {
            try {
                findLoadingView(viewHolder)?.visibility = View.VISIBLE
                val file = subsamplingDownloadRequest(url)
                subsamplingView.setImage(
                    ImageSource.uri(Uri.fromFile(file)).also { source ->
                        subsamplingCache.put(url, source)
                    }
                )
            } catch (e: Throwable) {
                toast(e.message)
            } finally {
                findLoadingView(viewHolder)?.visibility = View.GONE
            }
        }
    }

    private suspend fun subsamplingDownloadRequest(url: String): File =
        withContext(Dispatchers.IO) {
            Glide.with(appContext).downloadOnly().load(url).submit().get()
        }
}
```

说明：

- `subsamplingDownloadRequest()` 现在是协程挂起函数，不再依赖 RxJava。
- 长图下载逻辑建议绑定生命周期，避免页面销毁后继续回调 UI。

### `Transformer` 示例

`Transformer` 用于把缩略图和预览页建立“配对关系”，决定过渡动画的起点和终点。

```kotlin
class SimpleTransformer : Transformer {
    override fun getView(key: Long): ImageView? = provide(key)

    companion object {
        private val transition = HashMap<ImageView, Long>()

        fun put(photoId: Long, imageView: ImageView) {
            require(isMainThread())
            if (!imageView.isAttachedToWindow) return

            imageView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) = Unit

                override fun onViewDetachedFromWindow(v: View) {
                    transition.remove(imageView)
                    imageView.removeOnAttachStateChangeListener(this)
                }
            })

            transition[imageView] = photoId
        }

        private fun provide(photoId: Long): ImageView? {
            transition.keys.forEach {
                if (transition[it] == photoId) return it
            }
            return null
        }
    }
}
```

到这里，基础接入已经完成。

## 进阶使用

你可以在 `builder` 上追加 3 类自定义能力：

- 自定义每一页上的 UI  
  例如：更多信息、保存、分享、操作按钮  
  `builder.setVHCustomizer(MyCustomViewHolderUI())`

- 自定义覆盖层 UI  
  例如：指示器、顶部栏、底部工具栏  
  `builder.setOverlayCustomizer(MyCustomIndicatorUI())`

- 监听预览状态变化  
  例如：翻页、拖拽、过渡动画、视频播放状态  
  `builder.setViewerCallback(MyViewerStateChangedListener())`

### `ViewerCallback`

```kotlin
interface ViewerCallback : ImageViewerAdapterListener {
    override fun onInit(viewHolder: RecyclerView.ViewHolder) {}
    override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {}
    override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {}
    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {}

    fun onPageScrollStateChanged(state: Int) {}
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    fun onPageSelected(position: Int, viewHolder: RecyclerView.ViewHolder) {}
}
```

## 参数配置

一般情况下不需要调整，但以下参数可按业务需要覆盖：

属性 | 说明
--- | ---
`OFFSCREEN_PAGE_LIMIT` | Viewer 预加载页数
`VIEWER_ORIENTATION` | Viewer 滑动方向
`VIEWER_BACKGROUND_COLOR` | 预览背景色
`DURATION_TRANSITION` | 过渡动画时长
`DURATION_BG` | 背景动画时长
`SWIPE_DISMISS` | 是否支持拖拽关闭
`SWIPE_TOUCH_SLOP` | 拖拽触摸阈值
`DISMISS_FRACTION` | 拖拽关闭判定阈值
`TRANSITION_OFFSET_Y` | 透明状态栏场景下的过渡位置修正

## 数据源定义

```kotlin
interface Photo {
    fun id(): Long
    fun itemType(): @ItemType.Type Int
}
```

说明：

- `id()` 必须唯一，用于分页定位和过渡动画映射。
- `itemType()` 决定当前数据是普通图、长图还是视频。

## FAQ

### 如何手动关闭预览页？

通过：

```kotlin
ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java)
```

获取 `viewer` 的动作控制器后，可以调用：

- `setCurrentItem(pos: Int)` 切换到指定位置
- `dismiss()` 关闭预览
- `remove(item: List<Photo>)` 删除指定元素

### 如何展示视频？

可直接参考 demo 中的实现：

- `SimpleViewerCustomizer`
- `SimpleImageLoader`
- `ExoVideoView`

### 为什么没有过渡动画？

通常是 `Transformer` 配置不正确。要保证：

- `getView()` 能返回当前缩略图对应的 `ImageView`
- 缩略图与预览页使用相同的 `photoId`

### 为什么过渡动画和原图位置有偏差？

注意状态栏/沉浸式布局的影响，必要时调整：

```kotlin
Config.TRANSITION_OFFSET_Y
```

## Demo 说明

仓库内自带 demo，包含以下能力示例：

- 普通图/长图/视频
- 分页预览
- 自定义覆盖层
- 自定义每页 UI
- 沉浸式 DialogFragment

## 备注

- 当前 demo 已迁移到 `Paging 3`
- Viewer 动作事件已改为 `SharedFlow`
- 视频能力已迁移到 `AndroidX Media3`
