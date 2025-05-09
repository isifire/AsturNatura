package es.uniovi.asturnatura.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.viewmodel.DetalleEspacioViewModel
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DetalleEspacioFragment : Fragment(R.layout.fragment_detalle_espacio) {

    private val detalleViewModel: DetalleEspacioViewModel by viewModels()
    private lateinit var espaciosViewModel: EspaciosViewModel
    private lateinit var mapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        mapView = view.findViewById(R.id.mapaDetalle)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

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
            espacio?.let {
                bindDatos(view, it)
                mostrarMapa(it)
                configurarBotonRuta(view, it)
            }
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
        val webView = root.findViewById<WebView>(R.id.youtubeWebView)
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
            val tv = root.findViewById<TextView>(textId)
            if (content.isNullOrBlank()) layout.visibility = View.GONE
            else {
                tv.text = content
                layout.visibility = View.VISIBLE
            }
        }

        hideIfEmpty(R.id.layoutDescripcion, getString(R.string.detalle_descripcion), espacio.descripcion)
        hideIfEmpty(R.id.layoutMunicipio, getString(R.string.detalle_municipio), espacio.municipio)
        hideIfEmpty(R.id.layoutZona, getString(R.string.detalle_zona), espacio.zona)
        hideIfEmpty(R.id.layoutCoordenadas, getString(R.string.detalle_coordenadas), espacio.coordenadas)
        hideIfEmpty(R.id.layoutFlora, getString(R.string.detalle_flora), espacio.flora)
        hideIfEmpty(R.id.layoutFauna, getString(R.string.detalle_fauna), espacio.fauna)
        hideIfEmpty(R.id.layoutQueVer, getString(R.string.detalle_que_ver), espacio.queVer)
        hideIfEmpty(R.id.layoutAltitud, getString(R.string.detalle_altitud), espacio.altitud)
        hideIfEmpty(R.id.layoutObservaciones, getString(R.string.detalle_observaciones), espacio.observaciones)


    }

    private fun mostrarMapa(espacio: EspacioNaturalEntity) {
        val coords = espacio.coordenadas?.split(",")?.map { it.trim() }
        if (coords != null && coords.size == 2) {
            val lat = coords[0].toDoubleOrNull()
            val lon = coords[1].toDoubleOrNull()
            if (lat != null && lon != null) {
                val punto = GeoPoint(lat, lon)
                mapView.controller.setZoom(10.0)
                mapView.controller.setCenter(punto)

                val marker = Marker(mapView).apply {
                    position = punto
                    title = espacio.nombre
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    icon = ContextCompat.getDrawable(
                        requireContext(),
                        when {
                            "playa" in title.lowercase() -> R.drawable.ic_marker_blue
                            "lago" in title.lowercase() -> R.drawable.ic_marker_blue
                            "río" in title.lowercase() -> R.drawable.ic_marker_blue
                            "rio" in title.lowercase() -> R.drawable.ic_marker_blue
                            "parque" in title.lowercase() -> R.drawable.ic_marker_orange
                            "área recreativa" in title.lowercase() -> R.drawable.ic_marker_green
                            "pico" in title.lowercase() -> R.drawable.ic_marker_gray
                            "montaña" in title.lowercase() -> R.drawable.ic_marker_gray
                            else -> R.drawable.ic_marker_green
                        }
                    )

                    // Evitar InfoWindow que da error
                    setOnMarkerClickListener { _, _ -> true }
                }

                mapView.overlays.add(marker)
                mapView.invalidate()
            }
        }
    }


    private fun configurarBotonRuta(view: View, espacio: EspacioNaturalEntity) {
        val btnRuta = view.findViewById<Button>(R.id.btnComoLlegar)
        btnRuta.setOnClickListener {
            val coords = espacio.coordenadas?.split(",")?.map { it.trim() }
            if (coords != null && coords.size == 2) {
                val lat = coords[0]
                val lon = coords[1]
                val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lon")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            }
        }
    }

    private fun hideIfEmpty(layoutId: Int, titulo: String, contenido: String?) {
        val layout = view?.findViewById<LinearLayout>(layoutId) ?: return
        val tvTitulo = layout.findViewById<TextView>(R.id.tvTitulo)
        val tvContenido = layout.findViewById<TextView>(R.id.tvContenido)

        if (contenido.isNullOrBlank()) {
            layout.visibility = View.GONE
        } else {
            tvTitulo.text = titulo
            tvContenido.text = contenido
            layout.visibility = View.VISIBLE
        }
    }



    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }
}
