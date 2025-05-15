import io.junseok.todeveloperdo.auth.config.CorsConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

class CorsConfigTest : FunSpec({

    val corsConfig = CorsConfig()

    test("특정 경로에 CORS 설정이 등록되어 있어야 한다") {
        val filter = corsConfig.corsFilter()

        val configSourceField = filter.javaClass.getDeclaredField("configSource")
        configSourceField.isAccessible = true
        val configSource = configSourceField.get(filter) as UrlBasedCorsConfigurationSource

        val testPaths = listOf(
            "/git/login/abc",
            "/login/oauth2/code/github/*",
            "/api/example"
        )

        for (path in testPaths) {
            val request = MockHttpServletRequest().apply {
                method = "GET"
                requestURI = path
            }

            val config = configSource.getCorsConfiguration(request)
            config shouldNotBe null
            config!!.allowCredentials shouldBe true
            config.allowedOriginPatterns shouldBe listOf("*")
            config.allowedHeaders shouldBe listOf("*")
            config.allowedMethods shouldBe listOf("*")
            config.allowedOrigins shouldBe listOf("http://localhost:8080")
        }
    }
})
