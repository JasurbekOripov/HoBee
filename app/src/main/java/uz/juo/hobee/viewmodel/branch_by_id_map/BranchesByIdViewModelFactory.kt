package uz.juo.hobee.viewmodel.branch_by_id_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class BranchesByIdViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BranchesByIdViewModel::class.java)) {
            return BranchesByIdViewModel() as T
        }
        throw IllegalArgumentException("Error")
    }
}