package com.nibodev.statussaverforwhatsapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nibodev.statussaverforwhatsapp.ui.theme.WhatsappStatusSaverTheme
import com.nibodev.statussaverforwhatsapp.R


@Composable
fun TopBar(onShareClick: () -> Unit = {}, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.align(Alignment.Center)
                .padding(vertical = 8.dp)
        )

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