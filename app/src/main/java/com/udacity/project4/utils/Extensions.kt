package com.udacity.project4.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.GeofenceStatusCodes
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.base.BaseRecyclerViewAdapter
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * Extension function to setup the RecyclerView
 */
fun <T> RecyclerView.setup(
	adapter: BaseRecyclerViewAdapter<T>
) {
	this.apply {
		layoutManager = LinearLayoutManager(this.context)
		this.adapter = adapter
	}
}

fun Fragment.setTitle(title: String) {
	if (activity is AppCompatActivity) {
		(activity as AppCompatActivity).supportActionBar?.title = title
	}
}

fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
	if (activity is AppCompatActivity) {
		(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
			bool
		)
	}
}

//animate changing the view visibility
fun View.fadeIn() {
	this.visibility = View.VISIBLE
	this.alpha = 0f
	this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
		override fun onAnimationEnd(animation: Animator) {
			this@fadeIn.alpha = 1f
		}
	})
}

//animate changing the view visibility
fun View.fadeOut() {
	this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
		override fun onAnimationEnd(animation: Animator) {
			this@fadeOut.alpha = 1f
			this@fadeOut.visibility = View.GONE
		}
	})
}

fun Intent.clearStack() {
	flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
}

fun Activity.openIntent(cls: Class<*>) = Intent(this, cls).apply { clearStack() }

fun Activity.openAuthentication() {
	startActivity(openIntent(AuthenticationActivity::class.java))
}

fun Activity.openReminders() {
	startActivity(openIntent(RemindersActivity::class.java))
}

fun Resources.errorMessage(errorCode: Int) = getString(
	when (errorCode) {
		GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> R.string.geofence_not_available
		GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> R.string.geofence_too_many_geofences
		GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> R.string.geofence_too_many_pending_intents
		else -> R.string.error_adding_geofence
	}
)