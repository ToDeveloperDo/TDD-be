package io.junseok.todeveloperdo.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.junseok.todeveloperdo.util.ObjectMappers.objectMapper
import org.springframework.test.web.servlet.MvcResult

inline fun <reified T> MvcResult.toResponse(): T {
    return objectMapper.readValue(this.response.contentAsString)
}

inline fun <reified T> T.toRequest(): String {
    return objectMapper.writeValueAsString(this)
}

object ObjectMappers {
    lateinit var objectMapper: ObjectMapper
}