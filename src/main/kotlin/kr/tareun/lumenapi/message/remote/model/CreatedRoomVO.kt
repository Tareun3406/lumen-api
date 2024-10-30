package kr.tareun.lumenapi.message.remote.model

data class CreatedRoomVO(
    val roomId: String,
    val playerCode: String,
    val observerCode: String
)
