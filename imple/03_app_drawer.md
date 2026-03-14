# Screen 03 — App Drawer (Pager Page 2 OR Bottom Sheet)

## Overview
Replaces current bottom-sheet-only drawer. In v2, App Drawer IS page 2 of the pager — swipe left from Dashboard. Also still accessible via bottom-bar button and swipe-up on home (opens as sheet for quick access). When opened as bottom sheet it shows identically but dismisses on down-swipe or back.

## Layout

```
┌──────────────────────────────────────────┐
│  ────────  [drag handle, centered]       │  ← Only shown when opened as bottom sheet
│                                          │
│  ┌────────────────────────────────────┐  │
│  │  🔍  Search apps...       [⚙]     │  │  ← Search bar full width, settings gear right
│  └────────────────────────────────────┘  │
│                                          │
│  [A]                                     │  ← Alphabetical section header, 12sp muted
│  Chrome                                  │  ← App row (LIST mode) — 56dp, tap to launch
│  Calculator                              │
│                                          │
│  [B]                                     │
│  BBC News                                │
│  ...                                     │
│                                          │
│  [Alphabet fast-scroll sidebar]          │  ← Right edge: A–Z letters, 10sp, 24dp wide
│                                          │    drag finger to jump to section
└──────────────────────────────────────────┘
```

### LIST mode (default)
```
[A]
  Chrome                              ←  56dp row, app name only 16sp
  Calculator                          
  Camera                              
```

### LIST WITH ICONS mode
```
[A]
  [🌐] Chrome                         ←  48dp icon + name, 64dp row height
  [🧮] Calculator                     
```

### GRID mode (no labels)
```
 [🌐] [🧮] [📷] [📱] [⚙️]            ←  5 per row, 80dp cells, icon only 56dp
 [🎵] [🗺️] [📰] [📲] [🔒]            
```

### GRID WITH LABELS mode
```
 ┌────┐ ┌────┐ ┌────┐ ┌────┐          ←  4 per row, 90dp cells
 │ 🌐 │ │ 🧮 │ │ 📷 │ │ 📱 │          
 │Chrm│ │Calc│ │Cam │ │Cont│          ←  label 10sp, 2 lines max, truncated
 └────┘ └────┘ └────┘ └────┘          
```

## Long-Press Context Menu (all modes)
```
  ┌───────────────────────┐
  │  App name             │  ← App name as menu title
  ├───────────────────────┤
  │  🚀 Open              │
  │  ⭐ Add to favorites  │
  │  🙈 Hide app          │
  │  ℹ️ App info           │
  │  🗑 Uninstall          │
  │  ⏱ Set time limit     │  ← NEW: links to blocker for this app
  └───────────────────────┘
```

## Alphabet Fast-Scroll Sidebar
- Vertical list of letters A–Z on right edge
- 24dp wide strip
- Each letter: 12sp, muted, 16dp height
- Drag → jumps list to that section instantly
- Currently-active letter shows highlighted (primary color, 16dp circle bg)
- Implementation: `LazyListState.scrollToItem()` + letter → index map

## Search Behavior
- Filters in real-time as user types (no debounce needed, local data)
- Hides section headers when searching
- Shows "No apps matching 'xyz'" empty state
- Clears with X button or back press
- Search field auto-focuses when drawer opens via search gesture

## Drawer Settings Screen (accessed via ⚙ icon)
```
  ← App Drawer Settings

  Layout
  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐
  │ Lst │ │LstI │ │Grid │ │GrdL │  ← Tappable cards, selected = primary border
  └─────┘ └─────┘ └─────┘ └─────┘

  Grid columns (only shown in grid modes)
  [3]  [4]  [5]  [6]

  Sort order
  [Alphabetical ▾]     ←  Dropdown: A–Z, Z–A, Most used, Install date, Custom

  App icons
  [Show icons toggle]

  [Hidden Apps →]      ←  Navigate to hidden apps screen
```

## Animations
- Drawer open (from swipe-up): spring from y=screenHeight to y=0, 300ms
- Item launch: pressed state scales to 0.92, releases to 1.0
- Keyboard dismiss on scroll (drag gesture clears focus)
- Layout type switch: crossfade 200ms

---
