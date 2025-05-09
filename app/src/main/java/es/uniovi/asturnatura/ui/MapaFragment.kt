package es.uniovi.asturnatura.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapaFragment : Fragment(R.layout.fragment_mapa) {

    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var viewModel: EspaciosViewModel

    companion object {
        val DARK_TILE_SOURCE = XYTileSource(
            "CartoDarkMatter", 1, 19, 256, ".png",
            arrayOf("https://basemaps.cartocdn.com/dark_all/")
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        viewModel = ViewModelProvider(requireActivity())[EspaciosViewModel::class.java]

        map = view.findViewById(R.id.map)
        map.setMultiTouchControls(true)

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.overlays.add(locationOverlay)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        map.controller.setZoom(8.0)
        map.controller.setCenter(GeoPoint(43.3619, -5.8494))

        viewModel.espaciosFiltrados.observe(viewLifecycleOwner) { lista ->
            val toRemove = map.overlays.filterIsInstance<Marker>()
            map.overlays.removeAll(toRemove)

            lista.forEach { espacio ->
                val coords = espacio.coordenadas?.split(",")?.map { it.trim().toDoubleOrNull() }
                if (coords?.size == 2 && coords[0] != null && coords[1] != null) {
                    val point = GeoPoint(coords[0]!!, coords[1]!!)
                    val marker = Marker(map).apply {
                        relatedObject = espacio
                        position = point
                        title = espacio.nombre
                        subDescription = espacio.descripcion
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            when {
                                listOf("playa", "lago", "río", "rio").any { it in title.lowercase() } ->
                                    R.drawable.ic_marker_blue
                                "parque" in title.lowercase() ->
                                    R.drawable.ic_marker_orange
                                "área recreativa" in title.lowercase() ->
                                    R.drawable.ic_marker_green
                                listOf("pico", "montaña").any { it in title.lowercase() } ->
                                    R.drawable.ic_marker_gray
                                else -> R.drawable.ic_marker_green
                            }
                        )
                        infoWindow = InfoWindowClickHandler(
                            R.layout.bonuspack_bubble, map, requireContext(), viewModel
                        )
                        setOnMarkerClickListener { m, mapView ->
                            InfoWindow.closeAllInfoWindowsOn(mapView)
                            m.showInfoWindow()
                            true
                        }
                    }
                    map.overlays.add(marker)
                }
            }
            map.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        val nightMode = PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .getBoolean("night_mode", false)
        map.setTileSource(if (nightMode) DARK_TILE_SOURCE else TileSourceFactory.MAPNIK)
        map.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map.onDetach()
    }

    class InfoWindowClickHandler(
        layoutResId: Int,
        mapView: MapView,
        private val context: Context,
        private val viewModel: EspaciosViewModel
    ) : InfoWindow(layoutResId, mapView) {

        override fun onOpen(item: Any?) {
            val marker = item as? Marker ?: return
            val view: View = mView ?: return
            val espacio = marker.relatedObject as? EspacioNaturalEntity ?: return

            view.findViewById<TextView>(R.id.bubble_title)?.text = espacio.nombre
            view.findViewById<TextView>(R.id.bubble_description)?.text = espacio.descripcion

            view.findViewById<Button>(R.id.bubble_button)?.setOnClickListener {
                val geoUri = "https://www.google.com/maps/dir/?api=1&destination=${marker.position.latitude},${marker.position.longitude}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)).apply {
                    setPackage("com.google.android.apps.maps")
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)))
                }
            }

            view.findViewById<Button>(R.id.bubble_btn_details)?.setOnClickListener {
                val bundle = bundleOf("espacioId" to espacio.id)
                view.findNavController().navigate(R.id.action_nav_mapa_to_detalleEspacioFragment, bundle)
                close()
            }

            val favButton = view.findViewById<ImageButton>(R.id.bubble_fav)
            favButton.setImageResource(
                if (espacio.esFavorito) R.drawable.ic_star else R.drawable.ic_star_border
            )
            favButton.setOnClickListener {
                viewModel.toggleFavorito(espacio)
                favButton.setImageResource(
                    if (espacio.esFavorito) R.drawable.ic_star else R.drawable.ic_star_border
                )
                close()
            }

            view.findViewById<ImageView>(R.id.bubble_close)?.setOnClickListener {
                close()
            }
        }

        override fun onClose() {
            // No-op
        }
    }
}
