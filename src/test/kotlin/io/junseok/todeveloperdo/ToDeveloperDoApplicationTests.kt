package io.junseok.todeveloperdo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [ToDeveloperDoApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ToDeveloperDoApplicationTests {

    @Test
    fun contextLoads() {
    }

}
