package com.gliss.motionui.growth

/**
 * Configuration for Growth Funnels and A/B Testing.
 * Allows remote control of feature rollout and pricing experiments.
 */
object FunnelConfig {
    
    // Growth Funnel Stages
    enum class FunnelStage {
        AWARENESS,    // Splash / Store
        ONBOARDING,   // Gesture Tutorial
        ACTIVATION,   // Home / Lab Interaction
        RETENTION,    // AI Personal Assistant
        REVENUE       // Premium Upgrade
    }

    // A/B Testing Variants
    data class AbTestConfig(
        val onboardingVariant: String = "gesture_first", // "gesture_first" vs "video_first"
        val premiumCtaText: String = "Unlock AI Potential",
        val showTrialOffer: Boolean = true
    )

    // Current Active Config (In production, this would come from Firebase Remote Config)
    val activeConfig = AbTestConfig()
}
