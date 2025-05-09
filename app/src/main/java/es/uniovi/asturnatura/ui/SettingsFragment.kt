package es.uniovi.asturnatura.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.switchmaterial.SwitchMaterial
import es.uniovi.asturnatura.R
import es.uniovi.asturnatura.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val switchNight: SwitchMaterial = view.findViewById(R.id.switchNightMode)
        val switchLanguage: SwitchMaterial = view.findViewById(R.id.switchLanguage)

        var ignoreNightChange = false
        var ignoreLanguageChange = false

        viewModel.isNightMode.observe(viewLifecycleOwner) {
            if (switchNight.isChecked != it) {
                ignoreNightChange = true
                switchNight.isChecked = it
                ignoreNightChange = false
            }
        }

        viewModel.isEnglish.observe(viewLifecycleOwner) {
            if (switchLanguage.isChecked != it) {
                ignoreLanguageChange = true
                switchLanguage.isChecked = it
                ignoreLanguageChange = false
            }
        }

        switchNight.setOnCheckedChangeListener { _, isChecked ->
            if (!ignoreNightChange) {
                viewModel.toggleNightMode(isChecked)
            }
        }

        switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            if (!ignoreLanguageChange) {
                val haCambiado = viewModel.toggleLanguage(isChecked)
                if (haCambiado) {
                    view?.post {
                        if (isAdded && activity != null) {
                            requireActivity().recreate()
                        }
                    }
                }
            }
        }
    }
}
