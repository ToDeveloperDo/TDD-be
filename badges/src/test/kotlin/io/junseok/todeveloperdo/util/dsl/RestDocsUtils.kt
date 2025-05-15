package io.junseok.todeveloperdo.util.dsl

import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.headers.RequestHeadersSnippet
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation

fun requestFields(vararg fields: Field) =
    PayloadDocumentation.requestFields(fields.map { it.descriptor })

fun responseFields(vararg fields: Field) =
    PayloadDocumentation.responseFields(fields.map { it.descriptor })

fun pathParameters(vararg fields: Parameter) =
    RequestDocumentation.pathParameters(fields.map { it.descriptor })

fun requestParameters(vararg fields: Parameter) =
    RequestDocumentation.requestParameters(fields.map { it.descriptor })

fun requestHeaders(vararg fields: Header) =
    HeaderDocumentation.requestHeaders(fields.map { it.descriptor })
fun authorizationHeader(required: Boolean = true): RequestHeadersSnippet {
    val descriptor = HeaderDocumentation.headerWithName("Authorization")
        .description("Bearer Token")

    if (!required) {
        descriptor.optional()
    }

    return HeaderDocumentation.requestHeaders(descriptor)
}
