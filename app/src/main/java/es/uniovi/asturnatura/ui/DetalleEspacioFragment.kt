package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
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
        val descripcion = arguments?.getString("descripcion")
        val ubicacion = arguments?.getString("ubicacion")
        val municipio = arguments?.getString("municipio")
        val zona = arguments?.getString("zona")
        val coordenadas = arguments?.getString("coordenadas")
        val flora = arguments?.getString("flora")
        val fauna = arguments?.getString("fauna")
        val queVer = arguments?.getString("queVer")
        val altitud = arguments?.getString("altitud")
        val observaciones = arguments?.getString("observaciones")
        val imagenUrl = arguments?.getString("imagen")

        view.findViewById<TextView>(R.id.tvDetalleNombre).text = nombre

        val imagenesStr = arguments?.getString("imagenes") ?: ""
        val urls = imagenesStr.split("|").filter { it.isNotBlank() }.reversed()

        // ViewPager de imágenes
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerImagenes)
        if (urls.isNotEmpty()) {
            viewPager.adapter = ImageSliderAdapter(urls)
        } else {
            viewPager.visibility = View.GONE
        }

        // Imagen principal
        val imageView = view.findViewById<ImageView>(R.id.ivDetalleImagen)
        val safeImageUrl = imagenUrl?.replace(" ", "%20")
        Glide.with(this)
            .load(safeImageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(imageView)

        // Video
        val youtubeUrl = arguments?.getString("youtubeUrl")


        val videoLayout = view.findViewById<LinearLayout>(R.id.layoutVideo)
        val webView = view.findViewById<WebView>(R.id.youtubeWebView)
        if (!youtubeUrl.isNullOrBlank()) {
            val html = "<iframe width=\"100%\" height=\"100%\" src=\"${youtubeUrl.replace("watch?v=", "embed/")}\" frameborder=\"0\" allowfullscreen></iframe>"
            webView.settings.javaScriptEnabled = true
            webView.loadData(html, "text/html", "utf-8")
            videoLayout.visibility = View.VISIBLE
        } else {
            videoLayout.visibility = View.GONE
        }


        // Mostrar solo si no está vacío
        ocultarBloqueSiVacio(view, R.id.layoutDescripcion, R.id.tvDetalleDescripcion, descripcion)
        ocultarBloqueSiVacio(view, R.id.layoutUbicacion, R.id.tvDetalleUbicacion, ubicacion)
        ocultarBloqueSiVacio(view, R.id.layoutMunicipio, R.id.tvDetalleMunicipio, municipio)
        ocultarBloqueSiVacio(view, R.id.layoutZona, R.id.tvDetalleZona, zona)
        ocultarBloqueSiVacio(view, R.id.layoutCoordenadas, R.id.tvDetalleCoordenadas, coordenadas)
        ocultarBloqueSiVacio(view, R.id.layoutFlora, R.id.tvDetalleFlora, flora)
        ocultarBloqueSiVacio(view, R.id.layoutFauna, R.id.tvDetalleFauna, fauna)
        ocultarBloqueSiVacio(view, R.id.layoutQueVer, R.id.tvDetalleQueVer, queVer)
        ocultarBloqueSiVacio(view, R.id.layoutAltitud, R.id.tvDetalleAltitud, altitud)
        ocultarBloqueSiVacio(view, R.id.layoutObservaciones, R.id.tvDetalleObservaciones, observaciones)
    }

    private fun ocultarSiVacio(view: View, id: Int, contenido: String?) {
        val textView = view.findViewById<TextView>(id)
        if (contenido.isNullOrBlank()) {
            textView.visibility = View.GONE
        } else {
            textView.text = contenido
            textView.visibility = View.VISIBLE
        }
    }

    private fun ocultarBloqueSiVacio(view: View, layoutId: Int, textId: Int, contenido: String?) {
        val layout = view.findViewById<LinearLayout>(layoutId)
        val textView = view.findViewById<TextView>(textId)
        if (contenido.isNullOrBlank()) {
            layout.visibility = View.GONE
        } else {
            textView.text = contenido
            layout.visibility = View.VISIBLE
        }
    }




}
