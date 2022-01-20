package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class SaveReminderFragmentTest {

	private lateinit var viewModel: SaveReminderViewModel

	@get:Rule
	val instantTaskExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setup() {
		stopKoin()

		viewModel = SaveReminderViewModel(
			ApplicationProvider.getApplicationContext(),
			RemindersLocalRepository(
				LocalDB.createRemindersDao(
					ApplicationProvider.getApplicationContext()
				)
			)
		)

		val myModule = module {
			single {
				viewModel
			}
		}

		startKoin {
			androidContext(ApplicationProvider.getApplicationContext())
			modules(listOf(myModule))
		}
	}

	@Test
	fun saveReminderFragmentTest_clicksaveButton_saveReminder() {

		viewModel.setLocation(PointOfInterest(LatLng(1.0, 1.0), "placeId", "placeName"))

		val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
		val navController = Mockito.mock(NavController::class.java)

		scenario.onFragment {
			Navigation.setViewNavController(it.view!!, navController)
		}


		Espresso.onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
		Espresso.onView(withId(R.id.reminderDescription)).perform(typeText("Description"))

		Espresso.closeSoftKeyboard()

		Espresso.onView(withId(R.id.saveReminder)).perform(click())

		assertThat(viewModel.showToast.getOrAwaitValue(), CoreMatchers.`is`("Reminder Saved !"))
	}

	@Test
	fun saveReminderFragmentTest_clicksaveButton_saveReminder_location_failed() {
		viewModel.setLocation(null)
		val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
		val navController = Mockito.mock(NavController::class.java)

		scenario.onFragment {
			Navigation.setViewNavController(it.view!!, navController)
		}


		Espresso.onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
		Espresso.onView(withId(R.id.reminderDescription)).perform(typeText("Description"))

		Espresso.closeSoftKeyboard()

		Espresso.onView(withId(R.id.saveReminder)).perform(click())

		assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), CoreMatchers.`is`(R.string.err_select_location))
	}

	@Test
	fun saveReminderFragmentTest_clicksaveButton_saveReminder_title_failed() {
		viewModel.setLocation(null)
		val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
		val navController = Mockito.mock(NavController::class.java)

		scenario.onFragment {
			Navigation.setViewNavController(it.view!!, navController)
		}

		Espresso.closeSoftKeyboard()

		Espresso.onView(withId(R.id.saveReminder)).perform(click())

		assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), CoreMatchers.`is`(R.string.err_enter_title))
	}

}