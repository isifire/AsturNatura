package es.uniovi.asturnatura

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var vm: EspaciosViewModel
    private val filtrosActivos = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavView.setupWithNavController(navController)

        vm = ViewModelProvider(this)[EspaciosViewModel::class.java]
        navView.menu.setGroupCheckable(R.id.group_filters, true, false)

        // Controlador del menú de filtros
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filtro_playa -> toggleFiltro("Playa", menuItem)
                R.id.filtro_parque -> toggleFiltro("Parque", menuItem)
                R.id.filtro_area -> toggleFiltro("Área Recreativa", menuItem)
                R.id.filtro_picos -> toggleFiltro("Picos", menuItem)
                R.id.filtro_lago -> toggleFiltro("Lago", menuItem)
                R.id.filtro_rio -> toggleFiltro("Río", menuItem)
                R.id.filtro_todo -> {
                    filtrosActivos.clear()
                    for (i in 0 until navView.menu.size()) {
                        navView.menu.getItem(i).isChecked = false
                    }
                }
            }
            val cb = menuItem.actionView?.findViewById<CheckBox>(R.id.menu_item_checkbox)
            if (cb != null) {
                cb.isChecked = menuItem.isChecked
            }

            vm.actualizarFiltroCategorias(filtrosActivos)

            true
        }
    }

    private fun toggleFiltro(nombre: String, item: MenuItem) {
        item.isChecked = !item.isChecked
        if (item.isChecked) {
            filtrosActivos.add(nombre)
        } else {
            filtrosActivos.remove(nombre)
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
