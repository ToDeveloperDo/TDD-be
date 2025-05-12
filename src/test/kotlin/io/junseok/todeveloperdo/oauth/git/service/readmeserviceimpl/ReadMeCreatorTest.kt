package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.exception.ErrorCode.NOT_EXIST_BRANCH
import io.junseok.todeveloperdo.exception.ErrorCode.NOT_EXIST_MEMBER
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.git.client.GtiHubReadMeClient
import io.junseok.todeveloperdo.oauth.git.dto.request.FileCommitRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.FileCommitRequest.Companion.fileCommitRequestInit
import io.junseok.todeveloperdo.oauth.git.dto.response.*
import io.junseok.todeveloperdo.oauth.git.service.GitHubService
import io.junseok.todeveloperdo.util.StubDateProvider
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.time.LocalDate

class ReadMeCreatorTest : FunSpec({
    val gitHubReadMeClient = mockk<GtiHubReadMeClient>()
    val today = LocalDate.of(2025, 5, 13)
    val timeProvider = StubDateProvider(today)
    val readMeCreator = ReadMeCreator(gitHubReadMeClient, timeProvider)
    beforeTest {
        mockkObject(FileCommitRequest.Companion)
    }
    test("README 콘텐츠에 현재 날짜와 요일이 포함되어야 한다") {
        val todoListContent =
            "- TDD 연습하기\n  - Memo: Kotest 사용\n  - Tag: #test\n  - Deadline: 2025-05-13"
        val result = readMeCreator.readMeContentCreate(todoListContent)

        result shouldContain "📝2025-05-13(화) / TODOLIST📝"
        result shouldContain "- TDD 연습하기"
        result shouldContain "Memo: Kotest 사용"
    }

    test("브랜치가 존재한다면 깃허브 README.md가 정상적으로 작성되어야한다.") {
        val token = "token"
        val owner = "owner"
        val repo = "repo"
        val content = "content"
        val gitHubBranchResponses = listOf(createGitBranchResponse("main"))
        val gitBranchResponse = createGitBranchResponse("main")
        val gitHubContent = createGitHubContent()
        val fileCommitRequest = createFileCommitRequest()
        val fileCommitResponse = createFileCommitResponse()

        every { gitHubReadMeClient.getBranches(token, owner, repo) } returns gitHubBranchResponses
        every {
            gitHubReadMeClient.getFile(
                token,
                owner,
                repo,
                GitHubService.PATH
            )
        } returns gitHubContent

        every {
            fileCommitRequestInit(
                owner,
                content,
                gitBranchResponse.name,
                gitHubContent.sha
            )
        } returns fileCommitRequest

        every {
            gitHubReadMeClient.createOrUpdateFile(
                token,
                owner,
                repo,
                path = "README.md",
                fileCommitRequest
            )
        } returns fileCommitResponse

        readMeCreator.createReadMe(token, owner, repo, content)
        verify(exactly = 1) {
            gitHubReadMeClient.getBranches(token, owner, repo)
        }

        verify(exactly = 1) {
            gitHubReadMeClient.getFile(token, owner, repo, GitHubService.PATH)
        }

        verify(exactly = 1) {
            fileCommitRequestInit(owner, content, gitBranchResponse.name, gitHubContent.sha)
        }

        verify(exactly = 1) {
            gitHubReadMeClient.createOrUpdateFile(
                token = token,
                owner = owner,
                repo = repo,
                path = "README.md",
                body = fileCommitRequest
            )
        }
    }

    test("master 브랜치가 존재한다면 README.md가 정상적으로 생성되어야 한다") {
        val token = "token"
        val owner = "owner"
        val repo = "repo"
        val content = "content"
        val masterBranch = GitHubBranchResponse(name = "master", commit = createGitCommit())
        val branches = listOf(masterBranch)
        val file = createGitHubContent()
        val fileCommitRequest = createFileCommitRequest()
        val fileCommitResponse = createFileCommitResponse()

        every { gitHubReadMeClient.getBranches(token, owner, repo) } returns branches
        every { gitHubReadMeClient.getFile(token, owner, repo, GitHubService.PATH) } returns file
        every {
            fileCommitRequestInit(owner, content, masterBranch.name, file.sha)
        } returns fileCommitRequest
        every {
            gitHubReadMeClient.createOrUpdateFile(token, owner, repo, "README.md", fileCommitRequest)
        } returns fileCommitResponse

        readMeCreator.createReadMe(token, owner, repo, content)

        verify { gitHubReadMeClient.getBranches(token, owner, repo) }
        verify { gitHubReadMeClient.getFile(token, owner, repo, GitHubService.PATH) }
        verify { fileCommitRequestInit(owner, content, masterBranch.name, file.sha) }
        verify { gitHubReadMeClient.createOrUpdateFile(token, owner, repo, "README.md", fileCommitRequest) }
    }


    test("기본 브랜치(main/master)가 없다면 예외가 발생해야 한다") {
        val token = "token"
        val owner = "owner"
        val repo = "repo"
        val branches = listOf(GitHubBranchResponse(name = "develop", commit = createGitCommit())) // main/master 없음

        every { gitHubReadMeClient.getBranches(token, owner, repo) } returns branches

        throwsWith<ToDeveloperDoException>(
            {
                readMeCreator.createReadMe(token, owner, repo, "content")
            },
            {
                ex -> ex.errorCode shouldBe NOT_EXIST_BRANCH
            }
        )
    }

})

fun createGitBranchResponse(branchName: String) = GitHubBranchResponse(
    name = branchName,
    commit = createGitCommit()
)

fun createGitCommit() = GitHubCommit("sha")

fun createGitHubLink() = GitHubLink(
    self = "self",
    git = "git",
    html = "html"
)

fun createGitHubContent() = GItHubContent(
    name = "name",
    path = "path",
    sha = "sha",
    size = 123,
    url = "url",
    htmlUrl = "htmlUrl",
    gitUrl = "gitUrl",
    downloadUrl = "downloadUrl",
    type = "type",
    links = createGitHubLink()
)

fun createFileCommitRequest() = FileCommitRequest(
    message = "message",
    content = "content",
    branch = "branch",
    sha = "sha"
)

fun createFileCommitResponse() = FileCommitResponse(
    content = createGitHubContent(),
    commit = createGitCommit()
)
