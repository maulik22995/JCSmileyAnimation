package com.sentimatedetactionsample.particleflow

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import com.sentimatedetactionsample.particleflow.emitter.ParticleExplodeEmitter
import com.sentimatedetactionsample.particleflow.emitter.ParticleFlowEmitter
import com.sentimatedetactionsample.particleflow.particle.Acceleration
import com.sentimatedetactionsample.particleflow.particle.EmissionType
import com.sentimatedetactionsample.particleflow.particle.Force
import com.sentimatedetactionsample.particleflow.particle.LifeTime
import com.sentimatedetactionsample.particleflow.particle.ParticleColor
import com.sentimatedetactionsample.particleflow.particle.ParticleConfigData
import com.sentimatedetactionsample.particleflow.particle.ParticleSize
import com.sentimatedetactionsample.particleflow.particle.Velocity
import com.sentimatedetactionsample.particleflow.particle.createForceVector
import com.sentimatedetactionsample.particleflow.particle.getExactColor

@Composable
fun CreateParticles(
    modifier: Modifier = Modifier,
    x: Float = 0f,
    y: Float = 0f,
    velocity: Velocity = Velocity(xDirection = 1f, yDirection = 1f),
    force: Force = Force.Gravity(0.0f),
    acceleration: Acceleration = Acceleration(0f, 0f),
    particleSize: ParticleSize = ParticleSize.ConstantSize(),
    particleColor: ParticleColor = ParticleColor.SingleColor(),
    lifeTime: LifeTime = LifeTime(255f, 1f),
    emissionType: EmissionType = EmissionType.ExplodeEmission(),
    durationMillis: Int = 10000,
) {

    val dt = remember { mutableStateOf(0f) }

    var startTime by remember { mutableStateOf(0L) }
    var previousTime by remember { mutableStateOf(System.nanoTime()) }

    val particleConfigData = remember(particleColor) {
        ParticleConfigData(
            x, y, velocity, force, acceleration, particleSize, particleColor, lifeTime, emissionType
        )
    }

    Log.d("updated color>>", "CreateParticles: ${particleConfigData.particleColor.getExactColor()}")

    val emitter = remember {
        when (emissionType) {
            is EmissionType.ExplodeEmission -> {
                ParticleExplodeEmitter(emissionType.numberOfParticles, particleConfigData)
            }

            is EmissionType.FlowEmission -> {
                ParticleFlowEmitter(
                    durationMillis,
                    emissionType,
                    particleConfigData
                )
            }
        }
    }

    startTime = System.currentTimeMillis()
    LaunchedEffect(Unit) {
        val condition = if (emissionType is EmissionType.FlowEmission &&
            emissionType.maxParticlesCount == EmissionType.FlowEmission.INDEFINITE
        ) {
            true
        } else {
            System.currentTimeMillis() - startTime < durationMillis
        }
        while (condition) {
            withFrameNanos {
                dt.value = ((it - previousTime) / 1E7).toFloat()
                previousTime = it
            }
        }
    }

    Log.d("dt.value >>",dt.value.toString())

    Canvas(modifier) {
        emitter.render(this)
        emitter.applyForce(force.createForceVector())
        emitter.update(dt.value, particleColor.getExactColor())
    }
}