@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.dede.basic.utils.dynamic

import android.util.Log
import kotlin.reflect.KClass

abstract class DynamicObject(protected val obj: Any?, protected val clazz: Class<*>) :
    MethodAccess, PropertyAccess, ConstructorAccess {
    companion object {
        internal const val TAG = "DynamicObject"
    }
}

internal interface PropertyAccess {
    fun getProperty(name: String): DynamicResult
    fun setProperty(name: String, value: Any?): DynamicResult
}

internal interface MethodAccess {
    fun invokeMethod(
        name: String,
        argumentsType: Array<Class<*>> = emptyArray(),
        arguments: Array<out Any?> = emptyArray()
    ): DynamicResult
}

internal interface ConstructorAccess {
    fun newInstance(
        argumentsType: Array<Class<*>> = emptyArray(),
        arguments: Array<out Any?> = emptyArray()
    ): DynamicResult
}

class DynamicResult private constructor(
    private val value: Any? = null,
    val error: Throwable? = null,
) {

    fun getValue(): Any? {
        if (!isNotFound()) {
            return value
        }
        Log.w(DynamicObject.TAG, IllegalStateException("Not Found!"))
        return null
    }

    fun isNotFound(): Boolean {
        return value == NOT_FOUND_VALUE
    }

    fun isError(): Boolean {
        return error != null
    }

    companion object {

        private val NOT_FOUND_VALUE = Any()
        private val ERROR_VALUE = Any()

        private val NOT_FOUND = DynamicResult(NOT_FOUND_VALUE)
        private val ERROR = DynamicResult(ERROR_VALUE)

        fun notFound(): DynamicResult = NOT_FOUND

        fun error(e: Throwable): DynamicResult = DynamicResult(error = e)

        fun success(value: Any? = null): DynamicResult = DynamicResult(value)

        fun <T : Any> DynamicResult.getTypeValue(kClass: KClass<out T>): T? {
            return getTypeValue(kClass.java)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> DynamicResult.getTypeValue(tClass: Class<out T>): T? {
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