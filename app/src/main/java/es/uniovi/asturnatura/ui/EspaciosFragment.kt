package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.util.JsonImage
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel
import es.uniovi.asturnatura.viewmodel.EspaciosViewModelFactory

class EspaciosFragment : Fragment() {

    private lateinit var viewModel: EspaciosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_espacios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerEspacios)

        // Inicializar ViewModel con la Factory
        val factory = EspaciosViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[EspaciosViewModel::class.java]

        // Observar espacios locales
        viewModel.espaciosLocales.observe(viewLifecycleOwner) { lista ->
            recycler.adapter = EspaciosAdapter(lista) { espacio ->
                abrirDetalle(espacio)
            }
        }

        // Cargar datos
        viewModel.cargarDatosInteligente(requireContext())
    }

    private fun abrirDetalle(espacio: EspacioNaturalEntity) {
        val bundle = Bundle().apply {
            putString("nombre", espacio.nombre)
            putString("descripcion", espacio.descripcion)
            putString("ubicacion", espacio.ubicacion)
            putString("municipio", espacio.municipio)
            putString("zona", espacio.zona)
            putString("coordenadas", espacio.coordenadas)
            putString("flora", espacio.flora)
            putString("fauna", espacio.fauna)
            putString("queVer", espacio.queVer)
            putString("altitud", espacio.altitud)
            putString("observaciones", espacio.observaciones)
            putString("imagen", espacio.imagen)
            putString("imagenes", espacio.imagenes)
            putString("youtubeUrl", espacio.youtubeUrl)
        }

        val detalleFragment = DetalleEspacioFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.main, detalleFragment)
            .addToBackStack(null)
            .commit()
    }
}
