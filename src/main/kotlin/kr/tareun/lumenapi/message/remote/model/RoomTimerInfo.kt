package kr.tareun.lumenapi.message.remote.model

data class RoomTimerInfo(
    var phaseGetTimerEndTime : TimerVO = TimerVO(10),
    var globalTimerEndTime: TimerVO = TimerVO(60 * 30),
)
