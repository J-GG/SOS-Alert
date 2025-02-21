package fr.jg.sosalert.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


enum class MultiFabState {
    COLLAPSED, EXPANDED
}

class FabItem(
    val icon: ImageVector,
    val label: String,
    val onFabItemClicked: () -> Unit
)

@Composable
fun MultiFloatingActionButton(
    fabIcon: ImageVector,
    items: List<FabItem>,
    showLabels: Boolean = true,
    onStateChanged: ((state: MultiFabState) -> Unit)? = null
) {
    var currentState by remember { mutableStateOf(MultiFabState.COLLAPSED) }
    val stateTransition: Transition<MultiFabState> =
        updateTransition(targetState = currentState, label = "")
    val stateChange: () -> Unit = {
        currentState = if (currentState == MultiFabState.EXPANDED) {
            MultiFabState.COLLAPSED
        } else MultiFabState.EXPANDED
        onStateChanged?.invoke(currentState)
    }
    val rotation: Float by stateTransition.animateFloat(
        transitionSpec = {
            if (targetState == MultiFabState.EXPANDED) {
                spring(stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = ""
    ) { state ->
        if (state == MultiFabState.EXPANDED) 45f else 0f
    }
    val isEnable = currentState == MultiFabState.EXPANDED

    BackHandler(isEnable) {
        currentState = MultiFabState.COLLAPSED
    }

    val modifier = if (currentState == MultiFabState.EXPANDED) {
        Modifier
            .fillMaxSize()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                currentState = MultiFabState.COLLAPSED
            }
    } else Modifier.fillMaxSize()

    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                items.forEach { item ->
                    SmallFloatingActionButtonRow(
                        item = item,
                        stateTransition = stateTransition,
                        showLabel = showLabels,
                        onFabItemClicked = {
                            item.onFabItemClicked()
                            stateChange()
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {
                        stateChange()
                    }) {
                    Icon(
                        imageVector = fabIcon,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }

        }
    }

}


@Composable
fun SmallFloatingActionButtonRow(
    item: FabItem,
    showLabel: Boolean,
    stateTransition: Transition<MultiFabState>,
    onFabItemClicked: () -> Unit
) {
    val alpha: Float by stateTransition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 50)
        }, label = ""
    ) { state ->
        if (state == MultiFabState.EXPANDED) 1f else 0f
    }

    val scale: Float by stateTransition.animateFloat(
        label = ""
    ) { state ->
        if (state == MultiFabState.EXPANDED) 1.0f else 0f
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .alpha(animateFloatAsState((alpha), label = "").value)
            .scale(animateFloatAsState(targetValue = scale, label = "").value)
    ) {
        if (showLabel) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                    .clickable(onClick = onFabItemClicked)
            ) {
                Text(
                    text = item.label,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
        SmallFloatingActionButton(
            shape = CircleShape,
            modifier = Modifier
                .padding(4.dp),
            onClick = onFabItemClicked,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label
            )
        }
    }
}