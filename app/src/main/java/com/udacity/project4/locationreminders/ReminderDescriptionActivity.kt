package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

	companion object {
		private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

		fun newIntent(context: Context, reminderDataItem: ReminderDataItem) =
			Intent(context, ReminderDescriptionActivity::class.java).apply {
				putExtra(EXTRA_ReminderDataItem, reminderDataItem)
			}


	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ActivityReminderDescriptionBinding.inflate(layoutInflater).let { binding ->
			setContentView(binding.root)
			(intent.extras?.getSerializable(EXTRA_ReminderDataItem) as? ReminderDataItem)?.apply {
				binding.reminderDataItem = this
			}
		}
	}
}
