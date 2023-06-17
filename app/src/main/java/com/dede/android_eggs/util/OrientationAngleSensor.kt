package com.dede.android_eggs.util

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.view.Display
import android.view.Surface
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

// https://developer.android.google.cn/guide/topics/sensors/sensors_position?hl=zh-cn#sensors-pos-orient
@Suppress("MemberVisibilityCanBePrivate")
class OrientationAngleSensor(
    private val context: Context, private val owner: LifecycleOwner?,
    private val onOrientationAnglesUpdate: (zAngle: Float, xAngle: Float, yAngle: Float) -> Unit,
    private val handleDefaultOffset: Boolean = true
) : SensorEventListener, DefaultLifecycleObserver {

    private val sensorManager: SensorManager = context.getSystemService()!!
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val remapRotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val defaultOrientationAngles = FloatArray(3)

    init {
        owner?.lifecycle?.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        resetDefaultOrientationAngles()
    }

    override fun onResume(owner: LifecycleOwner) {
        start()
    }

    override fun onPause(owner: LifecycleOwner) {
        stop()
    }

    fun resetDefaultOrientationAngles() {
        for (i in defaultOrientationAngles.indices)
            defaultOrientationAngles[i] = Float.NaN
    }

    private fun isInvalidDefaultOrientationAngles(): Boolean {
        // default is NaN
        return !defaultOrientationAngles[0].isNaN() &&
                // in most cases, it's not equal to 0
                defaultOrientationAngles[1] != 0f && defaultOrientationAngles[2] != 0f
    }

    fun start() {
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                //SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                //SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun stop() {
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }

    fun destroy() {
        stop()
        owner?.lifecycle?.removeObserver(this)
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(
                    event.values, 0,
                    accelerometerReading, 0, accelerometerReading.size
                )
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(
                    event.values, 0,
                    magnetometerReading, 0, magnetometerReading.size
                )
            }
        }
        updateOrientationAngles()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    private fun updateOrientationAngles() {
        // Rotation matrix based on current readings from accelerometer and magnetometer.
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // The Angle is converted according to the device orientation.
        var xAxis = SensorManager.AXIS_X
        var yAxis = SensorManager.AXIS_Y
        when (val rotation = context.getRotation()) {
            Surface.ROTATION_0 -> {
                // default
            }
            Surface.ROTATION_90 -> {
                xAxis = SensorManager.AXIS_Y
                yAxis = SensorManager.AXIS_MINUS_X
            }
            Surface.ROTATION_180 -> {
                xAxis = SensorManager.AXIS_MINUS_X
                yAxis = SensorManager.AXIS_MINUS_Y
            }
            Surface.ROTATION_270 -> {
                xAxis = SensorManager.AXIS_MINUS_Y
                yAxis = SensorManager.AXIS_X
            }
            else -> throw IllegalArgumentException("Display rotation error: %d".format(rotation))
        }
        SensorManager.remapCoordinateSystem(rotationMatrix, xAxis, yAxis, remapRotationMatrix)
        // Express the updated rotation matrix as three orientation angles.
        SensorManager.getOrientation(remapRotationMatrix, orientationAngles)
        // "mOrientationAngles" now has up-to-date information.

        // Handles the default offset.
        if (handleDefaultOffset) {
            // take the result of the first callback as the default offset
            if (!isInvalidDefaultOrientationAngles()) {
                for (i in orientationAngles.indices) {
                    defaultOrientationAngles[i] = 0f - orientationAngles[i]
                }
            }
            for (i in 1 until orientationAngles.size) {
                // add offset
                orientationAngles[i] = orientationAngles[i] + defaultOrientationAngles[i]
            }
        }

        val zAngle: Float = orientationAngles[0]
        val xAngle: Float = orientationAngles[1]
        val yAngle: Float = orientationAngles[2]
        onOrientationAnglesUpdate(zAngle, xAngle, yAngle)
    }

    private fun Context.getRotation(): Int {
        var currentDisplay: Display? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            currentDisplay = display
        }
        if (currentDisplay == null && this is Activity) {
            @Suppress("DEPRECATION")
            currentDisplay = windowManager.defaultDisplay
        }
        if (currentDisplay != null) {
            return currentDisplay.rotation
        }
        return Surface.ROTATION_0
    }
}