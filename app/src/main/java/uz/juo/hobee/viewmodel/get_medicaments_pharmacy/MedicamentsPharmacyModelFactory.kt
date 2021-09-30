package uz.juo.hobee.viewmodel.get_medicaments_pharmacy

import android.widget.SearchView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.juo.hobee.viewmodel.all_branch_view_model.AllBranchesViewModel
import java.lang.IllegalArgumentException

class MedicamentsPharmacyModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchView::class.java)) {
            return MedicamentsPharmacyViewModel() as T
        }
        throw IllegalArgumentException("Error")
    }
}