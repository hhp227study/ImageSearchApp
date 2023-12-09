package com.hhp227.imagesearchapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.hhp227.imagesearchapp.BuildConfig
import com.hhp227.imagesearchapp.api.MainService
import com.hhp227.imagesearchapp.utilities.DEFAULT_PAGE_SIZE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(private val service: MainService) {
    fun searchImage(query: String) = Pager(
        config = PagingConfig(pageSize = 25, enablePlaceholders = false),
        pagingSourceFactory = { ImagePagingSource(service, query) }
    ).liveData
}