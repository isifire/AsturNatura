package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.util.JsonImage

class DetalleEspacioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_espacio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nombre = arguments?.getString("nombre") ?: "Nombre no disponible"
        val descripcion = arguments?.getString("descripcion") ?: "Sin descripción"
        val ubicacion = arguments?.getString("ubicacion") ?: "Ubicación no disponible"
        val municipio = arguments?.getString("municipio") ?: "Municipio desconocido"
        val zona = arguments?.getString("zona") ?: "Zona desconocida"
        val coordenadas = arguments?.getString("coordenadas") ?: "Sin coordenadas"
        val flora = arguments?.getString("flora") ?: "No especificado"
        val fauna = arguments?.getString("fauna") ?: "No especificado"
        val queVer = arguments?.getString("queVer") ?: "Sin información"
        val altitud = arguments?.getString("altitud") ?: "Sin datos"
        val observaciones = arguments?.getString("observaciones") ?: "Sin observaciones"
        val imagenUrl = arguments?.getString("imagen") // <- nueva imagen

        val imagenesStr = arguments?.getString("imagenes") ?: ""
        Log.d("Imagenes", imagenesStr)
        val urls = imagenesStr?.split("|")?.filter { !it.isNullOrBlank() }?.reversed() ?: emptyList()



        if (urls.isNotEmpty()) {
            val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerImagenes)
            viewPager.adapter = ImageSliderAdapter(urls)
        }




        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerImagenes)
        viewPager.adapter = ImageSliderAdapter(urls)


        // Cargar la imagen
        val imageView = view.findViewById<ImageView>(R.id.ivDetalleImagen)
        val safeImageUrl = imagenUrl?.replace(" ", "%20")


        Glide.with(this)
                .load(safeImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)






        // Rellenar los TextViews
        view.findViewById<TextView>(R.id.tvDetalleNombre).text = nombre
        view.findViewById<TextView>(R.id.tvDetalleDescripcion).text = descripcion
        view.findViewById<TextView>(R.id.tvDetalleUbicacion).text = ubicacion
        view.findViewById<TextView>(R.id.tvDetalleMunicipio).text = municipio
        view.findViewById<TextView>(R.id.tvDetalleZona).text = zona
        view.findViewById<TextView>(R.id.tvDetalleCoordenadas).text = coordenadas
        view.findViewById<TextView>(R.id.tvDetalleFlora).text = flora
        view.findViewById<TextView>(R.id.tvDetalleFauna).text = fauna
        view.findViewById<TextView>(R.id.tvDetalleQueVer).text = queVer
        view.findViewById<TextView>(R.id.tvDetalleAltitud).text = altitud
        view.findViewById<TextView>(R.id.tvDetalleObservaciones).text = observaciones
    }
}
