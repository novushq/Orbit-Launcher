# Screen 05 — Habits Manager Screen

## Overview
Full habit tracking screen. Shows all habits with their 30-day history grid (GitHub-style contribution grid per habit). Create, edit, delete habits. View streaks. Long-press cell to backfill a day.

## Layout

```
┌──────────────────────────────────────────┐
│  ← Habits              [+ New habit]     │  ← TopAppBar
│                                          │
│  Today: 3 / 5 done                       │  ← Summary pill: X of Y habits done today
│  ┌──────────────────────────────────┐    │    tappable, shows breakdown tooltip
│  │  ██████████░░░░░░░░░░░░  60%    │    │  ← Today's completion bar
│  └──────────────────────────────────┘    │
│                                          │
│  ╔══════════════════════════════════╗    │
│  ║  🏃  Run 5km           🔥 12     ║    │  ← Habit card
│  ║  ● ● ● ○ ● ● ● ○ ○ ○ ● ●       ║    │    emoji + name left, streak right
│  ║  S M T W T F S  (last 2 weeks)  ║    │    2-week dot grid below
│  ║  ○ ○ ● ● ● ○ ●  ○ ● ● ● ● ○ ○ ║    │    ● = done, ○ = missed, lighter = today
│  ╚══════════════════════════════════╝    │
│                                          │
│  ╔══════════════════════════════════╗    │
│  ║  📚  Read 20 pages      🔥 5     ║    │
│  ║  ○ ● ● ● ● ○ ●  ● ● ○ ○ ○ ● ● ║    │
│  ╚══════════════════════════════════╝    │
│                                          │
│  ╔══════════════════════════════════╗    │
│  ║  💧  Drink water 2L     🔥 21    ║    │
│  ║  ● ● ● ● ● ● ●  ● ● ● ● ● ● ● ║    │  ← All done — full green
│  ╚══════════════════════════════════╝    │
│                                          │
│  [Tap any dot to toggle • Long-press     │  ← Hint text, 12sp muted, dismissible
│   a dot to backfill older entries]       │
│                                          │
└──────────────────────────────────────────┘
```

## Add / Edit Habit Bottom Sheet

```
  ─── [drag handle]

  ┌──────────────────────────────────────┐
  │  [🏃] [Choose emoji]                 │  ← Emoji picker row (commonly used)
  │                                      │
  │  Habit name                          │
  │  ┌────────────────────────────────┐  │
  │  │  Run 5km                       │  │  ← TextField
  │  └────────────────────────────────┘  │
  │                                      │
  │  Target days                         │
  │  [Mon] [Tue] [Wed] [Thu] [Fri]       │  ← Day selector chips, toggle on/off
  │  [Sat] [Sun]                         │    default: every day
  │                                      │
  │  Reminder (optional)                 │
  │  [07:00 AM  ▸]                       │  ← TimePicker opens on tap
  │                                      │
  │  Color                               │
  │  ● ● ● ● ● ● ●                      │  ← 7 color options as circles
  │                                      │
  │  [Cancel]            [Save habit]    │
  └──────────────────────────────────────┘
```

## Habit Card Long-Press Context Menu
```
  ┌───────────────────────┐
  │  ✏️ Edit               │
  │  📊 View full history  │
  │  🗑 Delete             │
  │  ⏸ Pause habit        │  ← Pause = don't require completion, don't break streak
  └───────────────────────┘
```

## Habit History Full Screen (navigate from context)
```
  ← Run 5km history     🔥 12 day streak

  All time: 47 completions / 62 days = 76%

  ┌──────────────────────────────────────┐
  │  [GitHub-style contribution grid]    │  ← LazyVerticalGrid of dots
  │  7 columns (Mon–Sun), N weeks rows   │    color by completion
  │  Jan | Feb | Mar ...                 │    months labeled on left
  └──────────────────────────────────────┘

  Tap any cell → toggle for that day
```

## Data Model (Room entity needed)
```kotlin
@Entity
data class Habit(
    @PrimaryKey val id: String,           // UUID
    val name: String,
    val emoji: String,
    val colorIndex: Int,
    val targetDays: Set<Int>,            // Calendar.MONDAY etc
    val reminderTime: String?,            // "HH:mm" or null
    val createdAt: Long,
    val isActive: Boolean = true
)

@Entity
data class HabitEntry(
    @PrimaryKey val id: String,          // UUID
    val habitId: String,                  // FK
    val date: String,                     // "YYYY-MM-DD"
    val completed: Boolean
)
```

## Streak Logic
- Current streak: consecutive days (matching targetDays) with completions ending today
- If today is a target day and not yet done: streak shows yesterday's count (grace period)
- If today is NOT a target day: streak continues unbroken
- Longest streak: stored in DB or computed from entries
- Streak broken: missed a target day entirely

## Navigation
- "+" button → show Add Habit bottom sheet
- Long-press card → context menu
- "View full history" → HabitHistoryScreen
- Back → Dashboard or Home depending on entry point

---
