package com.dede.basic.utils

import java.util.UUID
import kotlin.random.Random

/**
 * Cat Eggs Random impl
 *
 * @author shhu
 * @since 2022/8/23
 */
object CatRandom {

    // ThreadLocalRandom.current()
    private val random: Random = Random(UUID.randomUUID().mostSignificantBits)

    @JvmStatic
    fun nextSeed(): Long {
        // Math.abs(ThreadLocalRandom.current().nextInt())
        return random.nextLong()
    }

}