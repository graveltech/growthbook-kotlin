package com.sdk.growthbook

import com.sdk.growthbook.network.NetworkDispatcher
import com.sdk.growthbook.utils.Crypto
import com.sdk.growthbook.utils.GBCacheRefreshHandler
import com.sdk.growthbook.utils.GBError
import com.sdk.growthbook.utils.GBFeatures
import com.sdk.growthbook.utils.GBRemoteEvalParams
import com.sdk.growthbook.utils.GBUtils.Companion.refreshStickyBuckets
import com.sdk.growthbook.utils.Resource
import com.sdk.growthbook.utils.getFeaturesFromEncryptedFeatures
import com.sdk.growthbook.evaluators.GBExperimentEvaluator
import com.sdk.growthbook.evaluators.GBFeatureEvaluator
import com.sdk.growthbook.features.FeaturesDataModel
import com.sdk.growthbook.features.FeaturesDataSource
import com.sdk.growthbook.features.FeaturesFlowDelegate
import com.sdk.growthbook.features.FeaturesViewModel
import com.sdk.growthbook.model.GBContext
import com.sdk.growthbook.model.GBExperiment
import com.sdk.growthbook.model.GBExperimentResult
import com.sdk.growthbook.model.GBFeatureResult
import kotlinx.coroutines.flow.Flow

typealias GBTrackingCallback = (GBExperiment, GBExperimentResult) -> Unit
typealias GBFeatureUsageCallback = (featureKey: String, gbFeatureResult: GBFeatureResult) -> Unit

/**
 * The main export of the libraries is a simple GrowthBook wrapper class
 * that takes a Context object in the constructor.
 * It exposes two main methods: feature and run.
 */


class GrowthBookSDK(private val key: String) : FeaturesFlowDelegate {

    private var refreshHandler: GBCacheRefreshHandler? = null
    private lateinit var networkDispatcher: NetworkDispatcher
    private lateinit var featuresViewModel: FeaturesViewModel
    private var attributeOverrides: Map<String, Any> = emptyMap()
    private var forcedFeatures: Map<String, Any> = emptyMap()

    internal val gbContext by lazy {
        GbContextProvider.getGbContext(key) ?: throw IllegalArgumentException("GbContext not initialize")
    }

    internal constructor(
        key: String,
        context: GBContext,
        refreshHandler: GBCacheRefreshHandler?,
        networkDispatcher: NetworkDispatcher,
        features: GBFeatures? = null
    ) : this(key) {
        GbContextProvider.putGbContext(key, context)
        this.refreshHandler = refreshHandler
        this.networkDispatcher = networkDispatcher

        /**
         * JAVA Consumers preset Features
         * SDK will not call API to fetch Features List
         */
        this.featuresViewModel =
            FeaturesViewModel(
                key = key,
                delegate = this,
                dataSource = FeaturesDataSource(
                    dispatcher = networkDispatcher,
                    gbContext = gbContext
                ),
                encryptionKey = gbContext.encryptionKey,
            )
        if (features != null) {
            gbContext.features = features
        } else {
            refreshCache()
        }
        this.attributeOverrides = gbContext.attributes
        refreshStickyBucketService()
    }

    /**
     * Manually Refresh Cache
     */
    fun refreshCache() {
        if (gbContext.remoteEval) {
            refreshForRemoteEval()
        } else {
            featuresViewModel.fetchFeatures()
        }
    }

    /**
     * Get Context - Holding the complete data regarding cached features & attributes etc.
     */
    fun getGBContext(): GBContext {
        return gbContext
    }

    /**
     * receive Features automatically when updated SSE
     */
    fun autoRefreshFeatures(): Flow<Resource<GBFeatures?>> {
        return featuresViewModel.autoRefreshFeatures()
    }

    /**
     * Get Cached Features
     */
    fun getFeatures(): GBFeatures {
        return gbContext.features
    }

    /**
     * Delegate that set to Context successfully fetched features
     */
    override fun featuresFetchedSuccessfully(features: GBFeatures, isRemote: Boolean) {
        gbContext.features = features
        if (isRemote) {
            this.refreshHandler?.invoke(true, null)
        }
    }

    /**
     * The setEncryptedFeatures method takes an encrypted string with an encryption key
     * and then decrypts it with the default method of decrypting
     * or with a method of decrypting from the user
     */
    fun setEncryptedFeatures(
        encryptedString: String,
        encryptionKey: String,
        subtleCrypto: Crypto?
    ) {
        val feature = getFeaturesFromEncryptedFeatures(
            encryptedString = encryptedString,
            encryptionKey = encryptionKey,
            subtleCrypto = subtleCrypto
        )
        gbContext.features =
            feature ?: return
    }

    /**
     * Delegate which inform that fetching features failed
     */
    override fun featuresFetchFailed(error: GBError, isRemote: Boolean) {

        if (isRemote) {
            this.refreshHandler?.invoke(false, error)
        }
    }

    /**
     * The feature method takes a single string argument,
     * which is the unique identifier for the feature and returns a FeatureResult object.
     */
    fun feature(id: String): GBFeatureResult {
        return GBFeatureEvaluator().evaluateFeature(
            context = gbContext,
            featureKey = id,
            attributeOverrides = attributeOverrides
        )
    }

    /**
     * The isOn method takes a single string argument,
     * which is the unique identifier for the feature and returns the feature state on/off
     */
    fun isOn(featureId: String): Boolean {
        return feature(id = featureId).on
    }

    /**
     * The run method takes an Experiment object and returns an ExperimentResult
     */
    fun run(experiment: GBExperiment): GBExperimentResult {
        return GBExperimentEvaluator().evaluateExperiment(
            context = gbContext,
            experiment = experiment,
            attributeOverrides = attributeOverrides
        )
    }

    /**
     * The setForcedFeatures method setup the Map of user's (forced) features
     */
    fun setForcedFeatures(forcedFeatures: Map<String, Any>) {
        this.forcedFeatures = forcedFeatures
    }

    /**
     * The getForcedFeatures method for mapping model object for request's body type
     */
    fun getForcedFeatures(): List<List<Any>> {
        return this.forcedFeatures.map { listOf(it.key, it.value) }
    }

    /**
     * The setAttributes method replaces the Map of user attributes
     * that are used to assign variations
     */
    fun setAttributes(attributes: Map<String, Any>) {
        gbContext.attributes = attributes
        refreshStickyBucketService()
    }

    /**
     * The setAttributeOverrides method replaces the Map of user overrides attribute
     * that are used for Sticky Bucketing
     */
    fun setAttributeOverrides(overrides: Map<String, Any>) {
        attributeOverrides = overrides
        if (gbContext.stickyBucketService != null) {
            refreshStickyBucketService()
        }
        refreshForRemoteEval()
    }

    fun getAttributeOverrides(): Map<String, Any> {
        return attributeOverrides
    }

    /**
     * The setForcedVariations method setup the Map of user's (forced) variations
     * to assign a specific variation (used for QA)
     */
    fun setForcedVariations(forcedVariations: Map<String, Any>) {
        gbContext.forcedVariations = forcedVariations
        refreshForRemoteEval()
    }

    /**
     * Delegate that call refresh Sticky Bucket Service
     * after success fetched features
     */
    override fun featuresAPIModelSuccessfully(model: FeaturesDataModel) {
        refreshStickyBucketService(dataModel = model)
    }

    /**
     * Method for update latest attributes
     */
    private fun refreshStickyBucketService(dataModel: FeaturesDataModel? = null) {
        if (gbContext.stickyBucketService != null) {
            refreshStickyBuckets(
                context = gbContext,
                data = dataModel,
                attributeOverrides = attributeOverrides
            )
        }
    }

    /**
     * Method for sending request evaluate features remotely
     */
    private fun refreshForRemoteEval() {
        if (!gbContext.remoteEval) {
            return
        }
        val payload = GBRemoteEvalParams(
            gbContext.attributes,
            this.getForcedFeatures(),
            gbContext.forcedVariations
        )
        featuresViewModel.fetchFeatures(gbContext.remoteEval, payload)
    }
}
