package es.uniovi.asturnatura

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class PreferenciasTest {

    @Test
    fun testModoOscuroPorDefectoEsFalse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val nightMode = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("night_mode", false)
        assertFalse("El modo oscuro debería estar desactivado por defecto", nightMode)
    }

    @Test
    fun testIdiomaActualEstaConfigurado() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config: Configuration = context.resources.configuration
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            config.locale
        }

        assertTrue("Debe haber un idioma configurado", locale.language.isNotBlank())
    }

    @Test
    fun testSimulacionCambioIngles() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val configIngles = Configuration(context.resources.configuration)
        configIngles.setLocales(LocaleList(Locale.ENGLISH))
        val contextIngles = context.createConfigurationContext(configIngles)

        val idioma = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            contextIngles.resources.configuration.locales.get(0).language
        } else {
            @Suppress("DEPRECATION")
            contextIngles.resources.configuration.locale.language
        }

        assertEquals("en", idioma)
    }
}
