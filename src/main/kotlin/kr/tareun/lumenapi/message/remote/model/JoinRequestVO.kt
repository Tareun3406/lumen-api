package kr.tareun.lumenapi.message.remote.model

data class JoinRequestVO(
    val name: String,
    val inviteCode: String,
    val isReconnect: Boolean
)
