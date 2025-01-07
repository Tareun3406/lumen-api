package kr.tareun.lumenapi.message.remote.model

data class TimerRequest(
    val timerType: TimerType,
    val statusType: TimerStatusType
)