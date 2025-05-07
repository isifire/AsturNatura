package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel

class DetalleEspacioFragment : Fragment(R.layout.fragment_detalle_espacio) {

    private lateinit var viewModel: EspaciosViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EspaciosViewModel::class.java]
        // always load local data so ViewModel can serve by id
        viewModel.cargarEspaciosLocales()

        val args = requireArguments()
        if (args.containsKey("espacioId")) {
            // launched from map: load by id
            val espacioId = args.getString("espacioId") ?: return
            viewModel.getEspacioById(espacioId).observe(viewLifecycleOwner) { espacio ->
                espacio?.let { bindDatos(view, it) }
            }
        } else {
            // launched from list: read all fields from arguments
            val espacio = EspacioNaturalEntity(
                id           = args.getString("id") ?: "",
                nombre       = args.getString("nombre") ?: "",
                descripcion  = args.getString("descripcion") ?: "",
                ubicacion    = args.getString("ubicacion")?: "",
                tipo         = "", // unused here
                imagen       = args.getString("imagen"),
                municipio    = args.getString("municipio"),
                zona         = args.getString("zona"),
                coordenadas  = args.getString("coordenadas"),
                flora        = args.getString("flora"),
                fauna        = args.getString("fauna"),
                queVer       = args.getString("queVer"),
                altitud      = args.getString("altitud"),
                observaciones= args.getString("observaciones"),
                facebook     = null, instagram = null, twitter = null,
                imagenes     = args.getString("imagenes") ?: "",
                youtubeUrl   = args.getString("youtubeUrl")
            )
            bindDatos(view, espacio)
        }
    }

    private fun bindDatos(root: View, espacio: EspacioNaturalEntity) {
        root.findViewById<TextView>(R.id.tvDetalleNombre).text = espacio.nombre

        // gallery
        val urls = espacio.imagenes
            ?.split("|")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.reversed()
            ?: emptyList()
        val vp = root.findViewById<ViewPager2>(R.id.viewPagerImagenes)
        if (urls.isNotEmpty()) {
            vp.adapter = ImageSliderAdapter(urls)
            vp.visibility = View.VISIBLE
        } else {
            vp.visibility = View.GONE
        }

        // main image
        val iv = root.findViewById<ImageView>(R.id.ivDetalleImagen)
        Glide.with(this)
            .load(espacio.imagen?.replace(" ", "%20"))
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(iv)

        // video
        val videoLayout = root.findViewById<LinearLayout>(R.id.layoutVideo)
        val webView     = root.findViewById<WebView>(R.id.youtubeWebView)
        if (!espacio.youtubeUrl.isNullOrBlank()) {
            val embed = espacio.youtubeUrl.replace("watch?v=", "embed/")
            val html = "<iframe width=\"100%\" height=\"100%\" src=\"$embed\" frameborder=\"0\" allowfullscreen></iframe>"
            webView.settings.javaScriptEnabled = true
            webView.loadData(html, "text/html", "utf-8")
            videoLayout.visibility = View.VISIBLE
        } else {
            videoLayout.visibility = View.GONE
        }

        // dynamic sections
        fun hideIfEmpty(layoutId: Int, textId: Int, content: String?) {
            val layout = root.findViewById<LinearLayout>(layoutId)
            val tv     = root.findViewById<TextView>(textId)
            if (content.isNullOrBlank()) layout.visibility = View.GONE
            else {
                tv.text = content
                layout.visibility = View.VISIBLE
            }
        }

        hideIfEmpty(R.id.layoutDescripcion,   R.id.tvDetalleDescripcion,   espacio.descripcion)
        hideIfEmpty(R.id.layoutUbicacion,     R.id.tvDetalleUbicacion,     espacio.ubicacion)
        hideIfEmpty(R.id.layoutMunicipio,     R.id.tvDetalleMunicipio,     espacio.municipio)
        hideIfEmpty(R.id.layoutZona,          R.id.tvDetalleZona,          espacio.zona)
        hideIfEmpty(R.id.layoutCoordenadas,   R.id.tvDetalleCoordenadas,   espacio.coordenadas)
        hideIfEmpty(R.id.layoutFlora,         R.id.tvDetalleFlora,         espacio.flora)
        hideIfEmpty(R.id.layoutFauna,         R.id.tvDetalleFauna,         espacio.fauna)
        hideIfEmpty(R.id.layoutQueVer,        R.id.tvDetalleQueVer,        espacio.queVer)
        hideIfEmpty(R.id.layoutAltitud,       R.id.tvDetalleAltitud,       espacio.altitud)
        hideIfEmpty(R.id.layoutObservaciones, R.id.tvDetalleObservaciones, espacio.observaciones)
    }
}
