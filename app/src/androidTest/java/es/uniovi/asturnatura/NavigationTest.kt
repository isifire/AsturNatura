package es.uniovi.asturnatura

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @Test
    fun testLaunchMainActivity_ShowsBottomNavigation() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.bottom_nav_view)).check(matches(isDisplayed()))
    }
}
