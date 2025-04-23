package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.asturnatura.R
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

        viewModel.espaciosLocales.observe(viewLifecycleOwner) {
            recycler.adapter = EspaciosAdapter(it) // <- it es de tipo List<EspacioNaturalEntity>
        }


        viewModel.cargarDatosInteligente(requireContext())

    }
}
