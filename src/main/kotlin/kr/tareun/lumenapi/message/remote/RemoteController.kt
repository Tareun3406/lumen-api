package kr.tareun.lumenapi.message.remote

import kr.tareun.lumenapi.message.remote.model.*
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller

private val logger = LoggerFactory.getLogger(RemoteController::class.java)

@Controller
@MessageMapping("/remote") // /app/remote
class RemoteController(
    val remoteService: RemoteService,
    val messagingTemplate: SimpMessagingTemplate
) {
    @MessageMapping("/create")
    @SendToUser("/queue/created")
    fun create(request: CreateRequestVO): CreatedRoomVO {
        val room = remoteService.createRoom(request);
        return room
    }

    @MessageMapping("/joinAsCode")
    @SendToUser("/queue/joined")
    fun joinUser(@Payload joinRequest: JoinRequestVO): JoinedRoomVO? {
        val roomInfo = remoteService.findRoomAsInviteCode(joinRequest.name, joinRequest.inviteCode) ?: return null;

        val memberList = MemberListVO(roomInfo.playerList, roomInfo.observerList)

        val destination = "/topic/remote/${roomInfo.roomId}/memberList"
        messagingTemplate.convertAndSend(destination, memberList)

        return roomInfo
    }

    @MessageMapping("/updateBoard")
    fun updateBoard(@Payload board: BoardVO, @Header("roomId") roomId: String) {
        val result = remoteService.updateBoard(board, roomId)

        val destination = "/topic/remote/${roomId}/updateBoard"
        messagingTemplate.convertAndSend(destination, result)
    }

    @MessageMapping("/disconnect")
    fun disconnect(@Payload userName: String, @Header("roomId") roomId: String) {
        val memberList = remoteService.disconnect(userName, roomId)

        if (memberList.playerList.isEmpty()) {
            val destination = "/topic/remote/${roomId}/disconnect"
            messagingTemplate.convertAndSend(destination, "")
        }

        val destination = "/topic/remote/${roomId}/memberList"
        messagingTemplate.convertAndSend(destination, memberList)
    }

    @MessageMapping("/timer")
    fun triggerTimer(@Header("roomId") roomId: String, timerOn: Boolean) {
        val destination = "/topic/remote/${roomId}/timer"
        messagingTemplate.convertAndSend(destination, timerOn)
    }
}