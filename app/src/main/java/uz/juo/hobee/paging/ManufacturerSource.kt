package uz.juo.hobee.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.juo.hobee.models.ManufacturerItem
import uz.juo.hobee.retrofit.ApiClient

class ManufacturerSource(
    var name: String
) :
    PagingSource<Int, ManufacturerItem>() {
    override fun getRefreshKey(state: PagingState<Int, ManufacturerItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ManufacturerItem> {
        return try {
            var pageNumber = params.key ?: 1
            var list = ApiClient.apiService.getAllManufacturer(
                limit = 20,
                page = pageNumber,
                name = name,
            )
            if (pageNumber > 1) {
                LoadResult.Page(list.items, pageNumber - 1, pageNumber + 1)
            } else {
                LoadResult.Page(list.items, null, pageNumber + 1)
            }

        } catch (e: Exception) {
            LoadResult.Page(emptyList(), null, null)
        }
    }

}