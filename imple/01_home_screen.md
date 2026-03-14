# Screen 01 — Home Screen

## Overview
The primary launcher screen. Minimal, fast, glanceable. No chrome. Everything is touch-only — no titles, no section labels visible by default. Feels like the phone's skin, not an app running on top of it.

## Layout (top → bottom, full-screen)

```
┌─────────────────────────────────────────┐
│                                         │
│                                         │
│         [STATUS BAR — transparent]      │
│                                         │
│   ╔═══════════════════════════════╗     │
│   ║   10:42           PM          ║     │  ← Clock: large weight-thin digits, 72sp
│   ║   Tuesday, March 14           ║     │  ← Date: 16sp, muted, tap → calendar
│   ╚═══════════════════════════════╝     │
│                                         │
│   ┌─────────────────────────────────┐   │
│   │  ████ 3h 12m today             │   │  ← Screen time pill: tap → usage screen
│   │  [progress bar, fills left→right] │   │    filled portion = time used vs daily goal
│   └─────────────────────────────────┘   │
│                                         │
│   ┌─────────────────────────────────┐   │
│   │  Habits  ● ● ● ○ ○  [+]        │   │  ← Habit dots: filled = done today
│   └─────────────────────────────────┘   │    tap any dot → mark complete, long-press → manage
│                                         │
│   ┌─────────────────────────────────┐   │
│   │  ☐  Review DSA problem          │   │  ← Todo: top 3 uncompleted, tap → toggle done
│   │  ☐  Push orbit feature          │   │    swipe right → complete, swipe left → delete
│   │  ☐  Read 10 pages               │   │    tap "..." → full todo screen
│   └─────────────────────────────────┘   │
│                                         │
│   ┌─────────────────────────────────┐   │
│   │  Chrome  WhatsApp  Notion  ...  │   │  ← Favorites: up to 7, icon-only 48dp
│   └─────────────────────────────────┘   │    long-press → remove, drag to reorder
│                                         │
│                [SPACER — fills]         │
│                                         │
│   ┌──────────────────────────────────┐  │
│   │  [☎]  [✉]  [📷]     [⋮⋮⋮]       │  │  ← Bottom bar: Phone·Messages·Camera·Drawer
│   └──────────────────────────────────┘  │    icon buttons, 48dp tap targets, no labels
│                                         │
└─────────────────────────────────────────┘
```

## Component Details

### Clock
- Font: system default, weight 100–200 (thin), size 72sp
- Color: `onSurface` full opacity
- Time and AM/PM on same row, AM/PM at 20sp weight-300
- Tap → opens clock/alarm app
- Long-press → nothing (or no-op)
- Updates every second via `LaunchedEffect` + `delay(1000)`

### Screen Time Ring / Bar
- Shows time used today vs configurable daily goal (default 4h)
- Visual: thin horizontal bar, 4dp height, rounded ends
- Fill color: green < 50%, amber 50–80%, red > 80%
- Text: "Xh Xm today" left-aligned, 14sp
- Tap → navigate to Usage Screen
- Data: pulled from `UsageDetails.getCurrentDayPhoneUsageData()`

### Habits Row
- Shows up to 6 habit dots (●/○) in a horizontal row
- ● = completed today, ○ = not yet
- Each dot is 12dp diameter, 8dp gap between
- Tap any dot → toggle that habit for today (haptic feedback)
- Long-press row → navigate to Habits Manager screen
- Emoji before the label optional (set when creating habit)
- "+ " button at right edge → quick add habit sheet

### Quick Todos
- Shows top 3 highest-priority incomplete todos
- Each row: `[ ] Task label` — 16sp, full width, generous vertical padding (14dp)
- Tap checkbox → mark complete with strikethrough animation then fade out
- Swipe right → complete, swipe left → delete (with undo snackbar)
- "···" / "see all" link at bottom → full Todo Screen
- Empty state: `"All clear today"` centered in gentle muted text

### Favorites Row
- Up to 7 app icons, 48dp, no labels
- Single horizontal row, centered
- Long-press any icon → context menu (Remove, App Info, Uninstall)
- Drag to reorder (with haptic)
- Empty slot shown as dashed circle with "+" for quick add

### Bottom Bar
- Four icon-only circular buttons, 48dp each
- Left cluster: Phone, Messages, Camera
- Right: Drawer trigger (≡ lines icon)
- Background: `surfaceContainer.copy(alpha=0.8f)` frosted effect
- No visible bottom bar background band — icons float above content

## Navigation / Gestures
- **Swipe left** on any part of the home → go to Page 1 (Dashboard pager)
- **Swipe up** anywhere on home (except bottom bar) → open App Drawer as bottom sheet
- **Swipe down** → opens notification shade (system)
- **Back button** → swallowed (stay on home, this is the launcher)

## State Handling
- All data loaded via `HomeViewModel` using `StateFlow`
- `collectAsStateWithLifecycle()` on all flows
- Skeleton shimmer loading placeholders while data loads first time
- Screen time refreshes every 2 minutes via `flow { while(true) { emit(...); delay(120000) } }`

## Animations
- Clock seconds hand tick: instant (no animation needed)
- Habit dot toggle: scale 1.0→1.3→1.0 + color fill, 200ms spring
- Todo completion: checkbox fill 150ms, text strikethrough 200ms, row shrink+fade 300ms
- Drawer open: spring curve from bottom, 350ms, overshoot 0.1
- All transitions: `spring(dampingRatio=0.8f, stiffnessRatio=380f)` — feels snappy not elastic

## Colors / Typography
- Background: `MaterialTheme.colorScheme.surface` (no wallpaper support v1)
- All text: `onSurface` / `onSurfaceVariant`
- Accent: `primary` only for active states (habit dot, progress fill, checkbox)
- No cards, no elevated surfaces on home — completely flat

---
