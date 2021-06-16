# Imageviewer
提供查看缩略视图到原视图的无缝过渡转变的视觉效果，优雅的浏览普通图、长图、动图.

#### 主要功能

- **过渡动画** 缩略图到大图或大图到缩略图时提供无缝衔接动画
- **浏览手势** 浏览大图时可使用常势操用手.如缩放图片等.（[PhotoView](https://github.com/chrisbanes/PhotoView)）
- **超大图** 图片区块加载.避免OOM（[SubsamplingScaleImageView](https://github.com/davemorrissey/subsampling-scale-image-view)）
- **Video** 支持Video加载 ([SimpleExoPlayer](https://github.com/google/ExoPlayer))
- **拖拽关闭** 对大图进行上/下滑操作退出浏览.
- **数据分页加载** 在浏览大图的情况下异步加载百万数据.
- **自定义UI** 对预览页的UI元素自定义追加

![](https://github.com/iielse/res/blob/master/imageviewer/1.gif)

#### 使用
```
implementation 'com.github.iielse:imageviewer:x.y.z'
```
```
val builder = ImageViewerBuilder(
    context = this,
    initKey = photo.id, // 用于定位被点击缩略图变化大图后初始化所在位置.以此来执行过渡动画.
    dataProvider = MyDataProvider(), // 浏览数据源的提供者.支持一次性给全数据或分页加载.
    imageLoader = MyImageLoader(), // 实现对数据源的加载.支持自定义加载数据类型，加载方案
    transformer = MyTransformer() // 以photoId为标示，设置过渡动画的'配对'.
)
builder.setVHCustomizer(MyCustomViewHolderUI()) // 自定义每一页上的UI.比如可显示图片的更多信息.提供存储分享等更多功能等
builder.setOverlayCustomizer(MyCustomIndicatorUI()) // 自定义'覆盖(最上)层'上的UI.比如添加指示器等
builder.setViewerCallback(MyViewerStateChangedListener()) // 监听viewer的各种状态变化.包括页面的切换.以及过渡动画的执行状态
builder.show()
```

#### 主要接口
```
interface Photo {
    fun id(): Long // 每条图片数据的唯一标示. 主要用于分页数据加载. 定位过渡动画的对应关系
    fun itemType(): @ItemType.Type Int // 是否启用SubsamplingScaleImageView实现图片区块加载或ExoVideoView实现Video加载
}
```

```
class MyDataProvider() : DataProvider {
    override fun loadInitial(): List<Photo> {
        return listOf() // 返回查看大图对应的数据源.若不需要分页可再次一次性返回所有数据源.
    }

    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
        // 根据最后一条图片数据id. 可进行(服务器/本地db)请求访问后续数据内容集合
        Api.asyncQueryAfter(key) { result ->
            callback(result)
        }
    }

    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
        // 根据最初一条图片数据id. 可进行(服务器/本地db)请求访问之前数据内容集合
        Api.asyncQueryBefore(key) { result ->
            callback(result)
        }
    }
}
```

```
class MyImageLoader : ImageLoader {
    /**
     * 根据自身photo数据加载图片.可以使用其它图片加载框架.
     */
    override fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val it = (data as? MyData?)?.url ?: return
        Glide.with(view).load(it)
                .override(view.width, view.height)
                .placeholder(view.drawable)
                .into(view)
    }

    /**
     * 根据自身photo数据加载超大图.subsamplingView数据源需要先将内容完整下载到本地.需要注意生命周期
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
}
```
```
class MyTransformer(private val pageKey: String) : Transformer {
    override fun getView(key: Long): ImageView? = TransitionViewsRef.provideTransitionViewsRef(pageKey)[key]
}

/**
 * 维护Transition过渡动画的缩略图和大图之间的映射关系. 需要在Activity/Fragment释放时刻.清空此界面的View引用
 * (这里是比较随便的一种写法.没有说必须这样写.大家可以用更好的写法.谢谢)
 */
object TransitionViewsRef {
    private val map = mutableMapOf<String, LongSparseArray<ImageView>?>() // 可能有多级页面
    const val KEY_MAIN = "page_main"

    fun provideTransitionViewsRef(key: String): LongSparseArray<ImageView> {
        return map[key] ?: LongSparseArray<ImageView>().also { map[key] = it }
    }

    // invoke when activity onDestroy or fragment onDestroyView
    fun releaseTransitionViewRef(key: String) {
        map[key] = null
    }
}
```
```
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
#### 参数配置

属性  | 作用说明
------------- | -------------
DEBUG  | 调试日志开关
OFFSCREEN_PAGE_LIMIT  | viewer预加载条数
VIEWER_ORIENTATION  | viewer滑动方向
VIEWER_BACKGROUND_COLOR  | 大图预览时背景色(默认纯黑)
DURATION_TRANSITION  | 过渡动画时长
DURATION_BG  | 过渡动画背景变化时长
SUBSAMPLING_SCALE_TYPE  | 大图初始化加载模式
SWIPE_DISMISS  | 是否支持拖拽返回
SWIPE_TOUCH_SLOP | 拖拽触摸感知阈值
DISMISS_FRACTION  | 拖拽返回边界阈值
TRANSITION_OFFSET_Y | 修正透明状态栏下过渡动画的起始位置

#### 方法调用
注：通过 `ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java)`获取viewer 操作对象引用

方法  | 作用说明
------------- | -------------
`setCurrentItem(pos: Int)`  | 切换大图位置到指定位置
`dismiss()`  | 退出浏览大图

#### 其它说明
已适配RTL布局
Video加载请参考demo
demo可运行.demo代码已重构.具备良好可读性.放心食用

都看到这里了，不点下`Star`吗 [旺柴]