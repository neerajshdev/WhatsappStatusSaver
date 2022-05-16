package com.nibodev.composeapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nibodev.composeapp.R
import com.nibodev.composeapp.model.Media


@Composable
fun MediaView(
    media: Media,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color: Color = MaterialTheme.colors.secondary
    val tint: Color = MaterialTheme.colors.onSecondary

    val mediaIcon = remember {
        if (media is Media.Image) R.drawable.ic_round_image_24 else R.drawable.ic_round_video
    }
    BoxWithConstraints(modifier = modifier) {
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .size(maxWidth)
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        bottomEnd = 16.dp,
                        topEnd = 8.dp,
                        bottomStart = 8.dp
                    )
                )
                .border(width = 8.dp, color = color)
        ) {
            Box(
            ) {
                // background image
                Image(
                    bitmap = media.preview, null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // top left icon
                Icon(
                    painter = painterResource(id = mediaIcon),
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            color = color,
                            shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                        )
                        .padding(8.dp)
                )

                // bottom right icon button
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(
                            color = color,
                            shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_downloaad),
                        contentDescription = null,
                        tint = tint
                    )
                }
            }
        }

    }
}




@Composable
fun CircularButton(
    icon: ImageVector,
    color: Color = MaterialTheme.colors.primary,
    iconColor: Color = MaterialTheme.colors.contentColorFor(color),
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(color)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "menu",
            tint = iconColor
        )
    }
}

@Composable
fun Fab(expanded: Boolean, onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Row {
            Icon(Icons.Rounded.Add, null)
            if (expanded) {
                Spacer(Modifier.width(16.dp))
                Text("EDIT", style = MaterialTheme.typography.button)
            }
        }
    }
}
