@file:JvmName("DynamicObjectUtils")

package com.dede.basic

import android.util.Log
import java.lang.reflect.Method
import java.lang.reflect.Modifier


object DynamicObjectUtils {

    fun findClass(className: String): Class<*>? {
        return try {
            Class.forName(className)
        } catch (e: Exception) {
            null
        }
    }

    fun asDynamicObject(obj: Any): DynamicObject {
        return ReflectDynamicObject(obj, obj.javaClass)
    }

    fun asDynamicObject(clazz: Class<*>): DynamicObject {
        return ReflectDynamicObject(null, clazz)
    }

    fun asDynamicObject(obj: Any?, className: String): DynamicObject {
        val clazz = findClass(className) ?: return NotFoundDynamicObject()
        return ReflectDynamicObject(obj, clazz)
    }
}

private class NotFoundDynamicObject : DynamicObject(null, NotFoundDynamicObject::class.java) {
    override fun tryInvokeMethod(name: String, vararg arguments: Any?): DynamicInvokeResult {
        return DynamicInvokeResult.notFound()
    }

    override fun tryGetProperty(name: String): DynamicInvokeResult {
        return DynamicInvokeResult.notFound()
    }

    override fun trySetProperty(name: String, value: Any?): DynamicInvokeResult {
        return DynamicInvokeResult.notFound()
    }
}

private class ReflectDynamicObject(obj: Any?, clazz: Class<*>) : DynamicObject(obj, clazz) {

    companion object {
        private val EMPTY_CLASS_ARRAY = emptyArray<Class<*>?>()
    }

    private fun inferTypes(vararg arguments: Any?): Array<Class<*>?> {
        if (arguments.isNotEmpty()) {
            return arguments.map { it?.javaClass }.toTypedArray()
        }
        return EMPTY_CLASS_ARRAY
    }

    override fun tryInvokeMethod(name: String, vararg arguments: Any?): DynamicInvokeResult {
        val types = inferTypes(*arguments)
        var method: Method? = null
        try {
            method = clazz.getMethod(name, *types)
            if (method == null) {
                method = clazz.getDeclaredMethod(name, *types)
            }
        } catch (e: Exception) {
        }
        if (method == null) {
            return DynamicInvokeResult.notFound()
        }
        try {
            method.isAccessible = true
            val value = if (Modifier.isStatic(method.modifiers)) {
                method.invoke(null, *arguments)
            } else {
                method.invoke(obj, *arguments)
            }
            return DynamicInvokeResult(value)
        } catch (e: Exception) {
        }
        return DynamicInvokeResult.nil()
    }

    override fun tryGetProperty(name: String): DynamicInvokeResult {
        throw UnsupportedOperationException()
    }

    override fun trySetProperty(name: String, value: Any?): DynamicInvokeResult {
        throw UnsupportedOperationException()
    }
}

abstract class DynamicObject(protected val obj: Any?, protected val clazz: Class<*>) : MethodAccess,
    PropertyAccess


class DynamicInvokeResult(private val value: Any? = null) {

    fun getValue(): Any? {
        if (isFound()) {
            return value
        }
        Log.w(TAG, IllegalStateException("Not Found!"))
        return null
    }

    fun isFound(): Boolean {
        return value != NO_VALUE
    }

    companion object {
        internal const val TAG = "DynamicInvokeResult"

        private val NO_VALUE = Any()

        private val NOT_FOUND = DynamicInvokeResult(NO_VALUE)
        private val NULL = DynamicInvokeResult(null)

        fun notFound(): DynamicInvokeResult {
            return NOT_FOUND
        }

        fun nil(): DynamicInvokeResult {
            return NULL
        }

        inline fun <reified T> DynamicInvokeResult.getValue(): T? {
            val value = this.getValue() ?: return null
            val tValue = value as? T
            if (tValue == null) {
                val name = value.javaClass.name
                val tName = T::class.java.name
                Log.w("DynamicInvokeResult", TypeCastException("Cannot cast $name to $tName"))
            }
            return tValue
        }
    }
}

private interface PropertyAccess {
    fun tryGetProperty(name: String): DynamicInvokeResult
    fun trySetProperty(name: String, value: Any?): DynamicInvokeResult
}

private interface MethodAccess {
    fun tryInvokeMethod(name: String, vararg arguments: Any?): DynamicInvokeResult
}
