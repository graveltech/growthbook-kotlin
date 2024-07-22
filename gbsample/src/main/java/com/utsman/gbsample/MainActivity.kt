package com.utsman.gbsample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sdk.growthbook.GBSDKBuilder
import com.sdk.growthbook.GrowthBookSDK
import com.sdk.growthbook.network.GBNetworkDispatcherKtor
import com.sdk.growthbook.utils.GBStickyAssignmentsDocument

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gbSalam = GBSDKBuilder(
            apiKey = "sdk-nlzOsfVlR3SJY0",
            hostURL = "https://cdn.growthbook.io/",
            attributes = hashMapOf(
                FeatureFlagAttrs.APP_VERSION to 0.1,
                FeatureFlagAttrs.PLATFORM to "Android"
            ),
            networkDispatcher = GBNetworkDispatcherKtor(),
            trackingCallback = { _, _ -> }
        )
            .initialize()

        val gbDulur = GBSDKBuilder(
            apiKey = "sdk-jyRYOYpukH6zWGH",
            hostURL = "https://cdn.growthbook.io/",
            attributes = hashMapOf(
                FeatureFlagAttrs.APP_VERSION to 0.1,
                FeatureFlagAttrs.PLATFORM to "Android"
            ),
            networkDispatcher = GBNetworkDispatcherKtor(),
            trackingCallback = { _, _ -> }
        )
            .initialize()

        val gbOnwer = GBSDKBuilder(
            apiKey = "sdk-TlyhkBOAL8HIXLo8",
            hostURL = "https://cdn.growthbook.io/",
            attributes = hashMapOf(
                FeatureFlagAttrs.APP_VERSION to 0.1,
                FeatureFlagAttrs.PLATFORM to "Android"
            ),
            networkDispatcher = GBNetworkDispatcherKtor(),
            trackingCallback = { _, _ -> }
        )
            .initialize()

        val gbSalamKey = gbSalam.getGBContext().apiKey
        val gbDulurKey = gbDulur.getGBContext().apiKey

        println("asuuu dulur: ${gbDulur.getFeatures()}")
        println("asuuu salam: ${gbSalam.getFeatures()}")
        println("asuuu owner: ${gbOnwer.getFeatures()}")
    }
}

object FeatureFlagAttrs {
    const val UUID = "uuid"
    const val USER_ID = "user_id"
    const val APP_VERSION = "app_version"
    const val PLATFORM = "platform"
    const val IS_NEW_USER = "is_new_user"
}