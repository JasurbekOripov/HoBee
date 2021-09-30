package uz.juo.hobee.viewmodel.branch_by_id_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.juo.hobee.paging.BrancheByMedicamentSource

class BranchesByIdViewModel : ViewModel() {
    fun branches(id: Int, lat: String, long: String, type: String) =
        Pager(PagingConfig(20)) { BrancheByMedicamentSource(id, lat, long, type) }.flow.cachedIn(
            viewModelScope
        ).asLiveData()

}