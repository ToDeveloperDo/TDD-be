package io.junseok.todeveloperdo.auth.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import lombok.Data
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ExceptionHandlerFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ToDeveloperDoException) {
            setErrorResponse(response,e.errorCode())
        }
    }

    private fun setErrorResponse(
        response: HttpServletResponse,
        errorCode: ErrorCode
    ) {
        val objectMapper = ObjectMapper()
        response.status = errorCode.status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = IN_CODING_TYPE
        val errorResponse = ErrorResponse(
            errorCode.status,
            errorCode.message
        )
        try {
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Data
    class ErrorResponse(val status: Int, val message: String)

    companion object {
        private const val IN_CODING_TYPE = "UTF-8"
    }
}