package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.R.string
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

	override val _viewModel: SaveReminderViewModel by inject()

	private lateinit var map: GoogleMap

	private var currentLatLng: LatLng? = null

	companion object {
		private const val REQUEST_LOCATION_PERMISSION = 1
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.map_options, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.normal_map -> {
			map.mapType = GoogleMap.MAP_TYPE_NORMAL
			true
		}
		R.id.hybrid_map -> {
			map.mapType = GoogleMap.MAP_TYPE_HYBRID
			true
		}
		R.id.satellite_map -> {
			map.mapType = GoogleMap.MAP_TYPE_SATELLITE
			true
		}
		R.id.terrain_map -> {
			map.mapType = GoogleMap.MAP_TYPE_TERRAIN
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onMapReady(map: GoogleMap) {
		this.map = map

		map.setMapStyle()
		map.setMapClick()

		enableMyLocation()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = FragmentSelectLocationBinding.inflate(layoutInflater).let { binding ->

		binding.viewModel = _viewModel
		binding.lifecycleOwner = this

		binding.saveButton.setOnClickListener {
			onLocationSelected()
		}

		setHasOptionsMenu(true)
		setDisplayHomeAsUpEnabled(true)

		(childFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment)?.apply {
			getMapAsync(this@SelectLocationFragment)
		}

		_viewModel.eventSave.observe(viewLifecycleOwner, {
			if (it) {
				goToSaveScreen()
				_viewModel.onSaveComplete()
			}
		})

		return binding.root
	}

	private fun onLocationSelected() {
		_viewModel.setadas(currentLatLng)
	}

	private fun enableMyLocation() {
		if (ContextCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.ACCESS_FINE_LOCATION
			) == PackageManager.PERMISSION_GRANTED
		) {
			map.isMyLocationEnabled = true
			LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnCompleteListener {
				it.result?.apply {
					map.animateCamera(
						CameraUpdateFactory.newLatLngZoom(
							LatLng(latitude, longitude),
							15f
						)
					)
				}
			}
		} else {
			ActivityCompat.requestPermissions(
				requireActivity(),
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				REQUEST_LOCATION_PERMISSION
			)
		}
	}

	private fun GoogleMap.setMapStyle() {
		try {
			setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
		} catch (e: Exception) {
			Timber.e("Can't find style. Error: ", e)
		}
	}

	private fun GoogleMap.setMapClick() {
		setOnMapClickListener { latLng ->
			currentLatLng = latLng
			clear()
			animateCamera(CameraUpdateFactory.newLatLng(latLng))
			addMarker(
				MarkerOptions()
					.position(latLng)
					.title(getString(string.dropped_pin))
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
			)
		}
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		if (requestCode == REQUEST_LOCATION_PERMISSION
			&& grantResults.isNotEmpty()
			&& (grantResults[0] == PackageManager.PERMISSION_GRANTED)
		) {
			enableMyLocation()
		}
	}

	private fun goToSaveScreen() {
		findNavController().popBackStack()
	}
}
