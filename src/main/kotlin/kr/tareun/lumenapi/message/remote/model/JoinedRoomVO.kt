package kr.tareun.lumenapi.message.remote.model

data class JoinedRoomVO(
    val assignedName: String,
    val roomId: String,
    val hostName: String,
    val playerList: List<String>,
    val observerList: List<String>,
    val board: BoardVO,
    val isPlayer: Boolean
)
