package com.musicplayer.pro

import android.app.Application
import com.google.android.gms.ads.MobileAds

class MusicPlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this)
    }
}
