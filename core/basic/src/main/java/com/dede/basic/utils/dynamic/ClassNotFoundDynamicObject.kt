package com.dede.basic.utils.dynamic


internal class ClassNotFoundDynamicObject private constructor() :
    DynamicObject(null, ClassNotFoundDynamicObject::class.java) {

    companion object {
        val INSTANCE = ClassNotFoundDynamicObject()
    }

    override fun getProperty(name: String): DynamicResult {
        return DynamicResult.notFound()
    }

    override fun setProperty(name: String, value: Any?): DynamicResult {
        return DynamicResult.notFound()
    }

    override fun invokeMethod(
        name: String,
        argumentsType: Array<Class<out Any>>,
        arguments: Array<out Any?>
    ): DynamicResult {
        return DynamicResult.notFound()
    }

    override fun newInstance(
        argumentsType: Array<Class<out Any>>,
        arguments: Array<out Any?>
    ): DynamicResult {
        return DynamicResult.notFound()
    }
}
