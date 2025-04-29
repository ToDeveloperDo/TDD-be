package io.junseok.todeveloperdo.util.dsl

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

open class Field(val descriptor: FieldDescriptor) {
    open infix fun means(value: String) = apply { descriptor.description(value) }
}

open class Parameter(val descriptor: ParameterDescriptor)

infix fun String.parameterMeans(value: String): Parameter =
    Parameter(parameterWithName(this).description(value))

infix fun String.requestParamMeans(value: String): Parameter =
    Parameter(parameterWithName(this).description(value))