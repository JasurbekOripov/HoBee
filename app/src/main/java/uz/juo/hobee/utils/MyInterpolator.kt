package uz.juo.hobee.utils

import kotlin.math.cos
import kotlin.math.pow

class MyInterpolator(var amplitude: Double, var frequency: Double) :
    android.view.animation.Interpolator {

    override fun getInterpolation(time: Float): Float {
        return (-1 * Math.E.pow(-time / amplitude) * cos(frequency * time) + 1).toFloat()
    }
}