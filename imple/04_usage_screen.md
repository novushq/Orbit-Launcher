# Screen 04 — Usage / Screen Time Screen

## Overview
Detailed screen time dashboard. Accessed from the usage pill on home screen or usage section on dashboard. Shows weekly usage graph, daily breakdown, and per-app stats. Replaces current broken "Wellbeing" tap-through.

## Layout

```
┌──────────────────────────────────────────┐
│  ← Screen Time                [today ▾] │  ← TopAppBar, date filter dropdown
│                                          │
│  ┌────────────────────────────────────┐  │
│  │          3h 12m today              │  │  ← Hero number, centered, 48sp bold
│  │   goal: 5h  ████████████░░░░░░░   │  │  ← Progress bar with goal marker
│  │   62% of goal used                 │  │  ← Percentage text, 14sp muted
│  └────────────────────────────────────┘  │
│                                          │
│  ← This Week    [3 Wks Ago ▸]           │  ← Week navigation arrows
│  ┌────────────────────────────────────┐  │
│  │                                    │  │
│  │    ▐█▌  ▐█▌  ▐█▌  ▐▌   ▐█▌  ▐▌   │  │  ← ColumnChart bars
│  │                                    │  │    bar tap → select that day
│  │   Sun Mon Tue Wed Thu Fri Sat      │  │  ← Labels below bars
│  └────────────────────────────────────┘  │
│                                          │
│  Wednesday  •  2h 48m                   │  ← Selected day header, 16sp
│  ─────────────────────────────────────  │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │  [YT]  YouTube       1h 24m  ████  │  │  ← App usage row
│  │  [WA]  WhatsApp       48m   ██░    │  │    icon 40dp + name + time + mini bar
│  │  [IG]  Instagram      30m   ██░    │  │    mini bar: relative to top app
│  │  [CH]  Chrome         15m   █░░    │  │    Tap row → App Detail screen
│  │  [TW]  Twitter         5m   ░░░    │  │
│  │                                    │  │
│  │  [+ 12 more apps ▾]               │  │  ← Collapsed by default, tap to expand
│  └────────────────────────────────────┘  │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │  Daily goal         [5h      ▸]   │  │  ← Settings section
│  │  Screen time widget [ON  ●──○ ]   │  │
│  │  Downtime schedule  [Set →]       │  │  ← Links to App Blocker
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

## Component Details

### Hero Total
- Large centered time display: `Xh Xm` — 48sp, weight 500
- Sub-text: goal progress
- Progress bar: 4dp height, rounded, `primary` fill color
- Goal marker: small vertical tick above the bar at goal position
- Color coding: green < 60%, amber 60–85%, red > 85%

### Weekly Bar Chart
- Uses `compose-charts` `ColumnChart`
- 7 bars (Sun–Sat)
- Currently selected day: `primary` color, others: `surfaceContainerHighest`
- Y-axis: hours (0 → max+1, capped at 24)
- Tap a bar → `viewModel.updateSelectedDate(dayIndex)`
- Swipe left/right on chart → previous/next week
- Week nav arrows: `< This Week >` style header
- Limited to 4 weeks back (existing logic)

### App Usage List
- Each row: 64dp height
  - App icon 40dp, `RoundedCornerShape(10.dp)`
  - App name 16sp weight-400
  - Duration text 14sp, `primary` color, right-aligned
  - Mini progress bar below name: 3dp height, relative fill
- Sorted: most time first
- Top 5 visible, rest collapsed behind "X more apps ▾"
- Tap any row → `AppDetailScreen`

### App Detail Screen (sub-screen)
```
  ← YouTube

  ┌────────────────────────────────────┐
  │  [YT icon 64dp]                    │
  │  YouTube          1h 24m today     │
  │  Opened: 8 times today             │
  └────────────────────────────────────┘

  Usage by hour today
  ┌────────────────────────────────────┐
  │  ▐▌ ▐█▌  ▐▌  ▐██▌  ▐▌  ▐▌       │  ← Hourly chart (ColumnChart)
  │  6  8   10   12   14  16  18      │
  └────────────────────────────────────┘

  This week
  ┌────────────────────────────────────┐
  │  Mon  Tue  Wed  Thu  Fri  Sat  Sun │
  │  45m  1h  1h24  0   30m  2h   0   │  ← Simple row of day chips
  └────────────────────────────────────┘

  [Set daily limit for YouTube →]      ←  Links to App Blocker with app pre-selected
  [Open app ↗]
```

## Data Sources
- `UsageDetails.getDailyStats()` for today total
- `UsageDetails.getWeekPhoneUsageData()` for weekly graph
- `AppUsageDetails.getAppOpenCountByHour()` for hourly chart in detail
- `AppUsageDetails.getAppOpenCountByDay()` for weekly per-app

## Navigation
- Back: `navController.popBackStack()`
- App row tap: `navigate(UsagesRoutes.AppDetail(packageName))`
- Daily goal → inline `AlertDialog` with number picker
- Downtime schedule → `Routes.AppBlocker`

---
