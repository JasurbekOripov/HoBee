package uz.juo.hobee.paging

import android.content.Context
import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.juo.hobee.models.ItemX
import uz.juo.hobee.retrofit.ApiClient

class SearchMedicamentSource(var context: Context, var name: String) :
    PagingSource<Int, ItemX>() {
    override fun getRefreshKey(state: PagingState<Int, ItemX>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemX> {
        return try {
            var pageNumber = params.key ?: 1
            var list = ApiClient.apiService.getMedicamentByName(10, pageNumber, name = name)
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