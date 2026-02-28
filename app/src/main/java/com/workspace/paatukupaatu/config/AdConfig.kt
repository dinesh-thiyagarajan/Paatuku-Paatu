package com.workspace.paatukupaatu.config

import com.workspace.paatukupaatu.BuildConfig

object AdConfig {
    val adsEnabled: Boolean
        get() = try {
            BuildConfig.ADS_ENABLED.toBoolean()
        } catch (_: Exception) {
            false
        }
}
