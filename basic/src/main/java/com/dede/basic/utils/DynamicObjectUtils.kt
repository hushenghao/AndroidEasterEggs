@file:JvmName("DynamicObjectUtils")
@file:Suppress("MemberVisibilityCanBePrivate")

package com.dede.basic.utils

import android.util.Log
import com.dede.basic.utils.dynamic.ClassNotFoundDynamicObject
import com.dede.basic.utils.dynamic.DynamicObject
import com.dede.basic.utils.dynamic.DynamicObject.Companion.TAG
import com.dede.basic.utils.dynamic.ReflectDynamicObject
import kotlin.reflect.KClass


object DynamicObjectUtils {

    fun forName(className: String): Class<out Any>? {
        return try {
            Class.forName(className)
        } catch (e: Throwable) {
            Log.w(TAG, "Class not found: $className", e)
            null
        }
    }

    fun asDynamicObject(className: String): DynamicObject {
        val clazz = forName(className) ?: return ClassNotFoundDynamicObject.INSTANCE
        return asDynamicObject(clazz)
    }

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
