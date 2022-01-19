package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

	private lateinit var viewModel: SaveReminderViewModel
	private lateinit var dataSource: FakeDataSource

	@ExperimentalCoroutinesApi
	@get:Rule
	var mainCoroutineRule = MainCoroutineRule()

	// Executes each task synchronously using Architecture Components.
	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setupViewModel() {
		GlobalContext.stopKoin()
		dataSource = FakeDataSource()
		viewModel = SaveReminderViewModel(
			ApplicationProvider.getApplicationContext(),
			dataSource
		)
	}

	@Test
	fun saveReminderViewModelTest_reset() = runBlockingTest {
		viewModel.reset()
		Truth.assertThat(viewModel.eventSave.getOrAwaitValue()).isFalse()
		Truth.assertThat(viewModel.addGeofence.getOrAwaitValue()).isFalse()

		val reminder = viewModel.reminderDataItem.getOrAwaitValue()

		Truth.assertThat(reminder.title).isEmpty()
		Truth.assertThat(reminder.description).isEmpty()
		Truth.assertThat(reminder.location).isEmpty()
		Truth.assertThat(reminder.latitude).isZero()
		Truth.assertThat(reminder.longitude).isZero()
	}

	@Test
	fun saveReminderViewModelTest_not_valid_title() = runBlockingTest {
		viewModel.setReminderLocation(ReminderDataItem())
		viewModel.validateAndSaveReminder()

		Truth.assertThat(viewModel.showSnackBarInt.getOrAwaitValue())
			.isEqualTo(R.string.err_enter_title)
	}

	@Test
	fun saveReminderViewModelTest_not_valid_description() = runBlockingTest {
		viewModel.setReminderLocation(ReminderDataItem(title = "title"))
		viewModel.validateAndSaveReminder()

		Truth.assertThat(viewModel.showSnackBarInt.getOrAwaitValue())
			.isEqualTo(R.string.err_enter_description)
	}

	@Test
	fun saveReminderViewModelTest_not_valid_location() = runBlockingTest {
		viewModel.setReminderLocation(
			ReminderDataItem(
				title = "title",
				description = "description"
			)
		)
		viewModel.validateAndSaveReminder()

		Truth.assertThat(viewModel.showSnackBarInt.getOrAwaitValue())
			.isEqualTo(R.string.err_select_location)
	}

	@Test
	fun saveReminderViewModelTest_valid_reminder() = runBlockingTest {
		viewModel.setReminderLocation(
			ReminderDataItem(
				title = "title",
				description = "description",
				location = "location"
			)
		)
		viewModel.validateAndSaveReminder()

		Truth.assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo("Reminder Saved !")
	}

	@Test
	fun remindersListViewModel_loading() = runBlockingTest {
		mainCoroutineRule.pauseDispatcher()
		viewModel.saveReminder(
			ReminderDataItem(
				title = "title",
				description = "description",
				location = "location"
			)
		)
		Truth.assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
		mainCoroutineRule.resumeDispatcher()
		Truth.assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
	}

	@Test
	fun saveReminderViewModelTest_save_reminder() = runBlockingTest {
		mainCoroutineRule.pauseDispatcher()
		viewModel.saveReminder(
			ReminderDataItem(
				title = "title",
				description = "description",
				location = "location"
			)
		)
		mainCoroutineRule.resumeDispatcher()
		Truth.assertThat(viewModel.addGeofence.getOrAwaitValue()).isTrue()
		Truth.assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo("Reminder Saved !")
	}

	@Test
	fun saveReminderViewModelTest_set_location() = runBlockingTest {
		viewModel.setLocation(PointOfInterest(LatLng(1.0, 1.0), "placeId", "placeName"))
		val reminder = viewModel.reminderDataItem.getOrAwaitValue()

		Truth.assertThat(reminder.location).isNotEmpty()
		Truth.assertThat(reminder.latitude).isNonZero()
		Truth.assertThat(reminder.longitude).isNonZero()
	}

	@Test
	fun saveReminderViewModelTest_no_location() = runBlockingTest {
		viewModel.setLocation(null)
		Truth.assertThat(viewModel.showSnackBarInt.getOrAwaitValue())
			.isEqualTo(R.string.err_select_location)
	}
}