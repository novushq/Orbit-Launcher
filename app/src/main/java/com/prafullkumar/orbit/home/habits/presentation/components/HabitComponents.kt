
package com.prafullkumar.orbit.home.habits.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Momentum Score Card ──────────────────────────────────────────────────────

@Composable
fun MomentumScoreCard(score: Int, modifier: Modifier = Modifier) {
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationStarted = true }
    val animatedFraction by animateFloatAsState(
        targetValue = if (animationStarted) score / 100f else 0f,
        animationSpec = tween(1200),
        label = "momentum_anim"
    )
    val accentColor = Color(0xFF7C6FE0)
    val trackColor = Color(0xFF2A2A2A)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF111111))
            .padding(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Momentum Score",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF888888)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${score}",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "7-day rolling score",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF555555)
                )
            }
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(80.dp)) {
                    drawArc(
                        color = trackColor,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = accentColor,
                        startAngle = 135f,
                        sweepAngle = 270f * animatedFraction,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    "${(score * animatedFraction).toInt()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

// ── Streak Badge ────────────────────────────────────────────────────────────

@Composable
fun StreakBadge(current: Int, longest: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StreakPill(label = "Current", value = current, icon = "🔥")
        Box(
            Modifier
                .width(1.dp)
                .height(24.dp)
                .background(Color(0xFF333333))
        )
        StreakPill(label = "Longest", value = longest, icon = "🏆")
    }
}

@Composable
private fun StreakPill(label: String, value: Int, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$icon $value days",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF888888))
    }
}

// ── Heatmap Component ────────────────────────────────────────────────────────

@Composable
fun HeatmapComponent(data: Map<Long, Boolean>, modifier: Modifier = Modifier) {
    val activeColor = Color(0xFF7C6FE0)
    val inactiveColor = Color(0xFF1E1E1E)
    // Show last 18 weeks (126 days) in a grid
    val days = data.entries.toList().takeLast(126)
    val weeks = days.chunked(7)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "Year Heatmap",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF888888)
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            weeks.forEach { week ->
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    week.forEach { (_, completed) ->
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (completed) activeColor else inactiveColor)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(inactiveColor)
            )
            Spacer(Modifier.width(4.dp))
            Text("None", style = MaterialTheme.typography.labelSmall, color = Color(0xFF555555))
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(activeColor)
            )
            Spacer(Modifier.width(4.dp))
            Text("Done", style = MaterialTheme.typography.labelSmall, color = Color(0xFF555555))
        }
    }
}

// ── Weekly Bar Chart ─────────────────────────────────────────────────────────

@Composable
fun WeeklyTrendChart(weeklyData: List<com.prafullkumar.orbit.home.habits.model.DayData>, modifier: Modifier = Modifier) {
    val accentColor = Color(0xFF7C6FE0)
    val dimColor = Color(0xFF1E1E1E)
    val days = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Last 7 Days", style = MaterialTheme.typography.labelMedium, color = Color(0xFF888888))
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyData.forEachIndexed { idx, dayData ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        Modifier
                            .width(28.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (dayData.completed) accentColor else dimColor)
                    )
                    Text(
                        days.getOrElse(idx) { "" },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF555555)
                    )
                }
            }
        }
    }
}

// ── Compounding Projection ───────────────────────────────────────────────────

@Composable
fun CompoundingProjection(habitName: String, projection: Int, unit: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111111))
            .padding(16.dp)
    ) {
        Column {
            Text("📈 Compounding Effect", style = MaterialTheme.typography.labelMedium, color = Color(0xFF888888))
            Spacer(Modifier.height(8.dp))
            Text(
                "At this rate: $projection ${unit.ifEmpty { "completions" }} / year",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "You're building something lasting.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF555555)
            )
        }
    }
}

// ── Habit Stacking Suggestions ────────────────────────────────────────────────

@Composable
fun HabitStackingSuggestion(suggestions: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    if (suggestions.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        Text("🔗 Habit Stacking", style = MaterialTheme.typography.labelMedium, color = Color(0xFF888888))
        Spacer(Modifier.height(8.dp))
        suggestions.forEach { (after, doThis) ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF111111))
                    .padding(12.dp)
            ) {
                Text(
                    "After $after → $doThis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCCCCCC)
                )
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

// ── Identity Prompt ─────────────────────────────────────────────────────────

@Composable
fun IdentityPromptCard(prompt: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F0F20))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C6FE0).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("✨", fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                prompt,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFD0CCFF),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
