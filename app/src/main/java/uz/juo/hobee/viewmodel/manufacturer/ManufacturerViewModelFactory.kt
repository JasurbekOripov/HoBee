package uz.juo.hobee.viewmodel.manufacturer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ManufacturerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManufacturerViewModel::class.java)) {
            return ManufacturerViewModel() as T
        }
        throw IllegalArgumentException("Error")
    }
}