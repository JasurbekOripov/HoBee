package uz.juo.hobee.viewmodel.all_branch_view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.juo.hobee.paging.AllBranchesSource

class AllBranchesViewModel : ViewModel() {
    fun branches(name: String, lat: String, long: String) =
        Pager(PagingConfig(20)) { AllBranchesSource(name, lat, long) }.flow.cachedIn(
            viewModelScope
        ).asLiveData()

}