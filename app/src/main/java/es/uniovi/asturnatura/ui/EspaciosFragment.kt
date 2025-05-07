package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel

class EspaciosFragment : Fragment() {

    private lateinit var adapter: EspaciosAdapter
    private lateinit var viewModel: EspaciosViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerEspacios)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = EspaciosAdapter { espacio ->
            abrirDetalle(espacio)
        }
        recycler.adapter = adapter


        viewModel = ViewModelProvider(requireActivity())[EspaciosViewModel::class.java]
        viewModel.espaciosFiltrados.observe(viewLifecycleOwner) { lista ->
            Log.d("Fragment", "RecyclerView recibido ${lista.size} elementos")
            adapter.update(lista)
        }

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

        findNavController().navigate(
            R.id.action_nav_espacios_to_detalleEspacioFragment,
            bundle
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_espacios, container, false)
}
