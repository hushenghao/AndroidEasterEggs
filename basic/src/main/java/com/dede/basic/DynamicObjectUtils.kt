@file:JvmName("DynamicObjectUtils")
@file:Suppress("MemberVisibilityCanBePrivate")

package com.dede.basic

import android.util.Log
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass


object DynamicObjectUtils {

    fun asDynamicObject(obj: Any): DynamicObject {
        return ReflectDynamicObject(obj, obj.javaClass)
    }

    fun asDynamicObject(clazz: Class<out Any>): DynamicObject {
        return ReflectDynamicObject(null, clazz)
    }

    fun asDynamicObject(clazz: KClass<out Any>): DynamicObject {
        return asDynamicObject(clazz.java)
    }

}

private class ReflectDynamicObject(obj: Any?, clazz: Class<out Any>) : DynamicObject(obj, clazz) {

    companion object {
        private val EMPTY_CLASS_ARRAY = emptyArray<Class<out Any>?>()

        private val Method.isStatic: Boolean
            get() = Modifier.isStatic(modifiers)

        private val Field.isStatic: Boolean
            get() = Modifier.isStatic(modifiers)

        private val Field.isFinal: Boolean
            get() = Modifier.isFinal(modifiers)
    }

    private fun inferTypes(vararg arguments: Any?): Array<Class<out Any>?> {
        if (arguments.isNotEmpty()) {
            return arguments.map { it?.javaClass }.toTypedArray()
        }
        return EMPTY_CLASS_ARRAY
    }

    private fun pickMethod(name: String, vararg arguments: Any?): Method? {
        val types = inferTypes(*arguments)
        var method: Method? = null
        try {
            method = clazz.getMethod(name, *types)
            if (method == null) {
                method = clazz.getDeclaredMethod(name, *types)
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return method
    }

    override fun tryInvokeMethod(name: String, vararg arguments: Any?): DynamicInvokeResult {
        val method = pickMethod(name, *arguments) ?: return DynamicInvokeResult.notFound()
        try {
            method.isAccessible = false
            val value = if (method.isStatic) {
                method.invoke(null, *arguments)
            } else {
                method.invoke(obj, *arguments)
            }
            return DynamicInvokeResult.found(value)
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return DynamicInvokeResult.found()
    }

    override fun hasMethod(name: String, vararg arguments: Any?): Boolean {
        return pickMethod(name, *arguments) != null
    }

    private fun pickField(name: String): Field? {
        var field: Field? = null
        try {
            field = clazz.getField(name)
            if (field == null) {
                field = clazz.getDeclaredField(name)
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return field
    }

    override fun tryGetProperty(name: String): DynamicInvokeResult {
        val field = pickField(name) ?: return DynamicInvokeResult.notFound()
        try {
            field.isAccessible = true
            val value = if (field.isStatic) {
                field.get(null)
            } else {
                field.get(obj)
            }
            return DynamicInvokeResult.found(value)
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return DynamicInvokeResult.found()
    }

    override fun trySetProperty(name: String, value: Any?): DynamicInvokeResult {
        val field = pickField(name) ?: return DynamicInvokeResult.notFound()
        if (field.isFinal) {
            Log.w(TAG, "Property $name is final!")
            return DynamicInvokeResult.notFound()
        }
        try {
            field.isAccessible = true
            if (field.isStatic) {
                field.set(null, value)
            } else {
                field.set(obj, value)
            }
            return DynamicInvokeResult.found()
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        return DynamicInvokeResult.notFound()
    }

    override fun hasProperty(name: String): Boolean {
        return pickField(name) != null
    }
}

abstract class DynamicObject(protected val obj: Any?, protected val clazz: Class<*>) :
    MethodAccess, PropertyAccess {
    companion object {
        internal const val TAG = "DynamicObject"
    }
}

class DynamicInvokeResult private constructor(private val value: Any? = null) {

    fun getValue(): Any? {
        if (isFound()) {
            return value
        }
        Log.w(DynamicObject.TAG, IllegalStateException("Not Found!"))
        return null
    }

    fun isFound(): Boolean {
        return value != NO_VALUE
    }

    companion object {

        private val NO_VALUE = Any()

        private val NOT_FOUND = DynamicInvokeResult(NO_VALUE)
        private val NULL = DynamicInvokeResult(null)

        fun notFound(): DynamicInvokeResult = NOT_FOUND

        fun found(value: Any?): DynamicInvokeResult = DynamicInvokeResult(value)

        fun found(): DynamicInvokeResult = NULL

        fun <T : Any> DynamicInvokeResult.getTypeValue(kClass: KClass<out T>): T? {
            return getTypeValue(kClass.java)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> DynamicInvokeResult.getTypeValue(tClass: Class<out T>): T? {
            val value = this.getValue()
            val typeValue = value as? T
            if (value != null && typeValue !== value) {
                val name = value.javaClass.name
                val tName = tClass.name
                Log.w(DynamicObject.TAG, TypeCastException("Cannot cast $name to $tName"))
            }
            return typeValue
        }
    }
}

private interface PropertyAccess {
    fun tryGetProperty(name: String): DynamicInvokeResult
    fun trySetProperty(name: String, value: Any?): DynamicInvokeResult
    fun hasProperty(name: String): Boolean
}

private interface MethodAccess {
    fun tryInvokeMethod(name: String, vararg arguments: Any?): DynamicInvokeResult
    fun hasMethod(name: String, vararg arguments: Any?): Boolean
}
