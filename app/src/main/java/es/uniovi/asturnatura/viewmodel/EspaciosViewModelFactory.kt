package es.uniovi.asturnatura.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EspaciosViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EspaciosViewModel::class.java)) {
            return EspaciosViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
