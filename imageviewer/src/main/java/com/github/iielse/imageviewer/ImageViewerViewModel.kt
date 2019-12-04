package com.github.iielse.imageviewer

import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.core.*
import com.github.iielse.imageviewer.model.Photo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

object Injection {
    private val picExecutor by lazy { Executors.newSingleThreadExecutor() }
    val picScheduler by lazy { Schedulers.from(picExecutor) }
}

private fun runOnPicThread(block: () -> Unit): Disposable = Observable.fromCallable { block() }.subscribeOn(Injection.picScheduler).subscribe()

data class PhotoState(
        val page: Int = PAGE_INITIAL,
        val list: List<String> = listOf(),
        val data: Map<String, Photo> = mapOf(),
        val loading: LoadStatus = LoadStatus.IDLE
)

class ImageViewerViewModel : ViewModel() {
    private var photoState = PhotoState()
    val photos = photosDataSource()
            .toLiveData(
                    pageSize = Config.PAGE_SIZE,
                    boundaryCallback = object : PagedList.BoundaryCallback<PWrapper>() {
                        override fun onItemAtEndLoaded(itemAtEnd: PWrapper) {
                            if (ID_MORE_LOADING == itemAtEnd.id) {
//                                requestPKHistory(false)
                            }
                        }
                    }
            )

    private var photoDataSource: StorePageKeyedDataSource? = null

    fun test() {
        fetch(0) {
            runOnPicThread {
                photoState = photoState.copy(
                        page = PAGE_INITIAL,
                        list = it.map { it.id.toString() },
                        data = photoState.data.mutablePutAll(it.map { Pair(it.id.toString(), Photo(it.id.toString(), it.imageUrl, 0, 0)) })
                )
                notifyStateInvalidate()
            }
        }
    }

    private fun photosDataSource(): DataSource.Factory<Int, PWrapper> =
            object : DataSource.Factory<Int, PWrapper>() {
                override fun create(): DataSource<Int, PWrapper> {
                    return object : StorePageKeyedDataSource() {

                        override fun totalCount(): Int {
                            return photoState.list.size
                        }

                        override fun loadRange(start: Int, count: Int): List<PWrapper?> {
                            val list = photoState.list
                            return list.safelySubList(max(0, start), min(list.size, start + count))
                                    .map {
                                        transformDefaultPWrapper(it)
                                                ?: PWrapper(ItemType.PHOTO, it, extra = photoState.data[it])
                                    }
                        }
                    }.also {
                        photoDataSource = it
                    }
                }
            }

    private fun notifyStateInvalidate() {
        photoDataSource?.invalidate()
    }
}