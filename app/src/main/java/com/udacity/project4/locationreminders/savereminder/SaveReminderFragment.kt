package com.udacity.project4.locationreminders.savereminder

import android.Manifest.permission
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber

class SaveReminderFragment : BaseFragment() {

	override val _viewModel: SaveReminderViewModel by inject()


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View = FragmentSaveReminderBinding.inflate(layoutInflater).let { binding ->

		setDisplayHomeAsUpEnabled(true)

		binding.lifecycleOwner = this
		binding.viewModel = _viewModel

		binding.selectLocation.setOnClickListener {
			_viewModel.navigationCommand.value = NavigationCommand.To(
				SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment()
			)
		}

		_viewModel.addGeofence.observe(viewLifecycleOwner, {
			if (it) {
				_viewModel.reminderDataItem.value?.apply {
					addGeofence(this)
				}
				_viewModel.onAddfenceComplete()
			}
		})

		return binding.root
	}

	private fun addGeofence(reminderData: ReminderDataItem) {
		if (ActivityCompat.checkSelfPermission(
				requireContext(),
				permission.ACCESS_FINE_LOCATION
			) == PackageManager.PERMISSION_GRANTED
		) {
			Timber.v("Adding Geofence")
			val client = LocationServices.getGeofencingClient(requireActivity())

			val geofencePendingIntent = PendingIntent.getBroadcast(
				requireContext(),
				0,
				Intent(requireContext(), GeofenceBroadcastReceiver::class.java).apply {
					action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
				},
				PendingIntent.FLAG_UPDATE_CURRENT
			)

			val geofence = Geofence.Builder()
				.setRequestId(reminderData.id)
				.setCircularRegion(
					reminderData.latitude ?: 0.0,
					reminderData.longitude ?: 0.0,
					5000f
				)
				.setExpirationDuration(Geofence.NEVER_EXPIRE)
				.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
				.build()

			val geofencingRequest = GeofencingRequest.Builder()
				.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
				.addGeofence(geofence)
				.build()


			client.addGeofences(geofencingRequest, geofencePendingIntent).run {
				addOnSuccessListener {
					Timber.v("addOnSuccess - Added geofence")
				}
				addOnFailureListener {
					Timber.e(it,"addOnFailure - Added geofence")
				}
			}
		} else {
			Timber.v("We do not have permission for addGeofence")
		}

	}
}
