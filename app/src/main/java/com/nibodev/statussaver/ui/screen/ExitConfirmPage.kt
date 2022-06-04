package com.nibodev.statussaver.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nibodev.statussaver.R
import com.nibodev.statussaver.ui.LocalNavController
import com.nibodev.statussaver.ui.components.NativeMediumAdUnit
import com.nibodev.statussaver.ui.exitConfirmNativeAdManager


@Composable
fun ExitConfirmPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NativeMediumAdUnit(nativeAdManager = exitConfirmNativeAdManager, modifier = Modifier.weight(0.5f))
        Spacer(modifier = Modifier.height(64.dp))
        Dialog(modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun Dialog(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    Column(modifier = modifier,  horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.exit_confirm_text),
        )
        ConfirmButtons { wantToExit ->
            if (wantToExit) {
                navController.exitApp()
            } else {
                navController.pop()
            }
        }
    }
}

@Composable
private fun ConfirmButtons(wantToExit: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { wantToExit(false) }, modifier = Modifier.weight(1f)) {
            Text(text = stringResource(R.string.confirm_no))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { wantToExit(true) }, modifier = Modifier.weight(1f)) {
            Text(text = stringResource(R.string.confirm_yes))
        }
    }
}