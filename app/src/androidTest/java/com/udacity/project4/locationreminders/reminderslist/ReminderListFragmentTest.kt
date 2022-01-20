package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

	private lateinit var dataSource: ReminderDataSource

	@get:Rule
	val instantTaskExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setup() {
		stopKoin()

		dataSource = RemindersLocalRepository(
			LocalDB.createRemindersDao(
				ApplicationProvider.getApplicationContext()
			)
		)

		val myModule = module {
			viewModel {
				RemindersListViewModel(
					ApplicationProvider.getApplicationContext(),
					dataSource
				)
			}
		}

		startKoin {
			androidContext(ApplicationProvider.getApplicationContext())
			modules(listOf(myModule))
		}

		runBlocking {
			dataSource.deleteAllReminders()
		}
	}

	@Test
	fun reminderListFragmentTest_clickAddReminderButton_navigateToAddSaveFragment() {
		val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
		val navController = Mockito.mock(NavController::class.java)

		scenario.onFragment {
			Navigation.setViewNavController(it.view!!, navController)
		}

		Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

		Mockito.verify(navController).navigate(
			ReminderListFragmentDirections.toSaveReminder()
		)
	}


	@Test
	fun reminderListFragmentTest_showsData() {
		val data = ReminderDTO(
			title = "title",
			description = "description",
			location = "location",
			latitude = 2.0,
			longitude = 1.0
		)
		runBlocking {
			dataSource.saveReminder(data)
		}

		val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
		val navController = Mockito.mock(NavController::class.java)

		scenario.onFragment {
			Navigation.setViewNavController(it.view!!, navController)
		}

		Espresso.onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
	}

	@Test
	fun reminderListFragmentTest_NoData() {
		runBlocking {
			dataSource.deleteAllReminders()
		}

		val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
		val navController = Mockito.mock(NavController::class.java)

		scenario.onFragment {
			Navigation.setViewNavController(it.view!!, navController)
		}

		Espresso.onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
	}

}