package es.uniovi.asturnatura

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetalleEspacioNavigationTest {

    @Test
    fun testClickRecyclerItem_navegaADetalle() {
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(3000)

        onView(withId(R.id.recyclerEspacios))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                    0, click()
                )
            )

        onView(withId(R.id.tvDetalleNombre)).check(matches(isDisplayed()))
    }
}
