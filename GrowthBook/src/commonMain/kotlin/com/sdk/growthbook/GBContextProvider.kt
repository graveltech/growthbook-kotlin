package com.sdk.growthbook

import com.sdk.growthbook.model.GBContext
import java.util.concurrent.ConcurrentHashMap

object GBContextProvider {

    private val gbContextMap: MutableMap<String, GBContext> = ConcurrentHashMap()

    fun putGbContext(key: String, gbContext: GBContext) {
        gbContextMap[key] = gbContext
    }

    fun getGbContext(key: String): GBContext? {
        return gbContextMap[key]
    }
}