package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.R.string
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(val app: Application, val dataSource: ReminderDataSource) :
	BaseViewModel(app) {

	private var _reminderDataItem = MutableLiveData<ReminderDataItem>()
	val reminderDataItem: LiveData<ReminderDataItem>
		get() = _reminderDataItem

	private var _eventSave = MutableLiveData<Boolean>()
	val eventSave: LiveData<Boolean>
		get() = _eventSave

	private var _addGeofence = MutableLiveData<Boolean>()
	val addGeofence: LiveData<Boolean>
		get() = _addGeofence

	init {
		reset()
	}

	fun reset(){
		showSnackBarInt.postValue(0)
		_reminderDataItem.postValue(ReminderDataItem())
		_eventSave.postValue(false)
		_addGeofence.postValue(false)
	}

	fun validateAndSaveReminder() {
		_reminderDataItem.value?.apply {
			if (validateEnteredData(this)) {
				saveReminder(this)
			}
		}
	}

	/**
	 * Save the reminder to the data source
	 */
	fun saveReminder(reminderData: ReminderDataItem) {
		showLoading.value = true
		_addGeofence.value = true
		viewModelScope.launch {
			dataSource.saveReminder(
				ReminderDTO(
					reminderData.title,
					reminderData.description,
					reminderData.location,
					reminderData.latitude,
					reminderData.longitude,
					reminderData.id
				)
			)
			_reminderDataItem.value = ReminderDataItem()
			showLoading.value = false
			showToast.value = app.getString(R.string.reminder_saved)
			navigationCommand.value = NavigationCommand.Back
		}
	}

	/**
	 * Validate the entered data and show error to the user if there's any invalid data
	 */
	fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
		if (reminderData.title.isNullOrBlank()) {
			showSnackBarInt.value = R.string.err_enter_title
			return false
		}

		if (reminderData.description.isNullOrBlank()) {
			showSnackBarInt.value = R.string.err_enter_description
			return false
		}

		if (reminderData.location.isNullOrBlank()) {
			showSnackBarInt.value = R.string.err_select_location
			return false
		}
		return true
	}

	fun setReminderLocation(reminderData: ReminderDataItem) {
		_reminderDataItem.value = reminderData
	}

	fun setLocation(location: PointOfInterest?) {
		location?.apply {
			_reminderDataItem.value?.latitude = latLng.latitude
			_reminderDataItem.value?.longitude = latLng.longitude
			_reminderDataItem.value?.location = name

			_eventSave.postValue(true)

		} ?: kotlin.run {
			showSnackBarInt.value = string.err_select_location
		}
	}

	fun onSaveComplete() {
		_eventSave.postValue(false)
	}

	fun onAddfenceComplete() {
		_addGeofence.postValue(false)
	}
}