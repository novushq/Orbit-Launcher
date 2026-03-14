package com.prafullkumar.orbit.home.habits.model

enum class HabitType(val displayName: String, val description: String) {
    DAILY("Daily", "Every day"),
    X_PER_WEEK("X per week", "A few times per week"),
    SPECIFIC_DAYS("Specific days", "On chosen days"),
    QUANTITY("Quantity", "Track a measurable amount"),
    TIMER("Timer", "Track duration")
}

enum class HabitDifficulty(val displayName: String, val weight: Float, val emoji: String) {
    EASY("Easy", 1f, "🌱"),
    MEDIUM("Medium", 2f, "🔥"),
    HARD("Hard", 3f, "⚡")
}

enum class EnvironmentTag(val displayName: String, val emoji: String) {
    HOME("Home", "🏠"),
    GYM("Gym", "🏋️"),
    OFFICE("Office", "💼"),
    OUTDOOR("Outdoor", "🌿"),
    LIBRARY("Library", "📚"),
    ANY("Anywhere", "🌍")
}

enum class FailureReason(val displayName: String, val emoji: String) {
    TOO_BUSY("Too busy", "⏰"),
    LOW_ENERGY("Low energy", "😴"),
    FORGOT("Forgot", "🤔"),
    TRAVEL("Traveling", "✈️"),
    SICK("Sick / Unwell", "🤒"),
    MOTIVATION("Lost motivation", "💭"),
    OTHER("Other", "🗒️")
}

enum class EnergyLevel(val displayName: String, val value: Int) {
    VERY_LOW("Very low", 1),
    LOW("Low", 2),
    MEDIUM("Medium", 3),
    HIGH("High", 4),
    VERY_HIGH("Very high", 5)
}
