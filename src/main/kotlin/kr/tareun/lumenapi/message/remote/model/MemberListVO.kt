package kr.tareun.lumenapi.message.remote.model

data class MemberListVO(
    val hostName: String,
    val playerList: List<String>,
    val observerList: List<String>,
    val roomId: String
) {
    fun existHost() = playerList.contains(hostName) || observerList.contains(hostName)
}
