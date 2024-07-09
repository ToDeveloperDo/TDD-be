package io.junseok.todeveloperdo.oauth.git

import feign.Response
import feign.codec.Decoder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type

class GitHubTokenDecoder : Decoder {
    override fun decode(response: Response, type: Type): Any {
        val reader = BufferedReader(InputStreamReader(response.body().asInputStream()))
        val result = reader.lines().toArray().joinToString("")
        reader.close()

        val map = result.split("&")
            .map { it.split("=") }
            .associate { it[0] to it[1] }

        return map
    }
}