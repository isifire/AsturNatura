package es.uniovi.asturnatura

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import es.uniovi.asturnatura.ui.EspaciosFragment
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel

class MainActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    fun isOnline(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private lateinit var vm: EspaciosViewModel  // Fuera de onCreate, en la clase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val online = isOnline()
        Log.d("MainActivity", "¿Conectado a Internet? $online")

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        // ✅ ViewModel compartido y único
        vm = ViewModelProvider(this)[EspaciosViewModel::class.java]

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filtro_todo -> vm.actualizarFiltroCategoria("")
                R.id.filtro_playa -> vm.actualizarFiltroCategoria("Playa")
                R.id.filtro_parque -> vm.actualizarFiltroCategoria("Parque")
                R.id.filtro_area -> vm.actualizarFiltroCategoria("Área Recreativa")
                R.id.filtro_picos -> vm.actualizarFiltroCategoria("Picos")
            }
            drawerLayout.closeDrawers()
            true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                vm.actualizarTextoBusqueda(newText ?: "")
                return true
            }
        })
        return true
    }


}
