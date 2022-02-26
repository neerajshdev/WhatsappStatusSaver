package com.ns.whatsappstatussaver.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.ns.whatsappstatussaver.R


@Composable
fun TopBar(onMenuClick: () -> Unit = {}, onShareClick: () -> Unit = {}, title: String) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_menu_24),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_share_24),
                    contentDescription = null
                )
            }
        }
    )
}