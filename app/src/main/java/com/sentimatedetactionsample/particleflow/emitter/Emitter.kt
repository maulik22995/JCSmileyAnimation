package com.sentimatedetactionsample.particleflow.emitter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.sentimatedetactionsample.particleflow.core.Vector2D
import com.sentimatedetactionsample.particleflow.particle.Particle
import com.sentimatedetactionsample.particleflow.particle.createAccelerationVector
import com.sentimatedetactionsample.particleflow.particle.createVelocityVector
import com.sentimatedetactionsample.particleflow.particle.getExactColor
import com.sentimatedetactionsample.particleflow.particle.getExactSize

internal abstract class Emitter(
    private val particleConfigData: com.sentimatedetactionsample.particleflow.particle.ParticleConfigData
) {

    val particlePool = mutableListOf<com.sentimatedetactionsample.particleflow.particle.Particle>()

    abstract fun generateParticles(numberOfParticles: Int)

    fun addParticle() {
        val particle = createFreshParticle()
        particlePool.add(particle)
    }

    private fun createFreshParticle(): com.sentimatedetactionsample.particleflow.particle.Particle {
        return Particle(
            initialX = particleConfigData.x,
            initialY = particleConfigData.y,
            color = particleConfigData.particleColor.getExactColor(),
            size = particleConfigData.particleSize.getExactSize(),
            velocity = particleConfigData.velocity.createVelocityVector(),
            acceleration = particleConfigData.acceleration.createAccelerationVector(),
            lifetime = particleConfigData.lifeTime.maxLife,
            agingFactor = particleConfigData.lifeTime.agingFactor,
        )
    }

    fun applyForce(force: Vector2D) {
        for (particle in particlePool) {
            particle.applyForce(force)
        }
    }

    abstract fun update(dt: Float, color: Color)

    fun render(drawScope: DrawScope) {
        for (particle in particlePool) {
            particle.show(drawScope)
        }
    }

}