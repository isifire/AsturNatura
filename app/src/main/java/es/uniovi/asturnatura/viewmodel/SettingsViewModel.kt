package es.uniovi.asturnatura.viewmodel

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("ajustes", Context.MODE_PRIVATE)

    private val _isNightMode = MutableLiveData<Boolean>()
    val isNightMode: LiveData<Boolean> = _isNightMode

    private val _isEnglish = MutableLiveData<Boolean>()
    val isEnglish: LiveData<Boolean> = _isEnglish

    init {
        _isNightMode.value = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        _isEnglish.value = prefs.getString("idioma", "es") == "en"
    }

    fun toggleNightMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        _isNightMode.value = enabled
    }

    /**
     * Cambia el idioma y devuelve true si hubo cambio real.
     */
    fun toggleLanguage(useEnglish: Boolean): Boolean {
        val nuevoIdioma = if (useEnglish) "en" else "es"
        val actual = prefs.getString("idioma", "es")
        val haCambiado = actual != nuevoIdioma

        if (haCambiado) {
            prefs.edit().putString("idioma", nuevoIdioma).apply()
            _isEnglish.value = useEnglish
        }

        return haCambiado
    }
}
