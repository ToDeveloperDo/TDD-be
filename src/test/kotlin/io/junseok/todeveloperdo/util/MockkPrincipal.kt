package io.junseok.todeveloperdo.util

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import java.security.Principal

class MockkPrincipal(private val username: String) : Principal {
    override fun getName() = username
}

fun MockHttpServletRequestBuilder.setAuthorization(
    userName: String = "username",
): MockHttpServletRequestBuilder {
    return this
        .header("Authorization", "Bearer test-token")
        .principal(MockkPrincipal(userName))
}