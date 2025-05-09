package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel

class FavoritosFragment : Fragment() {

    private lateinit var viewModel: EspaciosViewModel
    private lateinit var adapter: EspaciosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_favoritos, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EspaciosViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFavoritos)
        val tvVacio = view.findViewById<TextView>(R.id.tvFavoritosVacio)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = EspaciosAdapter(
            espacios = emptyList(),
            onItemClick = { espacio ->
                val bundle = Bundle().apply {
                    putString("espacioId", espacio.id)
                }
                findNavController().navigate(
                    R.id.action_favoritosFragment_to_detalleEspacioFragment,
                    bundle
                )
            },
            onToggleFavorito = { espacio ->
                viewModel.toggleFavorito(espacio)
            }
        )

        recyclerView.adapter = adapter

        viewModel.favoritos.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
            tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
