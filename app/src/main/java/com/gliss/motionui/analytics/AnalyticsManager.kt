package com.gliss.motionui.analytics

import android.util.Log

/**
 * Production-ready Analytics abstraction.
 * Easily swappable with Firebase, Mixpanel, or Amplitude.
 */
object AnalyticsManager {
    private const val TAG = "TouchraAnalytics"

    fun logEvent(name: String, params: Map<String, Any> = emptyMap()) {
        Log.d(TAG, "Event: $name | Params: $params")
    }

    fun logScreenView(screenName: String) {
        logEvent("screen_view", mapOf("screen_name" to screenName))
    }

    fun logGesture(type: String, target: String) {
        logEvent("gesture_interaction", mapOf("type" to type, "target" to target))
    }

    fun logConversion(tier: String, price: Double) {
        logEvent("premium_conversion", mapOf("tier" to tier, "revenue" to price))
    }
}
