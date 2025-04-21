package es.uniovi.asturnatura

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.uniovi.asturnatura.ui.EspaciosFragment

// LLAMADA A LA API
// https://www.turismoasturiasprofesional.es/documents/portlet_file_entry/39908/espacios-naturales.json/580094f8-4608-d911-f4de-da4c152073e7?download=true
class MainActivity : AppCompatActivity() {
    @Suppress("DEPRECATION")
    fun isOnline(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)  // <-- primero esto

        val online = isOnline()
        Log.d("MainActivity", "¿Conectado a Internet? $online")

        supportFragmentManager.beginTransaction()
            .replace(R.id.main, EspaciosFragment())
            .commit()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}