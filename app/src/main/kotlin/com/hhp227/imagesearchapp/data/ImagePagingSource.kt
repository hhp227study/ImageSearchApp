package com.hhp227.imagesearchapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hhp227.imagesearchapp.BuildConfig
import com.hhp227.imagesearchapp.api.MainService
import com.hhp227.imagesearchapp.utilities.DEFAULT_PAGE

class ImagePagingSource(private val mainService: MainService, private val query: String) : PagingSource<Int, Document>() {
    override fun getRefreshKey(state: PagingState<Int, Document>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Document> {
        val page = params.key ?: DEFAULT_PAGE
        return try {
            val response = mainService.searchImages(
                authorization = "KakaoAK ${BuildConfig.REST_API_KEY}",
                query = query,
                page = page,
                size = params.loadSize
            )
            LoadResult.Page(
                data = response.documents,
                prevKey = if (page == DEFAULT_PAGE) null else page - 1,
                nextKey = /*if (params.key == null) page + 3 else */page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}