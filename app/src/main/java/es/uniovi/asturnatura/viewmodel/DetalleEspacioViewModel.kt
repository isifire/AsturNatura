package es.uniovi.asturnatura.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.uniovi.asturnatura.model.EspacioNaturalEntity

class DetalleEspacioViewModel : ViewModel() {

    private val _espacio = MutableLiveData<EspacioNaturalEntity>()
    val espacio: LiveData<EspacioNaturalEntity> = _espacio

    fun cargarDesdeId(id: String, viewModel: EspaciosViewModel) {
        viewModel.getEspacioById(id).observeForever {
            _espacio.value = it
        }
    }

    fun cargarDesdeBundle(args: Bundle) {
        val espacio = EspacioNaturalEntity(
            id           = args.getString("id") ?: "",
            nombre       = args.getString("nombre") ?: "",
            descripcion  = args.getString("descripcion") ?: "",
            ubicacion    = args.getString("ubicacion")?: "",
            tipo         = "", // no se usa
            imagen       = args.getString("imagen"),
            municipio    = args.getString("municipio"),
            zona         = args.getString("zona"),
            coordenadas  = args.getString("coordenadas"),
            flora        = args.getString("flora"),
            fauna        = args.getString("fauna"),
            queVer       = args.getString("queVer"),
            altitud      = args.getString("altitud"),
            observaciones= args.getString("observaciones"),
            facebook     = null, instagram = null, twitter = null,
            imagenes     = args.getString("imagenes") ?: "",
            youtubeUrl   = args.getString("youtubeUrl")
        )
        _espacio.value = espacio
    }
}
