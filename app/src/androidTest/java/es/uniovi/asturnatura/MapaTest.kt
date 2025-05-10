package es.uniovi.asturnatura

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.rules.ActivityScenarioRule


@RunWith(AndroidJUnit4::class)
class MapaTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testMapaSeMuestra() {
        onView(withId(R.id.nav_mapa)).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed())) // `map` es el ID del fragmento de mapa
    }

}
