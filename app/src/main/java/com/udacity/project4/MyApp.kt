package com.udacity.project4

import android.app.Application
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

import timber.log.Timber.*


class MyApp : Application() {

	override fun onCreate() {
		super.onCreate()
		if (BuildConfig.DEBUG) {
			Timber.plant(DebugTree())
		}

		/**
		 * use Koin Library as a service locator
		 */
		val myModule = module {
			single { LocalDB.createRemindersDao(this@MyApp) }
			single<ReminderDataSource> { RemindersLocalRepository(get()) }
			single {
				SaveReminderViewModel(
					get(),
					get()
				)
			}
			viewModel {
				RemindersListViewModel(
					this@MyApp,
					get()
				)
			}

		}

		startKoin {
			androidContext(this@MyApp)
			modules(listOf(myModule))
		}
	}
}