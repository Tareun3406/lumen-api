package kr.tareun.lumenapi.message.remote.model

data class RemoteRoomVO(
    val roomId: String,
    val playerList: MutableList<String>,
    val observerList: MutableList<String>,
    val playerInviteCode: String,
    val observerInviteCode: String,
    var board: Any
)
