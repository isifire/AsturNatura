package es.uniovi.asturnatura.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import es.uniovi.asturnatura.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val switchNight: SwitchMaterial = view.findViewById(R.id.switchNightMode)
        val switchLanguage: SwitchMaterial = view.findViewById(R.id.switchLanguage)

        // Modo noche
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        switchNight.isChecked = (currentMode == AppCompatDelegate.MODE_NIGHT_YES)

        switchNight.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO

            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Modo idioma
        val prefs = requireContext().getSharedPreferences("settings", 0)
        val idioma = prefs.getString("idioma", "es")
        switchLanguage.isChecked = (idioma == "en")

        switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val nuevoIdioma = if (isChecked) "en" else "es"
            prefs.edit().putString("idioma", nuevoIdioma).apply()

            val locale = java.util.Locale(nuevoIdioma)
            java.util.Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            requireActivity().baseContext.resources.updateConfiguration(
                config,
                requireActivity().baseContext.resources.displayMetrics
            )

            // Reinicia la actividad para aplicar el idioma
            requireActivity().recreate()
        }
    }

}