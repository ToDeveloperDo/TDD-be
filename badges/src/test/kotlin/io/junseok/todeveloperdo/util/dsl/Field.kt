package io.junseok.todeveloperdo.util.dsl

import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

open class Field(val descriptor: FieldDescriptor) {
    open infix fun means(value: String) = apply { descriptor.description(value) }
}

open class Parameter(val descriptor: ParameterDescriptor){
    open infix fun parameterMeans(value: String): Parameter = apply {
        descriptor.description(value)
    }
    open infix fun requestParamMeans(value: String): Parameter = apply {
        descriptor.description(value)
    }
}

open class Header(val descriptor: HeaderDescriptor){
    open infix fun means(value: String) = apply { descriptor.description(value) }
}
