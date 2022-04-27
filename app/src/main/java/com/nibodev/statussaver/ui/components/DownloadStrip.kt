package com.nibodev.statussaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nibodev.statussaver.R

@Composable
fun DownloadStrip(
    downloadDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val id = if (downloadDone) R.drawable.ic_round_download_done_24 else
        R.drawable.ic_round_download_24

    val itemModifier = modifier
        .width(320.dp)
        .background(color = MaterialTheme.colors.primary)

    IconButton(onClick = { if (!downloadDone) onClick() }, modifier = itemModifier) {
        Icon(
            painter = painterResource(id),
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary,
        )
    }


}