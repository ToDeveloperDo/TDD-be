package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class RepoValidator {
    fun isValid(repoName:String){
        existGap(repoName)
        existKorean(repoName)
    }

    fun existGap(repoName: String){
        if (repoName.contains(" "))
            throw ToDeveloperDoException{ErrorCode.INVALID_REPO_NAME_GAP}
    }

    fun existKorean(repoName: String){
        val pattern: Pattern = Pattern.compile("[가-힣ㄱ-ㅎㅏ-ㅣ]")
        if(pattern.matcher(repoName).find())
            throw ToDeveloperDoException{ErrorCode.INVALID_REPO_NAME_KOREAN}
    }
}