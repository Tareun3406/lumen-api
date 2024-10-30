package kr.tareun.lumenapi.message.remote

import kr.tareun.lumenapi.message.remote.model.CreatedRoomVO
import kr.tareun.lumenapi.message.remote.model.JoinRequestVO
import kr.tareun.lumenapi.message.remote.model.JoinedRoomVO
import kr.tareun.lumenapi.message.remote.model.MemberListVO
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("/remote") // /app/remote
class RemoteController(
    val remoteService: RemoteService,
    val messagingTemplate: SimpMessagingTemplate
) {
    @MessageMapping("/create")
    @SendToUser("/queue/created")
    fun create(board: Map<String, Any>): CreatedRoomVO {
        return remoteService.createRoom(board)
    }

    @MessageMapping("/joinAsCode")
    @SendToUser("/queue/joined")
    fun joinUser(@Payload joinRequest: JoinRequestVO): JoinedRoomVO {
        val roomInfo = remoteService.findRoomAsInviteCode(joinRequest.name, joinRequest.inviteCode)
        val memberList = MemberListVO(roomInfo.playerList, roomInfo.observerList)

        val destination = "/topic/remote/${roomInfo.roomId}/memberList"
        messagingTemplate.convertAndSend(destination, memberList)
        return roomInfo
    }

    @MessageMapping("/updateBoard")
    fun updateBoard(@Payload board:Any, headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.getFirstNativeHeader("roomId") ?: ""
        val result = remoteService.updateBoard(board, roomId)

        val destination = "/topic/remote/${roomId}/updateBoard"
        messagingTemplate.convertAndSend(destination, result)
    }

    @MessageMapping("/disconnect")
    fun disconnect(@Payload userName: String, headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.getFirstNativeHeader("roomId") ?: ""
        val memberList = remoteService.disconnect(userName, roomId)

        val destination = "/topic/remote/${roomId}/memberList"
        messagingTemplate.convertAndSend(destination, memberList)
    }
}