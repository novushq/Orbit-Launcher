package com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Height of each letter slot in the sidebar. */
private val LetterSlotHeight = 16.dp

/**
 * Right-edge alphabet fast-scroll sidebar.
 *
 * @param letters    Ordered list of letters to display (e.g. 'A'..'Z' subset).
 * @param selectedLetter The currently active letter (highlighted), or null when no touch is active.
 * @param onLetterSelected Called with the letter under the pointer (or null on release).
 */
@Composable
fun AlphabetSidebar(
    letters: List<Char>,
    selectedLetter: Char?,
    modifier: Modifier = Modifier,
    onLetterSelected: (Char?) -> Unit
) {
    if (letters.isEmpty()) return

    val density = LocalDensity.current
    val slotHeightPx = with(density) { LetterSlotHeight.toPx() }

    Box(
        modifier = modifier
            .width(24.dp)
            .pointerInput(letters) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.firstOrNull() ?: continue
                        if (change.pressed) {
                            val idx = (change.position.y / slotHeightPx)
                                .toInt()
                                .coerceIn(0, letters.lastIndex)
                            onLetterSelected(letters[idx])
                            change.consume()
                        } else if (change.previousPressed) {
                            // Finger lifted — clear highlight
                            onLetterSelected(null)
                            change.consume()
                        }
                    }
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            letters.forEach { letter ->
                val isSelected = letter == selectedLetter
                Box(
                    modifier = Modifier
                        .size(LetterSlotHeight)
                        .then(
                            if (isSelected) Modifier.background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter.toString(),
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
