package uz.juo.hobee.viewmodel.get_medicaments_pharmacy

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.juo.hobee.paging.GetPharmacyMedicamentSource

class MedicamentsPharmacyViewModel : ViewModel() {
    fun medicaments(context: Context, name: String, branch_id: Int) =
        Pager(PagingConfig(20)) {
            GetPharmacyMedicamentSource(
                context,
                name,
                branchId = branch_id
            )
        }.flow.cachedIn(
            viewModelScope
        ).asLiveData()

}