package io.junseok.todeveloperdo.client.openai.controller

import io.junseok.todeveloperdo.client.openai.dto.CurriculumRequest
import io.junseok.todeveloperdo.client.openai.service.CurriculumProcessor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin
class CurriculumController(
    private val curriculumProcessor: CurriculumProcessor,
) {
    @PostMapping("/curriculum")
    fun slangTranslator(
        @RequestBody curriculumRequest: CurriculumRequest,
    ): ResponseEntity<String> =
        ResponseEntity.ok(curriculumProcessor.recommendCurriculum(curriculumRequest))
}
