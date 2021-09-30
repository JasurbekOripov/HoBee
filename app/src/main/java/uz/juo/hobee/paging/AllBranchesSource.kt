package uz.juo.hobee.paging

import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.juo.hobee.models.Item
import uz.juo.hobee.retrofit.ApiClient

class AllBranchesSource(
    var name: String,
    var lat: String,
    var long: String
) :
    PagingSource<Int, Item>() {
    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            var pageNumber = params.key ?: 1
            var list = ApiClient.apiService.getAllBranches(
                limit = 20,
                page = pageNumber,
                name = name,
                lat = lat,
                long = long
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