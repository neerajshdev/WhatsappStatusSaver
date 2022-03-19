package com.softneez.wasaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.softneez.wasaver.ui.theme.WhatsappStatusSaverTheme
import com.softneez.wasaver.R



@Composable
fun TopBar(onShareClick: () -> Unit = {}, title: String) {
    Box (
        modifier = Modifier.fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
    ){
        Text(text = title, color = MaterialTheme.colors.onPrimary, modifier = Modifier.align(Alignment.Center) )

        IconButton(onClick = onShareClick, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_share_24),
                contentDescription = "Share this app",
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}


@Preview
@Composable
private fun TopBarPrev() {
    WhatsappStatusSaverTheme {
        TopBar({}, "WA saver")
    }
}