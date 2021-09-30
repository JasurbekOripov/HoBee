package uz.juo.hobee.viewmodel.search_medicament

import android.widget.SearchView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.juo.hobee.viewmodel.all_branch_view_model.AllBranchesViewModel
import java.lang.IllegalArgumentException

class SearchViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchView::class.java)) {
            return AllBranchesViewModel() as T
        }
        throw IllegalArgumentException("Error")
    }
}