package com.udacity.project4.locationreminders.geofence

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.errorMessage
import com.udacity.project4.utils.sendNotification
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

	private var coroutineJob: Job = Job()
	override val coroutineContext: CoroutineContext
		get() = Dispatchers.IO + coroutineJob

	private val remindersLocalRepository by inject<ReminderDataSource>()

	companion object {
		private const val JOB_ID = 573

		fun enqueueWork(context: Context, intent: Intent) {
			enqueueWork(
				context,
				GeofenceTransitionsJobIntentService::class.java, JOB_ID,
				intent
			)
		}
	}

	override fun onHandleWork(intent: Intent) {
		Timber.v("onHandleWork")
		val event = GeofencingEvent.fromIntent(intent)

		if (event.hasError()) {
			Timber.e(
				applicationContext.resources.errorMessage(event.errorCode)
			)
			return
		}

		if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
			Timber.v(getString(R.string.geofence_entered))
			event.triggeringGeofences.map {
				Timber.v("triggeringGeofences ${it.requestId}")
				sendNotification(it.requestId)
			}
		}
	}

	private fun sendNotification(requestId: String) {
		Timber.v("start sendNotification")
		CoroutineScope(coroutineContext).launch(SupervisorJob()) {
			//get the reminder with the request id
			val result = remindersLocalRepository.getReminder(requestId)
			if (result is Result.Success<ReminderDTO>) {
				Timber.v("sendNotification ${result.data}")
				val reminderDTO = result.data
				//send a notification to the user with the reminder details
				sendNotification(
					this@GeofenceTransitionsJobIntentService, ReminderDataItem(
						reminderDTO.title,
						reminderDTO.description,
						reminderDTO.location,
						reminderDTO.latitude,
						reminderDTO.longitude,
						reminderDTO.id
					)
				)
			} else {
				Timber.v("do not sendNotification")
			}
		}
	}

}