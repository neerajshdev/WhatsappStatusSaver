package com.nibodev.statussaver.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.nibodev.statussaver.models.StatusImage
import com.nibodev.statussaver.ui.components.RecentImageCard
import com.nibodev.statussaver.ui.components.NativeAdUnit
import kotlin.random.Random


@Composable
fun ScrollableRecentImageList(
    images: List<StatusImage>,
    modifier: Modifier = Modifier,
    onItemChosen: (StatusImage) -> Unit,
    onDownloadClick: (StatusImage) -> Unit,
    state : LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = state,
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(images.size, key = { images[it].path }) {
            val statusImage = images[it]
            val itemModifier = Modifier
                .padding(vertical = 12.dp)
                .padding(horizontal = 8.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium)

            if (Random.nextBoolean()) NativeAdUnit(itemModifier)
            RecentImageCard(
                image = statusImage.image,
                isSaved = statusImage.isSaved.value,
                onImageClick = { onItemChosen(statusImage) },
                onDownloadClick = {
                    onDownloadClick(statusImage)
                },
                modifier = itemModifier
            )
        }
        item {
            Spacer(modifier = Modifier.size(160.dp))
        }
    }
}