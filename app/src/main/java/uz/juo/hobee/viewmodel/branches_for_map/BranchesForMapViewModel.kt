package uz.juo.hobee.viewmodel.branches_for_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.juo.hobee.paging.BrancheByMedicamentSource
import uz.juo.hobee.paging.BranchesForMapSource

class BranchesForMapViewModel : ViewModel() {
    fun branches(id: Int, lat: String, long: String) =
        Pager(PagingConfig(20)) { BranchesForMapSource(id, lat, long) }.flow.cachedIn(
            viewModelScope
        ).asLiveData()

}