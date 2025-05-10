package es.uniovi.asturnatura

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testToolbarSeMuestra() {
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerViewVisible() {
        onView(withId(R.id.recyclerEspacios))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBuscarPlaya_filtraCorrectamente() {
        // Pulsa el icono de búsqueda en el Toolbar
        onView(withContentDescription("Buscar")).perform(click())

        // Escribe "playa" en el campo de búsqueda
        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(typeText("playa"))

        // Verifica que al menos un ítem del RecyclerView contiene "playa" en cualquier TextView
        onView(withId(R.id.recyclerEspacios))
            .check(matches(hasDescendant(withTextIgnoreCase("playa"))))
    }

    // Matcher personalizado que ignora mayúsculas/minúsculas
    private fun withTextIgnoreCase(substring: String): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("TextView con texto que contiene \"$substring\" ignorando mayúsculas")
            }

            override fun matchesSafely(textView: TextView): Boolean {
                return textView.text.toString().lowercase().contains(substring.lowercase())
            }
        }
    }
}
