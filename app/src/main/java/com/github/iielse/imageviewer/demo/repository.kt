package com.github.iielse.imageviewer.demo

import android.os.Handler
import android.os.Looper
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.github.iielse.imageviewer.core.Photo
import kotlin.math.max
import kotlin.math.min

val image = arrayOf(
        // gif
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168009921&di=fe2e9e5bc508130558a9954c2a8bd28f&imgtype=0&src=http%3A%2F%2Fxuexi.leawo.cn%2Fuploads%2Fallimg%2F160926%2F134225K60-2.gif",
        "https://b-ssl.duitang.com/uploads/item/201206/29/20120629140234_QWAsX.thumb.700_0.gif",
        "http://pic2.52pk.com/files/allimg/150324/104923F49-12.jpg",
        "http://imgsrc.baidu.com/forum/w=580/sign=852e30338435e5dd902ca5d746c7a7f5/bd3eb13533fa828b6cf24d82fc1f4134960a5afa.jpg",
        "http://pic.3h3.com/up/2014-3/20143314140858312456.gif",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168118542&di=437ba348dfe4bd91afa5e5761f318cee&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201410%2F17%2F20141017094107_VdNJu.gif",
        "http://s4.sinaimg.cn/bmiddle/b4d49f27tdfe1a9907393&690",
        "http://media.giphy.com/media/4a6cioskxhvuU/giphy-tumblr.gif",
        "https://f12.baidu.com/it/u=3294379970,949120920&fm=72",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168792516&di=b3ae4a05909fd4c6b903851553649fb0&imgtype=0&src=http%3A%2F%2Fwww.people.com.cn%2Fmediafile%2Fpic%2F20171013%2F33%2F16097621616938606825.gif",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168265734&di=6dbf0daade2a0126fa6118ec3a185205&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160917%2Fb8b605c1f286482b8e748f37528ccfd5.jpg",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168279697&di=dcd2b62878ad6c2c92e5bd7facfe6c3c&imgtype=0&src=http%3A%2F%2Fphotocdn.sohu.com%2F20151126%2Fmp44425938_1448498418499_2.gif",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168389150&di=2fd5c826af5394b62777fd132dff7d8f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201701%2F17%2F20170117112406_zixk5.thumb.700_0.gif",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168397716&di=909dcfb1bf7a7fe37041ec5914c34c4a&imgtype=0&src=http%3A%2F%2Fs7.rr.itc.cn%2Fg%2FwapChange%2F20159_11_19%2Fa4m9779610717481352.gif",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168437965&di=f91b9c858eecf75799af00df525eab9a&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F201510_31_11%2Fa6cjhv9612585370352.gif",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168451881&di=805580bb76614eba5dcc4668253b9749&imgtype=0&src=http%3A%2F%2Fs8.rr.itc.cn%2Fr%2FwapChange%2F201510_31_11%2Fa979a69612629324352.gif",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542168465415&di=8c7e9f70a33c4e442427f5e4bd21db1e&cimgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20162_24_14%2Fa69tdw8577863829596.gif",
        // normal
        "http://b162.photo.store.qq.com/psb?/V14EhGon4cZvmh/z2WukT5EhNE76WtOcbqPIgwM2Wxz4Tb7Nub.rDpsDgo!/b/dOaanmAaKQAA",
        "https://images.pexels.com/photos/45170/kittens-cat-cat-puppy-rush-45170.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/145939/pexels-photo-145939.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
        "https://images.unsplash.com/photo-1503066211613-c17ebc9daef0?ixlib=rb-1.2.1&dpr=1&auto=format&fit=crop&w=416&h=312&q=60",
        "https://images.unsplash.com/photo-1520848315518-b991dd16a625?ixlib=rb-1.2.1&dpr=1&auto=format&fit=crop&w=416&h=312&q=60",
        "https://images.unsplash.com/photo-1539418561314-565804e349c0?ixlib=rb-1.2.1&dpr=1&auto=format&fit=crop&w=416&h=312&q=60",
        "https://images.unsplash.com/photo-1539418561314-565804e349c0?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1524272332618-3a94122bb0c1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjU3NTIxfQ&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1524293191286-59ec719556d4?ixlib=rb-1.2.1&auto=format&fit=crop&w=654&q=80",
        "https://images.unsplash.com/photo-1478005344131-44da2ded3415?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
        "https://images.unsplash.com/photo-1484406566174-9da000fda645?ixlib=rb-1.2.1&auto=format&fit=crop&w=635&q=80",
        "https://images.unsplash.com/photo-1462953491269-9aff00919695?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
        "https://images.unsplash.com/photo-1494256997604-768d1f608cac?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1543183344-acd290d5142e?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1452001603782-7d4e7d931173?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1539692858702-5cc9e1e40c17?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1563409236340-c174b51cbb81?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
        "https://images.unsplash.com/photo-1486723312829-f32b4a25211b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1486518714050-b97edb7fcfa9?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjExMjU4fQ&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1554226114-f7ae1de16f55?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1550699566-83f93df24072?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1418405752269-40caf13f90ad?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1486365227551-f3f90034a57c?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1568435363428-2474799f37c3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjE3MzYxfQ&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1553338258-24fe91e8baf3?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1491604612772-6853927639ef?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1565416448218-e59ef8b4f03a?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1516728778615-2d590ea1855e?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
        "https://images.unsplash.com/photo-1574260288371-7b63f7e3f186?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1550684863-a70a48d476d5?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1496963729609-7d408fa580b5?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1531959870249-9f9b729efcf4?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1490260400179-d656f04de422?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
)

private fun desc(id: Long): String {
    return  "[$id] Caption or other information for this picture [$id]"

}

data class MyData(val id: Long, val url: String, val subsampling: Boolean = false, val desc: String = desc(id)) : Photo {
    override fun id(): Long = id
    override fun subsampling() = subsampling
}


val mainHandler = Handler(Looper.getMainLooper())
var id = 0L
val myData: List<MyData> by lazy {
    mutableListOf(
            // long horizontal
            MyData(subsampling = true, id = id++, url = "http://image.xcar.com.cn/attachments/a/day_20141126/2014112609_68d9843ed0c3e1d05735CObUWVqIy3rr.jpg"),
            // long vertical
            MyData(subsampling = true, id = id++, url = "http://img.zcool.cn/community/01ee43596f0ce9a8012193a38dcb00.jpg")
    ).let {
        it.apply { addAll(image.map { MyData(id = id++, url = it) }) }
    }.toList()
}

val pageSize = 5

fun queryBefore(id: Long, callback: (List<MyData>) -> Unit) {
    val idx = myData.indexOfFirst { it.id == id }
    mainHandler.postDelayed({
        if (idx < 0) {
            callback(emptyList())
            return@postDelayed
        }
        val result = myData.subList(max(idx - pageSize, 0), idx)
        callback(result)
    }, 100)
}

fun queryAfter(id: Long, callback: (List<MyData>) -> Unit) {
    val idx = myData.indexOfFirst { it.id == id }
    mainHandler.postDelayed({
        if (idx < 0) {
            callback(emptyList())
            return@postDelayed
        }

        val result = myData.subList(idx + 1, min(idx + pageSize, myData.size - 1))
        callback(result)
    }, 100)
}


fun dataSourceFactory(): DataSource.Factory<Long, MyData> {
    return object : DataSource.Factory<Long, MyData>() {
        override fun create(): DataSource<Long, MyData> {
            return dataSource()
        }
    }
}

private fun dataSource() = object : ItemKeyedDataSource<Long, MyData>() {
    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<MyData>) {
        val result = myData.subList(0, pageSize)
        callback.onResult(result, 0, result.size)
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<MyData>) {
        queryAfter(params.key) {
            callback.onResult(it)
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<MyData>) {
        queryBefore(params.key) {
            callback.onResult(it)
        }
    }

    override fun getKey(item: MyData): Long {
        return item.id
    }
}