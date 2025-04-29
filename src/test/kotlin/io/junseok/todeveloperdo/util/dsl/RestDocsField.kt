package io.junseok.todeveloperdo.util.dsl

import org.springframework.restdocs.payload.JsonFieldType

sealed class RestDocsField(val type: JsonFieldType)

object NUMBER : RestDocsField(JsonFieldType.NUMBER)
object STRING: RestDocsField(JsonFieldType.STRING)
object BOOLEAN : RestDocsField(JsonFieldType.BOOLEAN)
object DATE : RestDocsField(JsonFieldType.STRING)
object OBJECT : RestDocsField(JsonFieldType.OBJECT)
object NULL : RestDocsField(JsonFieldType.NULL)
object ANY : RestDocsField(JsonFieldType.VARIES)
object ARRAY : RestDocsField(JsonFieldType.ARRAY)