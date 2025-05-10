package es.uniovi.asturnatura

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FiltrosTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testFiltroCategoriaLagoFunciona() {
        // Abrir el menú lateral (drawer)
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())

        // Marcar el filtro "Lago"
        onView(withText("Lago")).perform(click())

        // Pulsar el botón de aplicar filtros
        onView(withId(R.id.btn_aplicar_filtros)).perform(click())

        // Esperar a que se actualice la vista
        Thread.sleep(1500)

        // Comprobar que aparece un elemento con la palabra Lago
        onView(withId(R.id.recyclerEspacios))
            .check(matches(hasDescendant(withText(containsString("Lago")))))
    }

}
