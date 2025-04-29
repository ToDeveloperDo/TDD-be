package io.junseok.todeveloperdo.util.dsl

import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

infix fun String.typeOf(restDocsField: RestDocsField) = createField(this, restDocsField.type)

infix fun String.arrayTypeOf(restDocsField: RestDocsField) =
    createField("[]$this", restDocsField.type)

private fun createField(
    value: String,
    type: JsonFieldType,
    optional: Boolean = false,
): Field {
    val descriptor = PayloadDocumentation.fieldWithPath(value)
        .type(type)
        .description("")

    if (optional) {
        descriptor.optional()
    }

    return Field(descriptor)
}
