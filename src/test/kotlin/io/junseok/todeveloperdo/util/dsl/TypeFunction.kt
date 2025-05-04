package io.junseok.todeveloperdo.util.dsl

import io.kotest.assertions.print.printWithType
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Attributes

infix fun String.typeOf(restDocsField: RestDocsField) =
    createField(this, restDocsField.type)

infix fun String.arrayTypeOf(restDocsField: RestDocsField) =
    createField("[]$this", restDocsField.type)

infix fun String.parameterTypeOf(restDocsField: RestDocsField): Parameter =
    createParameter(this, restDocsField.type)

fun createField(
    value: String,
    type: JsonFieldType,
    optional: Boolean = false,
): Field {
    val descriptor = PayloadDocumentation
        .fieldWithPath(value)
        .type(type)
        .description("")
    if (optional) descriptor.optional()
    return Field(descriptor)
}

fun createParameter(
    value: String,
    type: JsonFieldType,
    optional: Boolean = false,
): Parameter {
    val descriptor = RequestDocumentation
        .parameterWithName(value)
        .attributes(
            Attributes.key("type").value(type.toString())
        )
        .description("")
    if (optional) descriptor.optional()
    return Parameter(descriptor)
}