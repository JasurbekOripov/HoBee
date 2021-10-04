package uz.juo.hobee.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.juo.hobee.models.BranchesByMedIdPrice
import uz.juo.hobee.models.ItemMedIdPrice
import uz.juo.hobee.retrofit.ApiClient

class BranchesForMapSource(
    var id: Int,
    var lat: String,
    var long: String
) :
    PagingSource<Int, ItemMedIdPrice>() {
    override fun getRefreshKey(state: PagingState<Int, ItemMedIdPrice>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemMedIdPrice> {
        var data = BranchesByMedIdPrice()
        return try {
            var pageNumber = params.key ?: 1
            data = ApiClient.apiService.getBranchesForMap(
                limit = 20,
                page = pageNumber,
                lat = long,
                long = lat,
                id = id
            )
            if (pageNumber > 1) {
                LoadResult.Page(data.items, pageNumber - 1, pageNumber + 1)
            } else {
                LoadResult.Page(data.items, null, pageNumber + 1)
            }

        } catch (e: Exception) {
            LoadResult.Page(emptyList(), null, null)
        }
    }

}