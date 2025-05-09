package es.uniovi.asturnatura

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import es.uniovi.asturnatura.viewmodel.EspaciosViewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var vm: EspaciosViewModel
    private val filtrosActivos = mutableSetOf<String>()

    // Se aplica el idioma guardado ANTES de crear vistas
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("ajustes", Context.MODE_PRIVATE)
        val lang = prefs.getString("idioma", "es") ?: "es" // Idioma por defecto: español
        val context = newBase.setLocale(lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // Icono cambia automáticamente por tema

        toolbar.setNavigationOnClickListener {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: android.view.View) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        })

        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavView.setupWithNavController(navController)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appBarLayout)) { view, insets ->
            view.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavView) { view, insets ->
            view.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
            insets
        }

        val headerView = navView.getHeaderView(0)
        ViewCompat.setOnApplyWindowInsetsListener(headerView) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(view.paddingLeft, topInset, view.paddingRight, view.paddingBottom)
            insets
        }

        vm = ViewModelProvider(this)[EspaciosViewModel::class.java]
        navView.menu.setGroupCheckable(R.id.group_filters, true, false)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_aplicar_filtros -> {
                    vm.actualizarFiltroCategorias(filtrosActivos)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.btn_limpiar_filtros -> {
                    filtrosActivos.clear()
                    for (i in 0 until navView.menu.size()) {
                        val item = navView.menu.getItem(i)
                        if (item.groupId == R.id.group_filters) {
                            item.isChecked = false
                        }
                    }
                    vm.actualizarFiltroCategorias(filtrosActivos)
                    true
                }
                else -> {
                    val nombre = menuItem.title.toString()
                    menuItem.isChecked = !menuItem.isChecked
                    if (menuItem.isChecked) {
                        filtrosActivos.add(nombre)
                    } else {
                        filtrosActivos.remove(nombre)
                    }
                    false
                }
            }
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

    // Función de extensión para aplicar el idioma
    fun Context.setLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return createConfigurationContext(config)
    }
}
