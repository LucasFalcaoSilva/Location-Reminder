package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {


	private lateinit var localDataSource: RemindersLocalRepository
	private lateinit var database: RemindersDatabase

	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun setup() {
		database = Room.inMemoryDatabaseBuilder(
			ApplicationProvider.getApplicationContext(),
			RemindersDatabase::class.java
		).allowMainThreadQueries().build()

		localDataSource = RemindersLocalRepository(
			database.reminderDao(),
			Dispatchers.Main
		)
	}

	@After
	fun cleanUp() {
		database.close()
	}

	@Test
	fun saveReminder_retrievesReminders() = runBlocking {
		val reminder = ReminderDTO(
			title = "title",
			description = "description",
			location = "location",
			latitude = 2.0,
			longitude = 1.0
		)
		localDataSource.saveReminder(reminder)

		val result = localDataSource.getReminder(reminder.id)

		Assert.assertThat(result.succeeded, `is`(true))
		result as Success
		Assert.assertThat(result.data.title, `is`(reminder.title))
		Assert.assertThat(result.data.description, `is`(reminder.description))
		Assert.assertThat(result.data.location, `is`(reminder.location))
		Assert.assertThat(result.data.longitude, `is`(reminder.longitude))
		Assert.assertThat(result.data.latitude, `is`(reminder.latitude))
	}

	@Test
	fun saveReminders_retrievedReminders() = runBlocking {
		localDataSource.deleteAllReminders()

		localDataSource.saveReminder(ReminderDTO(
			title = "title",
			description = "description",
			location = "location",
			latitude = 2.0,
			longitude = 1.0
		))
		localDataSource.saveReminder(ReminderDTO(
			title = "title",
			description = "description",
			location = "location",
			latitude = 2.0,
			longitude = 1.0
		))
		val result = localDataSource.getReminders()

		Assert.assertThat(result.succeeded, `is`(true))
		result as Success
		Assert.assertThat(result.data.size, `is`(2))
	}

	@Test
	fun saveReminders_deleteReminders() = runBlocking {
		localDataSource.saveReminder(ReminderDTO(
			title = "title",
			description = "description",
			location = "location",
			latitude = 2.0,
			longitude = 1.0
		))

		localDataSource.deleteAllReminders()
		val result = localDataSource.getReminders()

		Assert.assertThat(result.succeeded, `is`(true))
		result as Success
		Assert.assertThat(result.data.size, `is`(0))
	}
}