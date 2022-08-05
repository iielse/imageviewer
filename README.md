# Imageviewer

提供查看缩略视图到原视图的无缝过渡转变的视觉效果，优雅的浏览普通图、长图、动图.

#### 主要特征

- **过渡动画** 缩略图到大图或大图到缩略图时提供无缝衔接动画
- **浏览手势** 浏览大图时可使用常势操用手.如缩放图片等.（[PhotoView](https://github.com/chrisbanes/PhotoView)）
- **超大图** 图片区块加载 （[SubsamplingScaleImageView](https://github.com/davemorrissey/subsampling-scale-image-view)）
- **Video** 支持Video加载 ([ExoPlayer](https://github.com/google/ExoPlayer))
- **拖拽关闭** 对大图进行上/下滑操作退出浏览.
- **数据分页加载** 在浏览大图的情况下异步加载数据.
- **数据删除**
- **自定义UI** 对预览页的UI元素自定义追加
- **已适配RTL**

![](https://github.com/iielse/res/blob/master/imageviewer/1.gif)

### 引入 [![](https://jitpack.io/v/iielse/imageviewer.svg)](https://jitpack.io/#iielse/imageviewer)

```
implementation 'com.github.iielse:imageviewer:x.y.z' 
```

### 最简单的调用代码

```
fun show() { //
    val dataList： List<Photo> = // 将要展示的图片集合列表
    val clickedData: Photo = // 被点击的其中的那个图片元素信息
    val builder = ImageViewerBuilder(
        context = view.context,
        dataProvider = SimpleDataProvider(clickedData, dataList), // 一次性全量加载 // 实现DataProvider接口支持分页加载
        imageLoader = SimpleImageLoader(), // 可使用demo固定写法 // 实现对数据源的加载.支持自定义加载数据类型，加载方案
        transformer = SimpleTransformer(), // 可使用demo固定写法 // 以photoId为标示，设置过渡动画的'配对'.
    )
    builder.show()
}
```

```
// 基本是固定写法. Glide 可以换成别的. demo代码中有video的写法.
class SimpleImageLoader : ImageLoader {
    /** 根据自身photo数据加载图片.可以使用其它图片加载框架. */
    override fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val it = (data as? MyData?)?.url ?: return
        Glide.with(view).load(it)
                .placeholder(view.drawable)
                .into(view)
    }

    /**
     * 根据自身photo数据加载超大图.subsamplingView数据源需要先将内容完整下载到本地.
     */
    override fun load(subsamplingView: SubsamplingScaleImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val it = (data as? MyData?)?.url ?: return
        subsamplingDownloadRequest(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { findLoadingView(viewHolder)?.visibility = View.VISIBLE }
                .doFinally { findLoadingView(viewHolder)?.visibility = View.GONE }
                .doOnNext { subsamplingView.setImage(ImageSource.uri(Uri.fromFile(it))) }
                .doOnError { toast(it.message) }
                .subscribe().bindLifecycle(subsamplingView)
    }

    private fun subsamplingDownloadRequest(url: String): Observable<File> {
        return Observable.create {
            try {
                it.onNext(Glide.with(appContext).downloadOnly().load(url).submit().get())
                it.onComplete()
            } catch (e: java.lang.Exception) {
                if (!it.isDisposed) it.onError(e)
            }
        }
    }

    private fun findLoadingView(viewHolder: RecyclerView.ViewHolder): View? {
        return viewHolder.itemView.findViewById<ProgressBar>(R.id.loadingView)
    }

    ......
}
```

```
// 基本是可以作为固定写法.
class SimpleTransformer : Transformer {
    override fun getView(key: Long): ImageView? = provide(key)
    
    companion object {
        private val transition = HashMap<ImageView, Long>()
        fun put(photoId: Long, imageView: ImageView) {
            require(isMainThread())
            if (!imageView.isAttachedToWindow) return
            imageView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(p0: View?) = Unit
                override fun onViewDetachedFromWindow(p0: View?) {
                    transition.remove(imageView)
                    imageView.removeOnAttachStateChangeListener(this)
                }
            })
            transition[imageView] = photoId
        }

        private fun provide(photoId: Long): ImageView? {
            transition.keys.forEach {
                if (transition[it] == photoId)
                    return it
            }
            return null
        }
    }
} 
```

## 到此简单的集成已经完毕.

---

## 进阶使用.

（组合实现以下3个方法.可以追加自定义的展示和功能）

* // 自定义'每一页'上的UI.比如可显示图片的更多信息.提供存储分享等更多功能等
* `builder.setVHCustomizer(MyCustomViewHolderUI())`
* // 自定义'覆盖(最上)层'上的UI.比如添加指示器等
* `builder.setOverlayCustomizer(MyCustomIndicatorUI())`
* // 监听viewer的各种状态变化.包括页面的切换(显示当前在第几页).；过渡动画的执行状态；维护video的播放状态等
* `builder.setViewerCallback(MyViewerStateChangedListener())`

```
// 一般监听翻页onPageSelected可以控制 video播放的状态
// viewer 各状态监听回调
interface ViewerCallback : ImageViewerAdapterListener {
    // 当点击缩略图变化大图的瞬间
    override fun onInit(viewHolder: RecyclerView.ViewHolder) {}
    // 当图片被拖动时
    override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {}
    // 当图片被拖动但不至于退出浏览
    override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {}
    // 当图片被拖动执行退出浏览
    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {}
    // 翻页中状态变化
    fun onPageScrollStateChanged(state: Int) {}
    // 翻页中
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    // 当某大图页面被选中
    fun onPageSelected(position: Int, viewHolder: RecyclerView.ViewHolder) {}
}
```

#### 参数配置. 一般不用调整

属性  | 作用说明
------------- | -------------
OFFSCREEN_PAGE_LIMIT  | viewer预加载条数
VIEWER_ORIENTATION  | viewer滑动方向
VIEWER_BACKGROUND_COLOR  | 大图预览时背景色(默认纯黑)
DURATION_TRANSITION  | 过渡动画时长
DURATION_BG  | 过渡动画背景变化时长
SWIPE_DISMISS  | 是否支持拖拽返回
SWIPE_TOUCH_SLOP | 拖拽触摸感知阈值
DISMISS_FRACTION  | 拖拽返回边界阈值
TRANSITION_OFFSET_Y | 修正透明状态栏下过渡动画的起始位置

### 数据源的定义

```
interface Photo {
    fun id(): Long // 每条图片数据的唯一标示. 主要用于分页数据加载. 定位过渡动画的对应关系
    fun itemType(): @ItemType.Type Int // 是否启用SubsamplingScaleImageView实现图片区块加载或ExoVideoView实现Video加载
}
```

### FAQ

- 如何手动关闭退出整个页面？
- 如何删除一条数据？

通过 `ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java)`获取`viewer` 对象引用.
之后可使用 `setCurrentItem(pos: Int)`切换大图位置到指定位置; `dismiss()`退出浏览大图; `remove(item: List<Photo>)`删除其中的元素

- 如何实现Video的展示？
  可参考demo实现 demo代码位置 `SimpleViewerCustomizer`
- 为什么没有过渡动画？
  需要正确的配置 `Transformer`。需要保证`getView` 返回不为null.
- 为什么动画的执行和原图有高度偏差？
  注意状态栏的影响。配置`Config.TRANSITION_OFFSET_Y`

### 其它重要说明

demo可运行. demo可运行. demo可运行 .demo代码已重构.

都看到这里了，不点下`Star`吗 [旺柴]

### Thanks

#### 如果您觉得我的开源库帮你节省了大量的开发时间，可扫描下方的二维码随意打赏。你的鼓励是我维护项目最大的动力

<img src="https://raw.githubusercontent.com/iielse/res/master/q/ali_q.png" alt="支付宝捐赠" width="320"><img src="https://raw.githubusercontent.com/iielse/res/master/q/wx_q.png" alt="微信捐赠" width="320">


