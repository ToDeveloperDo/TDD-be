package io.junseok.todeveloperdo.global

import io.junseok.todeveloperdo.client.openai.config.OpenChatAiConfig.Companion.SECRET_KEY_PREFIX

fun String.generateSecretToken()=SECRET_KEY_PREFIX+this