package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.oauth.git.dto.response.PayloadResponse
import org.springframework.stereotype.Component

@Component
class PayloadCreator {
    fun create(payload: Map<String, Any>): PayloadResponse {
        val repository = payload["repository"] as? Map<*, *>
        val newRepoName = repository?.get("name") as? String
        val ownerName = (repository?.get("owner") as? Map<*, *>)?.get("login") as? String
        return PayloadResponse(
            newRepoName = newRepoName!!,
            username = ownerName!!
        )
    }
}