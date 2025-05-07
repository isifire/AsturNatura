package es.uniovi.asturnatura.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.widget.ImageView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import android.widget.TextView
import android.widget.Button
import androidx.core.content.ContextCompat
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapaFragment : Fragment(R.layout.fragment_mapa) {

    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var viewModel: EspaciosViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración de osmdroid
        Configuration.getInstance().load(
            requireContext(),
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        viewModel = ViewModelProvider(requireActivity())[EspaciosViewModel::class.java]

        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // GPS Overlay
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

        // Observar datos filtrados y generar marcadores
        viewModel.espaciosFiltrados.observe(viewLifecycleOwner) { lista ->
            val toRemove = mutableListOf<Marker>()
            for (overlay in map.overlays) {
                if (overlay is Marker && overlay != locationOverlay) {
                    toRemove.add(overlay)
                }
            }
            map.overlays.removeAll(toRemove)

            for (espacio in lista) {
                val coordenadasRaw = espacio.coordenadas
                if (!coordenadasRaw.isNullOrBlank()) {
                    val coords = coordenadasRaw.split(",")
                    if (coords.size == 2) {
                        val lat = coords[0].trim().toDoubleOrNull()
                        val lon = coords[1].trim().toDoubleOrNull()
                        if (lat != null && lon != null) {
                            val point = GeoPoint(lat, lon)
                            val marker = Marker(map)
                            marker.position = point
                            marker.title = espacio.nombre
                            marker.subDescription = espacio.descripcion
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            // Detección de categoría a partir del nombre
                            val nombreLower = espacio.nombre.lowercase()
                            val categoria = when {
                                "playa" in nombreLower        -> "Playa"
                                "lago" in nombreLower         -> "Lago"
                                "río" in nombreLower || "rio" in nombreLower -> "Río"
                                "parque" in nombreLower       -> "Parque"
                                "área recreativa" in nombreLower -> "Área Recreativa"
                                "pico" in nombreLower || "montaña" in nombreLower -> "Montaña"
                                else                          -> "Otros"
                            }

                            // Asignar el icono según la categoría detectada
                            marker.icon = when (categoria) {
                                "Playa", "Lago", "Río" ->
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_blue)
                                "Parque" ->
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_orange)
                                "Área Recreativa" ->
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_green)
                                "Montaña" ->
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_gray)
                                else ->
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_green)
                            }

                            marker.infoWindow = InfoWindowClickHandler(
                                R.layout.bonuspack_bubble, map, requireContext()
                            )

                            marker.setOnMarkerClickListener { m, mapView ->
                                InfoWindow.closeAllInfoWindowsOn(mapView)
                                m.showInfoWindow()
                                true
                            }

                            map.overlays.add(marker)
                        }
                    }
                }
            }


            map.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map.onDetach()
    }

    class InfoWindowClickHandler(
        layoutResId: Int,
        mapView: MapView,
        private val context: Context
    ) : InfoWindow(layoutResId, mapView) {

        override fun onOpen(item: Any?) {
            val marker = item as? Marker ?: return
            val view = mView ?: return

            val title = view.findViewById<TextView>(R.id.bubble_title)
            val desc = view.findViewById<TextView>(R.id.bubble_description)
            val button = view.findViewById<Button>(R.id.bubble_button)
            val closeBtn = view.findViewById<ImageView>(R.id.bubble_close)

            title?.text = marker.title ?: "Sin título"
            desc?.text = marker.subDescription ?: "Sin descripción"

            button?.setOnClickListener {
                val lat = marker.position.latitude
                val lon = marker.position.longitude
                val geoUri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lon"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                intent.setPackage("com.google.android.apps.maps")
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)))
                }
            }

            closeBtn?.setOnClickListener {
                close()
            }
        }

        override fun onClose() {
            // Aquí podrías limpiar algo si necesitas
        }
    }
}
