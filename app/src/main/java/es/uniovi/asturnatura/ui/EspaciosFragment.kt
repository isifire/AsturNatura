package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.util.JsonImage
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel

class EspaciosFragment : Fragment() {

    private val viewModel: EspaciosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_espacios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerEspacios)
        viewModel.espaciosLocales.observe(viewLifecycleOwner) { lista ->
            recycler.adapter = EspaciosAdapter(lista) { espacio ->
                val youtubeUrl = espacio.imagenes
                    ?.split("|")
                    ?.mapNotNull { JsonImage.extraerUrlYoutube(it) }
                    ?.firstOrNull() // Tomamos el primer video que sea válido

                val fragment = DetalleEspacioFragment().apply {
                    arguments = Bundle().apply {
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
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit()
            }
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
        }

        val detalleFragment = DetalleEspacioFragment()
        detalleFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.main, detalleFragment)
            .addToBackStack(null)
            .commit()
    }
}
