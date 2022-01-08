package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

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

		return binding.root
	}

}
