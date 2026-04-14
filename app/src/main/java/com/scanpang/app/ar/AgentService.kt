package com.scanpang.app.ar

import kotlinx.coroutines.delay

/**
 * 실제 LLM/에이전트 API 연동 시 구현체를 교체하면 됩니다.
 */
interface AgentService {
    suspend fun sendMessage(text: String): String
    suspend fun sendVoice(audioData: ByteArray): String
}

class DummyAgentService : AgentService {
    override suspend fun sendMessage(text: String): String {
        delay(250)
        return "안녕하세요! 무엇을 도와드릴까요?"
    }

    override suspend fun sendVoice(audioData: ByteArray): String {
        delay(250)
        return "음성 입력을 받았습니다. (더미: ${audioData.size} bytes)"
    }
}
