package com.nibodev.statussaverforwhatsapp.ui.layout

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.nibodev.statussaverforwhatsapp.models.StatusImage
import com.nibodev.statussaverforwhatsapp.ui.components.NativeAdUnit
import com.nibodev.statussaverforwhatsapp.ui.components.SavedImageCard
import kotlin.random.Random

@Composable
fun ScrollableSavedImageList(
    images: List<StatusImage>,
    modifier: Modifier = Modifier,
    onItemChosen: (StatusImage) -> Unit,
    state: LazyListState = rememberLazyListState()
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
            SavedImageCard(
                image = statusImage.image,
                onImageClick = { onItemChosen(statusImage) },
                modifier = itemModifier
            )
        }

        item {
            Spacer(modifier = Modifier.size(160.dp))
        }
    }
}