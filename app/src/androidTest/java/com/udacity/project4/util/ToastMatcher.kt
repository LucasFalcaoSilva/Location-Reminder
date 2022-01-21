package com.udacity.project4.util

import android.view.WindowManager.LayoutParams
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.junit.internal.matchers.TypeSafeMatcher

class ToastMatcher : TypeSafeMatcher<Root>() {

	override fun describeTo(description: Description) {}

	override fun matchesSafely(item: Root): Boolean {
		if (item.windowLayoutParams.get().type == LayoutParams.TYPE_TOAST) {
			return item.decorView.windowToken === item.decorView.applicationWindowToken
		}
		return false
	}

}