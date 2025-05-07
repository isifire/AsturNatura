package es.uniovi.asturnatura.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.model.EspacioNatural
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
        /** TileSource de “Dark Matter” (Carto) */
        val DARK_TILE_SOURCE = XYTileSource(
            "CartoDarkMatter",
            1, 19, 256, ".png",
            arrayOf("https://basemaps.cartocdn.com/dark_all/")
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa osmdroid
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        viewModel = ViewModelProvider(requireActivity())[EspaciosViewModel::class.java]

        map = view.findViewById(R.id.map)
        map.setMultiTouchControls(true)

        // Overlay de ubicación GPS
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

        // Centrar en Asturias
        val startPoint = GeoPoint(43.3619, -5.8494)
        map.controller.setZoom(8.0)
        map.controller.setCenter(startPoint)

        // Observa espacios y añade marcadores
        viewModel.espaciosFiltrados.observe(viewLifecycleOwner) { lista ->
            // Elimina marcadores anteriores (pero no el overlay de ubicación)
            val toRemove = map.overlays.filterIsInstance<Marker>()
            map.overlays.removeAll(toRemove)

            lista.forEach { espacio ->
                espacio.coordenadas
                    ?.takeIf { it.isNotBlank() }
                    ?.split(",")
                    ?.takeIf { it.size == 2 }
                    ?.let { coords ->
                        coords[0].trim().toDoubleOrNull()?.let { lat ->
                            coords[1].trim().toDoubleOrNull()?.let { lon ->
                                GeoPoint(lat, lon)
                            }
                        }
                    }?.let { point ->
                        val marker = Marker(map).apply {
                            relatedObject = espacio
                            position = point
                            title = espacio.nombre
                            subDescription = espacio.descripcion
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            icon = ContextCompat.getDrawable(
                                requireContext(),
                                when {
                                    listOf("playa","lago","río","rio").any { it in title.lowercase() } ->
                                        R.drawable.ic_marker_blue
                                    "parque" in title.lowercase() ->
                                        R.drawable.ic_marker_orange
                                    "área recreativa" in title.lowercase() ->
                                        R.drawable.ic_marker_green
                                    listOf("pico","montaña").any { it in title.lowercase() } ->
                                        R.drawable.ic_marker_gray
                                    else ->
                                        R.drawable.ic_marker_green
                                }
                            )

                            infoWindow = InfoWindowClickHandler(
                                R.layout.bonuspack_bubble, map, requireContext()
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
        // Cada vez que el fragment reaparece, re-lee la preferencia y aplica el tileSource
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

    /** InfoWindow con Ruta y Detalles */
    class InfoWindowClickHandler(
        layoutResId: Int,
        mapView: MapView,
        private val context: Context
    ) : InfoWindow(layoutResId, mapView) {

        override fun onOpen(item: Any?) {
            val marker = item as? Marker ?: return
            val view: View = mView ?: return

            // Título y descripción
            view.findViewById<TextView>(R.id.bubble_title)?.text = marker.title
            view.findViewById<TextView>(R.id.bubble_description)?.text = marker.subDescription

            // Botón Ruta
            view.findViewById<Button>(R.id.bubble_button)?.setOnClickListener {
                val lat = marker.position.latitude
                val lon = marker.position.longitude
                val geoUri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lon"
                Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)).apply {
                    setPackage("com.google.android.apps.maps")
                    try { context.startActivity(this) }
                    catch (e: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)))
                    }
                }
            }

            // Botón Detalles
            view.findViewById<Button>(R.id.bubble_btn_details)?.setOnClickListener {
                val espacio = marker.relatedObject as? EspacioNaturalEntity ?: return@setOnClickListener
                val bundle = bundleOf("espacioId" to espacio.id)
                view.findNavController().navigate(
                    R.id.action_nav_mapa_to_detalleEspacioFragment,
                    bundle
                )
                close()
            }

            // Cerrar
            view.findViewById<ImageView>(R.id.bubble_close)?.setOnClickListener { close() }
        }

        override fun onClose() { /* no-op */ }
    }
}
