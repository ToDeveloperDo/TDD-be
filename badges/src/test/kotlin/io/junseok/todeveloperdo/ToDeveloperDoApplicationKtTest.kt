package io.junseok.todeveloperdo

import io.kotest.core.spec.style.FunSpec
import io.mockk.unmockkAll

class ToDeveloperDoApplicationMainTest : FunSpec({

    test("main() 함수가 테스트 환경에서 예외 없이 실행된다") {
        System.setProperty("spring.profiles.active", "test")
        main(arrayOf())
    }

    test("ToDeveloperDoApplication 클래스가 로드될 수 있어야 한다") {
        ToDeveloperDoApplication()
    }

    afterTest {
        unmockkAll()
    }
})