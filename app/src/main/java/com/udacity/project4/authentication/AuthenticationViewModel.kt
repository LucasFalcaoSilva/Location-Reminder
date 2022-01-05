package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import timber.log.Timber

class AuthenticationViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    init {
        Timber.i("AuthenticationViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("AuthenticationViewModel destroyed!")
    }
}