package io.junseok.todeveloperdo.oauth.git.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubResponse
import io.junseok.todeveloperdo.oauth.git.service.GitHubService
import io.junseok.todeveloperdo.oauth.git.service.createGitHubRequest
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.createGitHubResponse
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.*
import io.junseok.todeveloperdo.util.setAuthorization
import io.junseok.todeveloperdo.util.toRequest
import io.junseok.todeveloperdo.util.toResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@WebMvcTest(GitHubController::class)
@AutoConfigureRestDocs
class GitHubControllerTest : BehaviorSpec({
    val gitHubService = mockk<GitHubService>()
    val gitHubController = GitHubController(gitHubService)

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(gitHubController)
        .apply<StandaloneMockMvcBuilder>(
            MockMvcRestDocumentation.documentationConfiguration(
                restDocumentation
            )
        )
        .build()

    beforeSpec {
        ObjectMappers.objectMapper = ObjectMapper()
            .registerModules(KotlinModule.Builder().build())
        restDocumentation.beforeTest(javaClass, "GitHubController")

    }
    afterSpec {
        restDocumentation.afterTest()
    }

    Given("깃허브 레포를 등록할 때") {
        val gitHubRequest = createGitHubRequest()
        val gitHubResponse = createGitHubResponse()
        every { gitHubService.createRepository(gitHubRequest, any()) } returns gitHubResponse

        When("POST /api/github/create/repo를 호출하면") {
            Then("깃허브 정보를 정상적으로 반환한다.") {
                val mvcResult = mockMvc.perform(
                    post(GITHUB_PATH + "create/repo")
                        .setAuthorization()
                        .content(gitHubRequest.toRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andDo(
                        document(
                            "github-repo-create",
                            authorizationHeader(),
                            requestFields(
                                "repoName" typeOf STRING means "레포 이름",
                                "description" typeOf STRING means "레포 설명 글",
                                "isPrivate" typeOf BOOLEAN means "레포 공개 유무(true면 비공개)"
                            ),
                            responseFields(
                                "id" typeOf NUMBER means "레포 고유 ID",
                                "name" typeOf STRING means "레포 이름",
                                "full_name" typeOf STRING means "owner/repo-name 형식의 전체 이름",
                                "owner" typeOf OBJECT means "Owner객체",
                                "owner.login" typeOf STRING means "깃허브 사용자"
                            )
                        )
                    ).andReturn()

                mvcResult.toResponse<GitHubResponse>() shouldBe gitHubResponse
            }
        }
    }

    Given("깃허브가 연동되어있는지 확인할 때") {
        every { gitHubService.checkGitLink(any()) } just runs
        When("GET /check를 호출하면") {
            Then("StatusCode 200이 반환되어야한다.") {
                mockMvc.perform(
                    get(GITHUB_PATH + "check")
                        .setAuthorization()
                ).andExpect(status().isOk)
                    .andDo(
                        document(
                            "check-git-link",
                            authorizationHeader()
                        )
                    )
                verify(exactly = 1) { gitHubService.checkGitLink("username") }
            }
        }
    }

    Given("깃허브 WebHook을 설정할 때") {
        val payload = mapOf("key" to "value")
        val event = "Github-event"
        every { gitHubService.webhookProcess(payload, event) } just runs
        When("POST /webhook을 호출하면") {
            Then("statusCode 200반환되고, webhook설정이 완료되어야한다.") {
                mockMvc.perform(
                    post(GITHUB_PATH + "webhook")
                        .header("X-GitHub-Event", event)
                        .content(payload.toRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andDo(
                        document(
                            "setting-git-webhook",
                            requestFields(
                                "key" typeOf STRING means "페이로드 내 key 값"
                            ),
                            requestHeaders(
                                "X-GitHub-Event" headerTypeOf STRING means "Github 이벤트 종류"
                            )
                        )
                    )
                verify(exactly = 1) { gitHubService.webhookProcess(payload, event) }
            }
        }
    }

}) {
    companion object {
        const val GITHUB_PATH = "/api/github/"
    }
}
