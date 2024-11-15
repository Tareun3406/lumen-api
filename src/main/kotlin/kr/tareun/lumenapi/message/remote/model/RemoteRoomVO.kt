package kr.tareun.lumenapi.message.remote.model

import java.time.LocalDateTime

data class RemoteRoomVO(
    val roomId: String,
    val hostName: String,
    val playerList: MutableList<String>,
    val observerList: MutableList<String>,
    val playerInviteCode: String,
    val observerInviteCode: String,
    var board: BoardVO,
    var lastUpdateTime: LocalDateTime
) {
    fun needDelete(thresholdMinutes: Long): Boolean {
        val thresholdTime = LocalDateTime.now().minusMinutes(thresholdMinutes)
        return (playerList.isEmpty() && observerList.isEmpty()) || lastUpdateTime.isBefore(thresholdTime)
    }
}
