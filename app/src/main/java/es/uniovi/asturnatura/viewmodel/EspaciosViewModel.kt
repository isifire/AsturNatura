package es.uniovi.asturnatura.viewmodel

import androidx.lifecycle.*
import es.uniovi.asturnatura.model.EspacioNatural
import es.uniovi.asturnatura.network.RetrofitInstance
import kotlinx.coroutines.launch

class EspaciosViewModel : ViewModel() {

    private val _espacios = MutableLiveData<List<EspacioNatural>>()
    val espacios: LiveData<List<EspacioNatural>> get() = _espacios

    fun cargarEspacios() {
        viewModelScope.launch {
            try {
                val resultado = RetrofitInstance.api.getEspaciosNaturales().articles.article
                _espacios.value = resultado
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
