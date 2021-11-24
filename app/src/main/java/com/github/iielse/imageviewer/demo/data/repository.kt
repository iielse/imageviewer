package com.github.iielse.imageviewer.demo.data

import androidx.paging.DataSource
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.demo.business.ItemType
import com.github.iielse.imageviewer.demo.core.*
import com.github.iielse.imageviewer.demo.utils.runOnWorkThread

class TestRepository {
    val data = myData // 模拟准备好一套数据
    private val api = Api(data)
    private var dataSource: XPageKeyedDataSource? = null
    private val dataSourceFactory: DataSource.Factory<Int, Cell> = object : DataSource.Factory<Int, Cell>() {
        override fun create(): DataSource<Int, Cell> = object : XPageKeyedDataSource() {
            override fun totalCount() = state.list.size
            override fun loadRange(start: Int, count: Int): List<Cell?> {
                val list = state.list
                return list.mapToCell(start, count) {
                    Cell(ItemType.TestData, it, state.data[it])
                }
            }
        }.also { dataSource = it }
    }
    private var state = ListState<Photo>()
    val dataList = dataSourceFactory.toLiveData(
        cellId = { it.id },
        requestMore = { request(false) }
    )

    fun request(initial: Boolean) {
        val requestKey = if (initial) -1 else state.nextKey?.toLong()
        api.asyncQueryAfter(requestKey, PAGE_SIZE) {
            runOnWorkThread {
                state = state.reduceOnNext(initial, it) { it.id().toString() }
                dataSource?.invalidate()
            }
        }
    }

    fun delete(item: List<MyData>) = runOnWorkThread {
        data.removeAll(item) // update api request
        state = state.copy(
            list = state.list.removeAll(item.map { it.id.toString() })
        )
        dataSource?.invalidate()
    }



    fun asyncQueryAfter(id: Long, callback: (List<MyData>) -> Unit) {
        api.asyncQueryAfter(id, PAGE_SIZE, callback)
    }

    fun asyncQueryBefore(id: Long, callback: (List<MyData>) -> Unit) {
        api.asyncQueryBefore(id, PAGE_SIZE,callback)
    }



    companion object {
        private val inst by lazy { TestRepository() }
        fun get() = inst
    }
}

const val PAGE_SIZE = 5 // 分页size
private val myData by lazy { data.toMutableList() }
private val data: List<MyData> by lazy {
    mutableListOf(
        // long horizontal
        // long vertical
        MyData(
            subsampling = true,
            id = id++,
            url = "https://imgkepu.gmw.cn/attachement/jpg/site2/20200417/94c69122e51c2003c2e220.jpg"
        ),
        // video
        //MyData(id = id++, url = "https://media.w3.org/2010/05/sintel/trailer.mp4"),
        MyData(
            id = id++,
            url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ),
    ).let {
        it.apply { addAll(image.map { MyData(id = id++, url = it) }) }
    }.toList()
}

// 用于测试的图片数据源
private var id = 0L

// 图片源数据 源自网络随缘摘取
private val image = arrayOf(
    // gif
    "https://img.nmgfic.com:90/uploadimg/image/20190305/15517786485c7e43584732a4.11936910.gif",
    // normal
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
    "https://images.unsplash.com/photo-1531959870249-9f9b729efcf4?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
)