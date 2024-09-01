package com.sentimatedetactionsample.faces

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sentimatedetactionsample.particleflow.CreateParticles
import com.sentimatedetactionsample.particleflow.particle.Acceleration
import com.sentimatedetactionsample.particleflow.particle.EmissionType
import com.sentimatedetactionsample.particleflow.particle.Force
import com.sentimatedetactionsample.particleflow.particle.LifeTime
import com.sentimatedetactionsample.particleflow.particle.ParticleColor
import com.sentimatedetactionsample.particleflow.particle.ParticleSize
import com.sentimatedetactionsample.particleflow.particle.Velocity

@Composable
fun FancyEmojiCanvasAnimation1() {
    var currentSentiment by remember { mutableStateOf(SentimentCanvas.Happy) }

    // Smooth color transition based on sentiment
    val faceColor by animateColorAsState(
        targetValue = when (currentSentiment) {
            SentimentCanvas.Happy -> Color.Green
            SentimentCanvas.Sad -> Color.Yellow
            SentimentCanvas.Angry -> Color.Red
        },
        animationSpec = tween(durationMillis = 1000)
    )

    // Eyebrow and mouth transition
    val eyebrowMovement by animateFloatAsState(
        targetValue = when (currentSentiment) {
            SentimentCanvas.Happy -> 0f
            SentimentCanvas.Sad -> -1f
            SentimentCanvas.Angry -> 1f
        },
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    val mouthCurve by animateFloatAsState(
        targetValue = when (currentSentiment) {
            SentimentCanvas.Happy -> 1f // Smile
            SentimentCanvas.Sad -> -1f // Frown
            SentimentCanvas.Angry -> -1f // Frown
        },
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    val particleColor = remember {
        mutableStateOf(ParticleColor.SingleColor(Color.Red))
    }

    LaunchedEffect(currentSentiment) {
        particleColor.value = ParticleColor.SingleColor(
            when (currentSentiment) {
                SentimentCanvas.Happy -> Color.Green.copy(alpha = 0.5f)
                SentimentCanvas.Sad -> Color.Yellow.copy(alpha = 1f)
                SentimentCanvas.Angry -> Color.Red.copy(alpha = 0.5f)
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            AnimatedFogEffect(sentiment = currentSentiment)
            CreateParticles(
                modifier = Modifier.size(200.dp),
                // Set the velocity of particle in x and y direction
                x = 500f,
                y = 700f,
                velocity = Velocity(
                    xDirection = 1f,
                    yDirection = 1f
                ),
                // Set the force acting on particle
                force = Force.Gravity(0f),
                // set acceleration on both x and y direction
                acceleration = Acceleration(0f, 0f),
                // set the desired size of particle that you want
                particleSize = ParticleSize.RandomSizes(5..25),
                // set particle colors or color
                particleColor = particleColor.value,
                // set the max lifetime and aging factor of a particle
                lifeTime = LifeTime(255f, 0.2f),
                // set the emission type - how do you want to generate particle - as a flow/stream, as a explosion/blast
//                emissionType = EmissionType.ExplodeEmission(numberOfParticles = 100),
                emissionType = EmissionType.FlowEmission(
                    maxParticlesCount = Int.MAX_VALUE,
                    emissionRate = 0.5f
                ),
                // duration of animation
                durationMillis = 10 * 1000
            )

            Canvas(modifier = Modifier.size(500.dp)) {
                drawFancyFace(
                    sentiment = currentSentiment,
                    eyeBlink = 1f,
                    eyebrowMovement = eyebrowMovement,
                    mouthCurve = mouthCurve,
                    faceColor = faceColor
                )
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        SentimentButtonsCanvas { sentiment ->
            currentSentiment = sentiment
        }
    }
}

@Composable
fun AnimatedFogEffect(sentiment: SentimentCanvas) {
    // Define colors for each sentiment
    val targetColor = when (sentiment) {
        SentimentCanvas.Happy -> Color.Green.copy(alpha = 0.2f)
        SentimentCanvas.Sad -> Color.Black.copy(alpha = 0.2f)
        SentimentCanvas.Angry -> Color.Red.copy(alpha = 0.2f)
    }

    // Animate the color transition
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ) // Adjust duration and easing as needed
    )

    Canvas(modifier = Modifier.size(500.dp)) {
        drawFogEffect(animatedColor)
    }
}

fun DrawScope.drawFogEffect(fogColor: Color) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(fogColor, fogColor.copy(alpha = 0f)),
            center = center,
            radius = size.maxDimension / 1.5f
        ),
        size = size,
        style = Fill
    )
}


fun DrawScope.drawFancyFace(
    sentiment: SentimentCanvas,
    eyeBlink: Float,
    eyebrowMovement: Float,
    mouthCurve: Float,
    faceColor: Color
) {
    drawFaceBackground(faceColor)
    drawEyes(eyeBlink)
    drawEyebrows(eyebrowMovement)

    when (sentiment) {
        SentimentCanvas.Happy -> drawSmileCurve(
            Offset(center.x, center.y - 10.dp.toPx()),
            sentiment = sentiment,
            progress = mouthCurve // Smile
        )

        SentimentCanvas.Sad -> drawSmileCurve(
            Offset(center.x, center.y + 10.dp.toPx()),
            sentiment = sentiment,
            progress = mouthCurve // Frown
        )

        SentimentCanvas.Angry -> drawSmileCurve(
            Offset(center.x, center.y + 10.dp.toPx()),
            sentiment = sentiment,
            progress = mouthCurve // Straight line
        )
    }
}

fun DrawScope.drawFaceBackground(color: Color) {
    val gradient = Brush.radialGradient(
        colors = listOf(color, color.copy(0.9f), color.copy(0.7f)),
        center = center,
        radius = 80.dp.toPx()
    )

    drawRoundRect(
        brush = gradient,
        size = Size(160.dp.toPx(), 160.dp.toPx()),
        topLeft = Offset(center.x - 80.dp.toPx(), center.y - 80.dp.toPx()),
        cornerRadius = CornerRadius(80.dp.toPx())
    )
}

fun DrawScope.drawEyes(eyeBlink: Float) {
    drawEye(Offset(center.x - 30.dp.toPx(), center.y - 30.dp.toPx()), eyeBlink)
    drawEye(Offset(center.x + 30.dp.toPx(), center.y - 30.dp.toPx()), eyeBlink)
}

fun DrawScope.drawEye(position: Offset, eyeBlink: Float) {
//    val eyeRadius = 10.dp.toPx() * eyeBlink
    val eyeRadius = 10.dp.toPx()

    if (eyeBlink > 0.5f) {
        drawCircle(
            color = Color.Black,
            center = position,
            radius = eyeRadius,
            style = Fill
        )
        drawCircle(
            color = Color.White,
            center = position.copy(y = position.y - 4.dp.toPx(), x = position.x - 3.dp.toPx()),
            radius = 3.dp.toPx() * eyeBlink,
            style = Fill
        )
    } else {
        // Draw a closed eye as a thin horizontal line
        drawLine(
            color = Color.Black,
            start = position.copy(x = position.x - 10.dp.toPx(), y = position.y),
            end = position.copy(x = position.x + 10.dp.toPx(), y = position.y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

fun DrawScope.drawEyebrows(eyebrowMovement: Float) {
    val eyebrowOffset = 10.dp.toPx() * eyebrowMovement

    // Left eyebrow
    drawLine(
        color = Color.Black,
        start = Offset(center.x - 50.dp.toPx(), center.y - 50.dp.toPx() - eyebrowOffset),
        end = Offset(center.x - 10.dp.toPx(), center.y - 50.dp.toPx() + eyebrowOffset),
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Right eyebrow
    drawLine(
        color = Color.Black,
        start = Offset(center.x + 10.dp.toPx(), center.y - 50.dp.toPx() + eyebrowOffset),
        end = Offset(center.x + 50.dp.toPx(), center.y - 50.dp.toPx() - eyebrowOffset),
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round
    )
}

fun DrawScope.drawSmileCurve(position: Offset, sentiment: SentimentCanvas, progress: Float) {
    when (sentiment) {
        SentimentCanvas.Happy -> {
            val startAngle = 30f
            val sweepAngle = 120f * progress
            val topLeftOffset = position.copy(x = position.x - 50.dp.toPx(), y = position.y)
            val size = Size(100.dp.toPx(), 50.dp.toPx())

            drawArc(
                color = Color.Black,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeftOffset,
                size = size,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        SentimentCanvas.Sad,SentimentCanvas.Angry -> {
            val startAngle = 210f
            val sweepAngle = -120f * progress
            val topLeftOffset = position.copy(x = position.x - 50.dp.toPx(), y = position.y)
            val size = Size(100.dp.toPx(), 50.dp.toPx())

            drawArc(
                color = Color.Black,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeftOffset,
                size = size,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }

//        SentimentCanvas.Angry -> {
//            drawAngryMouth(position, progress)
//        }
    }
}

fun DrawScope.drawAngryMouth(position: Offset, progress: Float) {
    val zigZagWidth = 100.dp.toPx() // Fixed width for the zig-zag pattern
    val startX = position.x - zigZagWidth / 2
    val startY = position.y + 30.dp.toPx() // Adjusted position for mouth

    val path = Path().apply {
        moveTo(startX, startY)
        val numZigZags = 4
        val segmentWidth = zigZagWidth / numZigZags

        // Draw full zig-zag segments based on progress
        val fullSegments = (numZigZags).toInt()
        for (i in 0 until fullSegments) {
            val xOffset = segmentWidth
            val yOffset = if (i % 2 == 0) 10.dp.toPx() else -10.dp.toPx()
            lineTo(startX + (i + 1) * xOffset, startY + yOffset)
        }

        // Draw partial segment if needed
        val remainingProgress = (numZigZags) - fullSegments
        if (remainingProgress > 0f && fullSegments < numZigZags) {
            val xOffset = segmentWidth * remainingProgress
            val yOffset = if (fullSegments % 2 == 0) 10.dp.toPx() else -10.dp.toPx()
            lineTo(startX + (fullSegments + 1) * segmentWidth + xOffset, startY + yOffset)
        }
    }

    drawPath(
        path = path,
        color = Color.Black,
        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
    )
}

@Composable
fun SentimentButtonsCanvas(onSentimentChange: (SentimentCanvas) -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = { onSentimentChange(SentimentCanvas.Happy) }) {
            Text(text = "Happy")
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(onClick = { onSentimentChange(SentimentCanvas.Sad) }) {
            Text(text = "Sad")
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(onClick = { onSentimentChange(SentimentCanvas.Angry) }) {
            Text(text = "Angry")
        }
    }
}

enum class SentimentCanvas {
    Happy, Sad, Angry
}

