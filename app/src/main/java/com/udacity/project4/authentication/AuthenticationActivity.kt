package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.utils.openReminders
import timber.log.Timber

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

	companion object {
		const val SIGN_IN_RESULT_CODE = 1001
	}

	private val viewModel by viewModels<AuthenticationViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ActivityAuthenticationBinding.inflate(layoutInflater).let { binding ->
			setContentView(binding.root)
			binding.lifecycleOwner = this

			binding.authButton.setOnClickListener {
				startActivityForResult(
					AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
						arrayListOf(
							AuthUI.IdpConfig.EmailBuilder().build(),
							AuthUI.IdpConfig.GoogleBuilder().build()
						)
					).build(), SIGN_IN_RESULT_CODE
				)
			}

			viewModel.authenticationState.observe(this, { authenticationState ->
				when (authenticationState) {
					AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
						Timber.i("open Reminder Activity")
						openReminders()
					}
					else -> Timber.i("authenticationState: $authenticationState")
				}
			})

		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == SIGN_IN_RESULT_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				Timber.i("Successfully signed in user")
			} else {
				Timber.e("Sign in unsuccessful ${IdpResponse.fromResultIntent(data)?.error?.message}")
			}
		}
	}
}
