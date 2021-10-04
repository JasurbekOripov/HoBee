package uz.juo.hobee.viewmodel.branches_for_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class BranchesForMapViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BranchesForMapViewModel::class.java)) {
            return BranchesForMapViewModel() as T
        }
        throw IllegalArgumentException("Error")
    }
}