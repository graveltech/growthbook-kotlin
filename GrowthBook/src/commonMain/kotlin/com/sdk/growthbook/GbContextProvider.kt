package com.sdk.growthbook

import com.sdk.growthbook.model.GBContext

object GbContextProvider {

    private val gbContextMap: MutableMap<String, GBContext> = mutableMapOf()

    fun putGbContext(key: String, gbContext: GBContext) {
        gbContextMap[key] = gbContext
    }

    fun getGbContext(key: String): GBContext? {
        return gbContextMap[key]
    }
}