package com.wheatley.morph.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardSmall(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit = {}
) {
    Card(
        colors = CardColors(
            containerColor = color,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun CardAction(
    modifier: Modifier = Modifier,
    colorTop: Color,
    colorBottom: Color,
    icon: String,
    label: String,
    number: String = "",
    numberColor: Color,
    actionIcon: ImageVector,
    action: () -> Unit,
) {
    CardSmall(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
            ) {
                CardBadge(
                    colorTop = colorTop,
                    colorBottom = colorBottom,
                    icon = icon
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                )

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),

                ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = numberColor
                )
                IconButton(
                    onClick = { action() }
                ) {
                    Icon(imageVector = actionIcon, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    text: String,
    subText: String,
    icon: String,
) {
    CardBig(
        
    ) {
        CardBadge(
            modifier = TODO(),
            icon = TODO(),
            colorTop = TODO()
        ) {
            
        }
    }
}

@Composable
fun CardBig(
    modifier: Modifier = Modifier,
    colorTop: Color = MaterialTheme.colorScheme.surfaceContainer,
    colorBottom: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(colorTop, colorBottom)
                    )
                ),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }
}

@Composable
fun CardBadge(
    modifier: Modifier = Modifier,
    icon: String,
    colorTop: Color,
    colorBottom: Color
){
    Box(
        modifier = modifier
            .height(48.dp)
            .width(48.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorTop, colorBottom)
                )
            ),
        contentAlignment = Alignment.Center
        ){
            Text(
                fontSize = 24.sp,
                text = icon,
            )
    }
}