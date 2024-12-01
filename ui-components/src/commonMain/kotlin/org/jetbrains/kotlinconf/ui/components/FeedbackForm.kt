package org.jetbrains.kotlinconf.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import kotlinconfapp.ui_components.generated.resources.Res
import kotlinconfapp.ui_components.generated.resources.arrow_right_24
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import org.jetbrains.kotlinconf.ui.theme.PreviewHelper

@Composable
fun FeedbackForm(
    emotion: Emotion,
    onSubmit: (comment: String) -> Unit,
    past: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val verticalBorderColor by animateColorAsState(
        if (focused) KotlinConfTheme.colors.strokeInputFocus
        else Color.Transparent,
        animationSpec = spring(stiffness = StiffnessHigh),
    )
    val horizontalBorderColor by animateColorAsState(
        if (focused) KotlinConfTheme.colors.strokeInputFocus
        else KotlinConfTheme.colors.strokePale,
        animationSpec = spring(stiffness = StiffnessHigh),
    )
    val fieldBackgroundColor by animateColorAsState(
        if (past) KotlinConfTheme.colors.mainBackground
        else KotlinConfTheme.colors.tileBackground,
    )

    Box(modifier.fillMaxWidth()) {
        var feedbackText by remember { mutableStateOf("") }
        BasicTextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            interactionSource = interactionSource,
            textStyle = KotlinConfTheme.typography.text1
                .copy(color = KotlinConfTheme.colors.primaryText),
            cursorBrush = SolidColor(KotlinConfTheme.colors.primaryText),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .padding(bottom = 40.dp)
                        .drawBehind {
                            val lineWidth = 1.dp.toPx()
                            // Top
                            drawLine(
                                horizontalBorderColor,
                                Offset(0f, 0f),
                                Offset(size.width, 0f),
                                lineWidth
                            )
                            // Bottom
                            drawLine(
                                horizontalBorderColor,
                                Offset(0f, size.height),
                                Offset(size.width, size.height),
                                lineWidth
                            )
                            // Start
                            drawLine(
                                verticalBorderColor,
                                Offset(0f, 0f),
                                Offset(0f, size.height),
                                lineWidth
                            )
                            // End
                            drawLine(
                                verticalBorderColor,
                                Offset(size.width, 0f),
                                Offset(size.width, size.height),
                                lineWidth
                            )
                        }
                        .background(fieldBackgroundColor)
                        .fillMaxWidth()
                        .heightIn(min = 132.dp)
                        .padding(bottom = 32.dp)
                        .padding(16.dp)
                        .animateContentSize(spring())
                ) {
                    innerTextField()
                    AnimatedVisibility(
                        feedbackText.isEmpty(),
                        enter = fadeIn(tween(10)),
                        exit = fadeOut(tween(10)),
                    ) {
                        StyledText(
                            text = "Type something",
                            style = KotlinConfTheme.typography.text1,
                            color = KotlinConfTheme.colors.placeholderText
                        )
                    }
                }
            }
        )
        Row(
            modifier = Modifier
                .height(80.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom
        ) {
            KodeeEmotion(emotion = emotion)
            Spacer(Modifier.weight(1f))
            Action(
                label = "Send",
                icon = Res.drawable.arrow_right_24,
                size = ActionSize.Large,
                enabled = feedbackText.isNotEmpty(),
                onClick = { onSubmit(feedbackText) },
            )
        }
    }
}

@Preview
@Composable
fun FeedbackFormPreview() {
    PreviewHelper {
        FeedbackForm(Emotion.Positive, { text -> println("Feedback: $text") }, true)
        FeedbackForm(Emotion.Negative, { text -> println("Feedback: $text") }, false)
    }
}
