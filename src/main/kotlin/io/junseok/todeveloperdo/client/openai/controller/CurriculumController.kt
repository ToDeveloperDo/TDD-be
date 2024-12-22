package io.junseok.todeveloperdo.client.openai.controller

import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequests
import io.junseok.todeveloperdo.client.openai.dto.response.CurriculumResponse
import io.junseok.todeveloperdo.domains.curriculum.service.CurriculumProcessor
import io.junseok.todeveloperdo.domains.curriculum.service.CurriculumService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/curriculum")
@CrossOrigin
class CurriculumController(
    private val curriculumProcessor: CurriculumProcessor,
    private val curriculumService: CurriculumService
) {
    @PostMapping("/recommend")
    fun createCurriculum(
        @RequestBody curriculumRequest: CurriculumRequest,
    ): ResponseEntity<String> =
        ResponseEntity.ok(
            curriculumProcessor.recommendCurriculum(curriculumRequest)
        )

    @PostMapping("/save")
    fun registerCurriculum(
        @RequestBody registerRequests: RegisterRequests,
        principal: Principal,
    ): ResponseEntity<Void> {
        curriculumService.saveCurriculum(registerRequests,principal.name)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{planId}")
    fun getCurriculum(@PathVariable planId: Long):ResponseEntity<List<CurriculumResponse>>{
        return ResponseEntity.ok(curriculumService.find(planId))
    }
}
