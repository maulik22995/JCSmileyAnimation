package com.sentimatedetactionsample.particleflow.emitter

import androidx.compose.ui.graphics.Color
import com.sentimatedetactionsample.particleflow.particle.ParticleConfigData

internal class ParticleExplodeEmitter(
    numberOfParticles: Int,
    particleConfigData: ParticleConfigData
) : Emitter(particleConfigData) {

    init {
        generateParticles(numberOfParticles)
    }

    override fun generateParticles(numberOfParticles: Int) {
        repeat(numberOfParticles) { addParticle() }
    }

    override fun update(dt: Float, color: Color) {
        for (particle in particlePool) {
            particle.update(dt, color)
        }
        particlePool.removeAll { it.finished() }
    }

}