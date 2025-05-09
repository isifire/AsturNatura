package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.viewmodel.DetalleEspacioViewModel
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel

class DetalleEspacioFragment : Fragment(R.layout.fragment_detalle_espacio) {

    private val detalleViewModel: DetalleEspacioViewModel by viewModels()
    private lateinit var espaciosViewModel: EspaciosViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        espaciosViewModel = ViewModelProvider(requireActivity())[EspaciosViewModel::class.java]
        espaciosViewModel.cargarEspaciosLocales()

        val args = requireArguments()
        if (args.containsKey("espacioId")) {
            val id = args.getString("espacioId") ?: return
            detalleViewModel.cargarDesdeId(id, espaciosViewModel)
        } else {
            detalleViewModel.cargarDesdeBundle(args)
        }

        detalleViewModel.espacio.observe(viewLifecycleOwner) { espacio ->
            espacio?.let { bindDatos(view, it) }
        }
    }

    private fun bindDatos(root: View, espacio: EspacioNaturalEntity) {
        root.findViewById<TextView>(R.id.tvDetalleNombre).text = espacio.nombre

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

        val iv = root.findViewById<ImageView>(R.id.ivDetalleImagen)
        Glide.with(this)
            .load(espacio.imagen?.replace(" ", "%20"))
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(iv)

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
