package com.example.altafedeltium.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.altafedeltium.ui.theme.AccentText
import com.example.altafedeltium.ui.theme.OrangeOnPrimaryContainer
import com.example.altafedeltium.ui.theme.MainOrange
import com.example.altafedeltium.ui.theme.MainOrangeDark

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .border(
                width = 2.dp,
                color = if (selected) MainOrangeDark else Color(0xFF555555),
                shape = CircleShape
            )
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Cerchio interno di arancione scuro quando selezionato
        if (selected) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(
                        color = MainOrangeDark,
                        shape = CircleShape
                    )
            )
        }
    }
}

