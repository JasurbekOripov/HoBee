package uz.juo.hobee.viewmodel.all_branch_view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class BranchesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllBranchesViewModel::class.java)) {
            return AllBranchesViewModel() as T
        }
        throw IllegalArgumentException("Error")
    }
}