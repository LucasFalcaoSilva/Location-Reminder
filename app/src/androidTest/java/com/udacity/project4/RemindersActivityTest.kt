package com.udacity.project4

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.EspressoIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
	AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

	private lateinit var viewModel: SaveReminderViewModel
	private lateinit var repository: ReminderDataSource

	private val dataBindingIdlingResource = DataBindingIdlingResource()

	@Before
	fun registerIdlingResource() {
		IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
		IdlingRegistry.getInstance().register(dataBindingIdlingResource)
	}

	@After
	fun unregisterIdlingResource() {
		IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
		IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
	}

	/**
	 * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
	 * at this step we will initialize Koin related code to be able to use it in out testing.
	 */
	@Before
	fun init() {
		stopKoin()//stop the original app koin
		repository = RemindersLocalRepository(
			LocalDB.createRemindersDao(
				getApplicationContext()
			)
		)
		viewModel = SaveReminderViewModel(
			getApplicationContext(),
			repository
		)

		val myModule = module {
			single {
				viewModel
			}
			viewModel {
				RemindersListViewModel(
					getApplicationContext(),
					repository
				)
			}
		}

		startKoin {
			androidContext(getApplicationContext())
			modules(listOf(myModule))
		}

		//clear the data to start fresh
		runBlocking {
			repository.deleteAllReminders()
		}
	}

	@Test
	fun emptyList_addReminder_hasList() = runBlocking {
		viewModel.setLocation(PointOfInterest(LatLng(1.0, 1.0), "placeId", "placeName"))

		val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
		dataBindingIdlingResource.monitorActivity(activityScenario)

		Espresso.onView(withId(R.id.noDataTextView)).check(
			ViewAssertions.matches(
				ViewMatchers.isDisplayed()
			)
		)

		Espresso.onView(withId(R.id.addReminderFAB)).perform(click())

		Espresso.onView(withId(R.id.reminderTitle)).perform(ViewActions.typeText("Title"))
		Espresso.onView(withId(R.id.reminderDescription))
			.perform(ViewActions.typeText("Description"))

		Espresso.closeSoftKeyboard()

		Espresso.onView(withId(R.id.saveReminder)).perform(click())

		activityScenario.close()
	}

}
