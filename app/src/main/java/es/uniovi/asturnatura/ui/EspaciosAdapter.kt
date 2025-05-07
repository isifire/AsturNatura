package es.uniovi.asturnatura.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity

class EspaciosAdapter(
    private val onItemClick: (EspacioNaturalEntity) -> Unit
) : RecyclerView.Adapter<EspaciosAdapter.EspacioViewHolder>() {

    private val espacios = mutableListOf<EspacioNaturalEntity>()

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
        holder.tvNombre.text = item.nombre
        holder.tvMunicipio.text = item.ubicacion
        holder.tvTitulo.text = item.descripcion

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = espacios.size

    fun update(nuevosEspacios: List<EspacioNaturalEntity>) {
        Log.d("Adapter", "Actualizando con ${nuevosEspacios.size} elementos")
        espacios.clear()
        espacios.addAll(nuevosEspacios)
        notifyDataSetChanged()
    }

}
