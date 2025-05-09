package es.uniovi.asturnatura.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity

class EspaciosAdapter(
    private var espacios: List<EspacioNaturalEntity>,
    private val onItemClick: (EspacioNaturalEntity) -> Unit,
    private val onToggleFavorito: (EspacioNaturalEntity) -> Unit
) : RecyclerView.Adapter<EspaciosAdapter.EspacioViewHolder>() {

    inner class EspacioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvMunicipio: TextView = itemView.findViewById(R.id.tvMunicipio)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EspacioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_espacio, parent, false)
        return EspacioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EspacioViewHolder, position: Int) {
        val espacio = espacios[position]

        holder.tvNombre.text = espacio.nombre
        holder.tvMunicipio.text = espacio.municipio ?: ""
        holder.tvDescripcion.text = espacio.descripcion ?: "Descripción no disponible"

        holder.btnFavorito.setImageResource(
            if (espacio.esFavorito) R.drawable.ic_star else R.drawable.ic_star_border
        )

        holder.itemView.setOnClickListener { onItemClick(espacio) }
        holder.btnFavorito.setOnClickListener { onToggleFavorito(espacio) }
    }

    override fun getItemCount(): Int = espacios.size

    fun actualizarLista(nuevaLista: List<EspacioNaturalEntity>) {
        espacios = nuevaLista
        notifyDataSetChanged()
    }
}
