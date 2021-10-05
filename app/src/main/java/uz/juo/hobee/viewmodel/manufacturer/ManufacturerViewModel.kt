package uz.juo.hobee.viewmodel.manufacturer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.juo.hobee.paging.BrancheByMedicamentSource
import uz.juo.hobee.paging.BranchesForMapSource
import uz.juo.hobee.paging.ManufacturerSource

class ManufacturerViewModel : ViewModel() {
    fun manufacturers(name:String) =
        Pager(PagingConfig(20)) { ManufacturerSource(name) }.flow.cachedIn(
            viewModelScope
        ).asLiveData()

}