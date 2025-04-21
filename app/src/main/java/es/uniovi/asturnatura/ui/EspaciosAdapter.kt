package es.uniovi.asturnatura.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNatural

class EspaciosAdapter(private val espacios: List<EspacioNatural>) :
    RecyclerView.Adapter<EspaciosAdapter.EspacioViewHolder>() {

    class EspacioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvMunicipio: TextView = view.findViewById(R.id.tvMunicipio)
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EspacioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_espacio, parent, false)
        return EspacioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EspacioViewHolder, position: Int) {
        val item = espacios[position]
        holder.tvNombre.text = item.nombre?.content ?: "Sin nombre"
        holder.tvMunicipio.text = item.municipio?.content ?: "Municipio desconocido"
        holder.tvTitulo.text = item.informacion?.titulo?.content ?: "Sin título"
    }

    override fun getItemCount(): Int = espacios.size
}
