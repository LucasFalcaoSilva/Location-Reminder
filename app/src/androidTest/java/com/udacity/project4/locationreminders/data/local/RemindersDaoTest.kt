package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {


	private lateinit var database: RemindersDatabase

	// Executes each task synchronously using Architecture Components.
	@get:Rule
	var instantExecutorRule = InstantTaskExecutorRule()

	@Before
	fun initDb() {
		database = Room.inMemoryDatabaseBuilder(
			ApplicationProvider.getApplicationContext(),
			RemindersDatabase::class.java
		).build()
	}

	@After
	fun closeDb() = database.close()

	@Test
	fun insertReminder() = runBlockingTest {
		val reminder = ReminderDTO(
			title = "title",
			description = "description",
			location = "location",
			latitude = .0,
			longitude = 1.0
		)
		database.reminderDao().saveReminder(reminder)

		val loaded = database.reminderDao().getReminderById(reminder.id)

		assertThat(loaded as ReminderDTO, notNullValue())
		assertThat(loaded.id, `is`(reminder.id))
		assertThat(loaded.title, `is`(reminder.title))
		assertThat(loaded.description, `is`(reminder.description))
		assertThat(loaded.location, `is`(reminder.location))
	}

	@Test
	fun deleteAllReminders() = runBlockingTest {
		database.reminderDao().deleteAllReminders()
		val result = database.reminderDao().getReminders()
		assert(result.isNullOrEmpty())
	}

	@Test
	fun gettReminderList() = runBlockingTest {
		database.reminderDao().deleteAllReminders()

		database.reminderDao().saveReminder(
			ReminderDTO(
				title = "title",
				description = "description",
				location = "location",
				latitude = .0,
				longitude = 1.0
			)
		)
		database.reminderDao().saveReminder(
			ReminderDTO(
				title = "title",
				description = "description",
				location = "location",
				latitude = .0,
				longitude = 1.0
			)
		)

		val result = database.reminderDao().getReminders()
		assertThat(result.size, `is`(2))
	}
}