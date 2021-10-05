package uz.juo.hobee.viewmodel.search_medicament

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.juo.hobee.paging.SearchMedicamentSource

class SearchViewModel : ViewModel() {
    fun medicaments(context: Context, name: String,filter:String) =
        Pager(PagingConfig(20)) { SearchMedicamentSource(context, name,filter) }.flow.cachedIn(
            viewModelScope
        ).asLiveData()

}