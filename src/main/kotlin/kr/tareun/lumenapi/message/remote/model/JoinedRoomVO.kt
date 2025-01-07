package kr.tareun.lumenapi.message.remote.model

data class JoinedRoomVO(
    val assignedName: String,
    val roomId: String,
    val hostName: String,
    val playerList: List<String>,
    val observerList: List<String>,
    val board: BoardVO,
    val hasControl: Boolean,
    val timer: RoomTimerInfo
) {
    companion object {
        fun from(assignedName: String, room: RemoteRoomVO, isPlayer: Boolean): JoinedRoomVO {
            return JoinedRoomVO(
                assignedName = assignedName,
                roomId = room.roomId,
                hostName = room.hostName,
                playerList = room.playerList.map { it.name },
                observerList = room.observerList.map { it.name },
                board = room.board,
                hasControl = isPlayer,
                timer = room.timer
            )
        }
    }
}
