package com.dede.basic.utils.dynamic

import android.util.Log
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal class ReflectDynamicObject(obj: Any?, clazz: Class<*>) : DynamicObject(obj, clazz) {

    companion object {
        private val Method.isStatic: Boolean
            get() = Modifier.isStatic(modifiers)

        private val Field.isStatic: Boolean
            get() = Modifier.isStatic(modifiers)

        private val Field.isFinal: Boolean
            get() = Modifier.isFinal(modifiers)

        private fun Class<*>.findMethod(name: String, vararg types: Class<out Any>): Method? {
            var method: Method? = null
            var error: Throwable? = null
            try {
                method = this.getMethod(name, *types)
            } catch (e: Throwable) {
                error = e
            }
            if (method == null) {
                error = null
                try {
                    method = this.getDeclaredMethod(name, *types)
                } catch (e: Throwable) {
                    error = e
                }
            }
            if (error != null) {
                Log.w(TAG, error)
            }
            return method
        }

        private fun Class<*>.findField(name: String): Field? {
            var field: Field? = null
            var error: Throwable? = null
            try {
                field = this.getField(name)
            } catch (e: Throwable) {
                error = e
            }
            if (field == null) {
                error = null
                try {
                    field = this.getDeclaredField(name)
                } catch (e: Throwable) {
                    error = e
                }
            }
            if (error != null) {
                Log.w(TAG, error)
            }
            return field
        }

        private fun Class<*>.pickConstructor(vararg types: Class<out Any>): Constructor<*>? {
            var constructor: Constructor<*>? = null
            var error: Throwable? = null
            try {
                constructor = this.getConstructor(*types)
            } catch (e: Throwable) {
                error = e
            }
            if (constructor == null) {
                error = null
                try {
                    constructor = this.getDeclaredConstructor(*types)
                } catch (e: Throwable) {
                    error = e
                }
            }
            if (error != null) {
                Log.w(TAG, error)
            }
            return constructor
        }
    }

    override fun invokeMethod(
        name: String,
        argumentsType: Array<Class<*>>,
        arguments: Array<out Any?>
    ): DynamicResult {
        val method = clazz.findMethod(name, *argumentsType) ?: return DynamicResult.notFound()
        try {
            method.isAccessible = true
            val value = if (method.isStatic) {
                method.invoke(null, *arguments)
            } else {
                method.invoke(obj, *arguments)
            }
            return DynamicResult.success(value)
        } catch (e: Throwable) {
            Log.w(TAG, e)
            return DynamicResult.error(e)
        }
    }

    override fun getProperty(name: String): DynamicResult {
        val field = clazz.findField(name) ?: return DynamicResult.notFound()
        try {
            field.isAccessible = true
            val value = if (field.isStatic) {
                field.get(null)
            } else {
                field.get(obj)
            }
            return DynamicResult.success(value)
        } catch (e: Throwable) {
            Log.w(TAG, e)
            return DynamicResult.error(e)
        }
    }

    override fun setProperty(name: String, value: Any?): DynamicResult {
        val field = clazz.findField(name) ?: return DynamicResult.notFound()
        if (field.isFinal) {
            val message = "Property $name is final!"
            Log.w(TAG, message)
            return DynamicResult.error(IllegalStateException(message))
        }
        try {
            field.isAccessible = true
            if (field.isStatic) {
                field.set(null, value)
            } else {
                field.set(obj, value)
            }
            return DynamicResult.success()
        } catch (e: Throwable) {
            Log.w(TAG, e)
            return DynamicResult.error(e)
        }
    }

    override fun newInstance(
        argumentsType: Array<Class<out Any>>,
        arguments: Array<out Any?>
    ): DynamicResult {
        val constructor = clazz.pickConstructor(*argumentsType) ?: return DynamicResult.notFound()
        try {
            constructor.isAccessible = true
            val value = constructor.newInstance(*arguments)
            return DynamicResult.success(value)
        } catch (e: Throwable) {
            Log.w(TAG, e)
            return DynamicResult.error(e)
        }
    }
}