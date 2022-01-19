package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

	private lateinit var viewModel: RemindersListViewModel
	private lateinit var dataSource: FakeDataSource

	@ExperimentalCoroutinesApi
	@get:Rule
	var mainCoroutineRule = MainCoroutineRule()

	// Executes each task synchronously using Architecture Components.
	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setupViewModel() {
		stopKoin()
		dataSource = FakeDataSource()
		viewModel = RemindersListViewModel(
			ApplicationProvider.getApplicationContext(),
			dataSource
		)
	}

	@Test
	fun remindersListViewModel_has_error() = runBlockingTest {
		dataSource.shouldReturnError = true
		viewModel.loadReminders()

		assertThat(viewModel.showSnackBar.getOrAwaitValue()).isNotEmpty()
	}

	@Test
	fun remindersListViewModel_loading() = runBlockingTest {
		mainCoroutineRule.pauseDispatcher()
		viewModel.loadReminders()

		assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

		mainCoroutineRule.resumeDispatcher()

		assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
	}

	@Test
	fun remindersListViewModel_no_data() = runBlockingTest {
		dataSource.deleteAllReminders()
		viewModel.loadReminders()

		assertThat(viewModel.remindersList.getOrAwaitValue().isEmpty()).isTrue()
		assertThat(viewModel.showNoData.getOrAwaitValue()).isTrue()
	}

	@Test
	fun remindersListViewModel_has_data() = runBlockingTest {
		dataSource.saveReminder(
			ReminderDTO(
				"Reminder Title",
				"Reminder description",
				"Reminder location",
				1.0,
				1.0
			)
		)

		viewModel.loadReminders()

		assertThat(viewModel.remindersList.getOrAwaitValue().isEmpty()).isFalse()
		assertThat(viewModel.showNoData.getOrAwaitValue()).isFalse()
	}
}