package com.udacity.project4.locationreminders.reminderslist

import java.io.Serializable
import java.util.UUID

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
	var title: String? = "",
	var description: String? = "",
	var location: String? = "",
	var latitude: Double? = 0.0,
	var longitude: Double? = 0.0,
	val id: String = UUID.randomUUID().toString()
) : Serializable