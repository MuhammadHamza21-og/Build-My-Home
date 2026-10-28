package com.example.data.converters

import com.example.data.model.MarlaStandard
import com.example.data.model.Point2D
import kotlin.math.abs

object PlotAreaConverter {

    fun marlaToSqFt(marla: Float, standard: MarlaStandard): Float = marla * standard.sqFt

    fun sqFtToMarla(sqFt: Float, standard: MarlaStandard): Float = sqFt / standard.sqFt

    fun kanalToMarla(kanal: Float): Float = kanal * 20.0f

    fun marlaToKanal(marla: Float): Float = marla / 20.0f

    fun kanalToSqFt(kanal: Float, standard: MarlaStandard): Float = kanal * 20.0f * standard.sqFt

    fun sqFtToKanal(sqFt: Float, standard: MarlaStandard): Float = sqFt / (20.0f * standard.sqFt)

    fun acreToKanal(acre: Float): Float = acre * 8.0f

    fun acreToSqFt(acre: Float, standard: MarlaStandard): Float = acre * 8.0f * 20.0f * standard.sqFt

    fun sqFtToAcre(sqFt: Float, standard: MarlaStandard): Float = sqFt / (160.0f * standard.sqFt)

    fun sqFtToSqYards(sqFt: Float): Float = sqFt / 9.0f

    fun sqYardsToSqFt(sqYards: Float): Float = sqYards * 9.0f

    fun sqYardsToMarla(sqYards: Float, standard: MarlaStandard): Float = (sqYards * 9.0f) / standard.sqFt

    /**
     * Compute polygon area in square feet using Shoelace formula
     */
    fun calculatePolygonArea(points: List<Point2D>): Float {
        if (points.size < 3) return 0f
        var sum1 = 0f
        var sum2 = 0f
        val n = points.size
        for (i in 0 until n) {
            val next = (i + 1) % n
            sum1 += points[i].x * points[next].y
            sum2 += points[next].x * points[i].y
        }
        return abs(sum1 - sum2) / 2.0f
    }
}
