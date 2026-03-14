
package com.prafullkumar.orbit.home.habits.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private data class FeatureSlide(
    val emoji: String,
    val title: String,
    val description: String
)

private val onboardingSlides = listOf(
    FeatureSlide("🔥", "Smart Streaks", "Track streaks + consistency rate. Missing one day won't nuke your motivation. Break the perfection addiction."),
    FeatureSlide("🎯", "Flexible Habit Types", "Daily, X/week, specific days, quantity-based (read 20 pages), or timer-based (10 min). One size doesn't fit all."),
    FeatureSlide("⚡", "Difficulty Scoring", "Set effort level: Easy / Medium / Hard. Progress is weighted — 5 pushups ≠ 5 km run."),
    FeatureSlide("📊", "Emotional Data Viz", "GitHub heatmaps, weekly trends, current vs longest streak. Make your patterns visible."),
    FeatureSlide("🔬", "Failure Analysis", "When you miss, log why: busy, low energy, forgot. Over time, surface your own patterns."),
    FeatureSlide("🔗", "Habit Stacking", "\"After brushing → 5 pushups.\" Behavior piggybacking backed by behavioral science."),
    FeatureSlide("✨", "Identity Prompts", "Instead of \"Completed workout,\" see \"You showed up like an athlete.\" Identity > action."),
    FeatureSlide("📈", "Momentum Score", "A 7-day rolling score. Even if a streak breaks, momentum stays alive. No more \"I missed once, screw it.\""),
    FeatureSlide("🏠", "Environment Tagging", "Track where you do each habit: Home / Gym / Library. See which environments make you succeed."),
    FeatureSlide("🚀", "Compounding View", "\"At this rate: 182 workouts/year.\" Humans underestimate compound effort. We make it visible.")
)

@Composable
fun OnboardingHabitScreen(onGetStarted: () -> Unit) {
    val accentColor = Color(0xFF7C6FE0)
    val pagerState = rememberPagerState { onboardingSlides.size }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF090909))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                "A Habit Tracker That\nActually Works",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Not a glorified checkbox app.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 0.dp),
                pageSpacing = 0.dp
            ) { page ->
                val slide = onboardingSlides[page]
                Column(
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF111111))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(slide.emoji, fontSize = 56.sp)
                    Spacer(Modifier.height(20.dp))
                    Text(
                        slide.title,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        slide.description,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF888888),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "${page + 1} / ${onboardingSlides.size}",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = Color(0xFF444444)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Pager dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingSlides.size) { idx ->
                    Box(
                        Modifier
                            .size(if (idx == pagerState.currentPage) 20.dp else 6.dp, 6.dp)
                            .clip(if (idx == pagerState.currentPage) RoundedCornerShape(3.dp) else CircleShape)
                            .background(if (idx == pagerState.currentPage) accentColor else Color(0xFF333333))
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    "Start Building Habits",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
