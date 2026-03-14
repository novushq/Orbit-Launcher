# Screen 02 — Dashboard Page (Pager Page 1)

## Overview
Swipe left from home. The "productivity command center." Dense but structured. Vertically scrollable. Contains the full habit grid, todo list, usage graph, app blocker toggle, and a configurable widgets zone. This is the screen that makes Orbit more than a launcher.

## Layout

```
┌──────────────────────────────────────────┐
│                                          │
│  [STATUS BAR — transparent]              │
│                                          │
│  ▒▒▒▒▒▒▒▒▒▒▒  SCREEN TIME  ▒▒▒▒▒▒▒▒▒▒  │  ← Section: 12sp label, muted
│  ┌────────────────────────────────────┐  │
│  │  3h 12m  ████████░░░░  goal: 5h   │  │  ← Progress bar + fraction
│  │  ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐   │  │  ← Top 3 apps, icon + time chip
│  │  │YT│ │WA│ │IG│                   │  │
│  │  2h  1h  20m                      │  │
│  └────────────────────────────────────┘  │
│  [See full usage →]                      │  ← tap → Usage Screen
│                                          │
│  ▒▒▒▒▒▒▒▒▒▒▒  HABITS  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒  │
│  ┌────────────────────────────────────┐  │
│  │  🏃 Run      Mon ● Tue ● Wed ○     │  │  ← Each habit row:
│  │  📚 Read     Mon ● Tue ○ Wed ○     │  │    emoji + name + last 7 day dots
│  │  💧 Water    Mon ● Tue ● Wed ●     │  │    Tap row → toggle today
│  │  🧘 Meditate Mon ○ Tue ● Wed ●     │  │    current streak "🔥 5" at right
│  │                      [+ Add habit] │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ▒▒▒▒▒▒▒▒▒▒▒  TODOS  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒  │
│  ┌────────────────────────────────────┐  │
│  │  [!] Review DSA                    │  │  ← Priority indicator [!]=high [·]=med
│  │  [·] Push orbit feature            │  │    Tap checkbox to complete
│  │  [·] Read 10 pages                 │  │    Swipe right → done, swipe left → delete
│  │  ─────────────────────             │  │    Done items collapsed under "3 done ▾"
│  │  [+ Quick add...]                  │  │  ← Input that appears inline when tapped
│  │  [See all todos →]                 │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ▒▒▒▒▒▒▒▒▒▒▒  APP BLOCKER  ▒▒▒▒▒▒▒▒▒  │
│  ┌────────────────────────────────────┐  │
│  │  Focus mode  ●────────○  [OFF]     │  │  ← Master toggle
│  │  Instagram    [Blocked ✓]          │  │  ← Per-app rows, showing blocked state
│  │  YouTube      [Blocked ✓]          │  │    Tap row → open App Blocker screen
│  │  Shorts/Reels [Blocked ✓]          │  │  ← Special Shorts/Reels blocker entry
│  │  [Manage blocklist →]              │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ▒▒▒▒▒▒▒▒▒▒▒  WIDGETS  ▒▒▒▒▒▒▒▒▒▒▒▒▒  │
│  ┌────────────────────────────────────┐  │
│  │  ┌──────────────┐  ┌───────────┐  │  │  ← 2-column grid of widgets
│  │  │  25°C ☁       │  │  3:15 PM  │  │  │    each widget is a card
│  │  │  Partly cloudy│  │  Work ⏳  │  │  │
│  │  └──────────────┘  └───────────┘  │  │
│  │  ┌────────────────────────────┐   │  │
│  │  │  📝 Quick note...          │   │  │  ← Full-width note widget
│  │  └────────────────────────────┘   │  │
│  │         [+ Add widget]            │  │  ← Tap → Widget Picker screen
│  └────────────────────────────────────┘  │
│                                          │
└──────────────────────────────────────────┘
```

## Component Details

### Screen Time Section
- Small donut or bar showing today's total vs goal
- Top 3 time-consuming apps shown as chips: `[AppIcon] Xh Xm`
- Chip background: `surfaceContainerHigh`
- Tap section header → Usage Screen (full)
- Data source: `UsageDetails.getCurrentDayPhoneUsageData()`

### Habits Grid
- Each row: 48dp height, horizontal `Row`
  - Left: emoji (20dp) + habit name (16sp, weight 400)
  - Middle: 7 day dots (Mon–Sun), 10dp each, 6dp gap
    - Filled circle (`primary`) = done
    - Outlined circle = not done / future
    - Today's dot has a subtle ring/border to highlight it
  - Right: 🔥 streak count (12sp, amber)
- Tap row → toggle today's completion for that habit
- Long-press row → context menu (Edit, Delete, View History)
- "+" button → bottom sheet quick-add habit (name + emoji picker)
- Max 6 habits shown; "+ N more" link if more exist

### Todos Section
- Full todo list, grouped: Active | Done
- Done section collapsed by default, expand with "X done ▾"
- Each todo row:
  - Checkbox (24dp) + task text (16sp) + optional due date chip
  - Priority dot: red=high, amber=medium, none=low
  - Swipe right: complete (green flash, row exits to right)
  - Swipe left: delete (red, row exits to left, undo snackbar 3s)
- Quick add: text field at bottom of list, shows keyboard, `Enter` to add
- Tap "See all →" → Todo Manager screen

### App Blocker Section
- Master toggle switch "Focus mode"
- When ON: shows which apps are currently blocked
- Blocked app row: icon + name + "Blocked" chip (red tint)
- Shorts/Reels row: special entry covering YouTube Shorts, Instagram Reels, TikTok
- Tap any row → App Blocker screen (full management)
- If Focus mode is OFF: show "Off — tap to activate" in muted text

### Widgets Zone
- Configurable 2-column grid
- Each widget is a card: `surfaceContainerLow`, 12dp radius, 12dp padding
- Widget types (v1):
  - **Clock widget**: large time + active alarm
  - **Weather widget**: temp + condition (requires location permission)
  - **Quick note**: single editable text area, auto-saves
  - **Countdown**: days until user-set event
  - **Battery**: current % + charging status
- "+" add widget: opens Widget Picker bottom sheet
- Long-press any widget → drag-to-reorder or "Remove" context

## Navigation
- Swipe right → back to Home (Page 0)
- Swipe left → App Drawer (Page 2)
- Section headers: tap → navigate to respective full screen

## Scroll Behavior
- `LazyColumn` wrapping all sections
- `stickyHeader` optional for section labels
- Each section is a `LazyListScope` extension function

---
