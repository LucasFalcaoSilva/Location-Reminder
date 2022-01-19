package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource() : ReminderDataSource {

	var shouldReturnError = false

	private val reminderList: MutableList<ReminderDTO> = mutableListOf()

	override suspend fun getReminders(): Result<List<ReminderDTO>> = if (shouldReturnError) {
		Result.Error("Error getReminders")
	} else {
		Result.Success(reminderList)
	}


	override suspend fun saveReminder(reminder: ReminderDTO) {
		reminderList.add(reminder)
	}

	override suspend fun getReminder(id: String): Result<ReminderDTO> {
		TODO("return the reminder with the id")
	}

	override suspend fun deleteAllReminders() {
		reminderList.clear()
	}

}