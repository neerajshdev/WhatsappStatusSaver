package com.nibodev.statussaver.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nibodev.statussaver.ui.Show
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import com.nibodev.statussaver.R

@Composable
fun AppDrawer(modifier: Modifier = Modifier, onDrawerItemSelect: (Show) -> Unit) {
    val dividerColor = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        DrawerHeader()

        IconText(
            label = "Home",
            iconResId = R.drawable.ic_round_home_24,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onDrawerItemSelect(Show.HOME_CONTENT) }),
        )

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = dividerColor
        )

        IconText(
            modifier = Modifier.fillMaxWidth(),
            label = "Recent Media",
            iconResId = R.drawable.ic_box
        )

        Column {
            Text(
                text = "Videos",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface.copy(0.7f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onDrawerItemSelect(Show.RECENT_VIDEO_LIST) })
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Text(
                text = "Images",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface.copy(0.7f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onDrawerItemSelect(Show.RECENT_IMAGE_LIST) })
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = dividerColor
        )

        IconText(
            iconResId = R.drawable.ic_round_download_done_24,
            label = "Saved Media",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onDrawerItemSelect(Show.HOME_CONTENT) })
        )

        Column {
            Text(
                text = "Videos",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface.copy(0.7f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onDrawerItemSelect(Show.SAVED_VIDEO_LIST) })
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Images",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface.copy(0.7f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onDrawerItemSelect(Show.SAVED_IMAGE_LIST) })
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }

    }
}


@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_image),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
        )
        Text(
            text = "Status Saver",
            fontSize = 26.sp,
            color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "download whatsapp status easily",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun IconText(iconResId: Int, label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(vertical = 16.dp)
            .padding(start = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun AppDrawerPrev() {
    WhatsappStatusSaverTheme {
        AppDrawer {}
    }
}
