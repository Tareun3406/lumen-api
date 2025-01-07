package kr.tareun.lumenapi.message.remote.model


import java.time.Duration
import java.time.LocalDateTime


data class TimerVO(
    var initTimeSec: Long,
    var endTime: LocalDateTime? = null,
    var pausedRemainMillis: Long? = null,
    var statusType: TimerStatusType = TimerStatusType.READY
) {
    fun setTimeState(statusType: TimerStatusType) {
        this.statusType = statusType
        when(statusType) {
            TimerStatusType.READY -> {
                this.endTime = null
                this.pausedRemainMillis = null
            }
            TimerStatusType.RUNNING -> {
                this.endTime = LocalDateTime.now().plusSeconds(this.initTimeSec)
                this.pausedRemainMillis = null
            }
            TimerStatusType.PAUSE -> {
                this.pausedRemainMillis = Duration.between(LocalDateTime.now(), this.endTime).toMillis()
                this.endTime = null
            }
        }
    }
}