package io.junseok.todeveloperdo.client.openai.dto

data class CurriculumRequest(
    val position: String,
    val stack: String,
    val experienceLevel: String,
    val targetPeriod: Int,
){
    companion object{
        fun CurriculumRequest.toPrompt(): String =
            String.format(
                """%s 취준생 개발자\n기술스택: %s
                    경험 수준: %s
                    목표 기간: %d개월
                    이 사람에 맞는 커리큘럼을 주차별 목표, 학습 내용만 포함해서 추천해줘
                    그리고 markdown 형식으로 감싸지 말고 Json형식으로 응답해줘
                    {
                       "curriculum":[
                       {
                          "weekTitle": 생성된 명령어(주차)
                          "objective": 생성된 명령어(목표)
                          "learningContents":[
                          {
                           "content": 생성된 명령어(학습내용)
                          }
                           ]
                         }
                      ]
                    }
                    """.trimIndent(),
                position,
                stack,
                experienceLevel,
                targetPeriod
            )
    }
}
