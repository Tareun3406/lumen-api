package kr.tareun.lumenapi.common

import kr.tareun.lumenapi.message.remote.RemoteService
import kr.tareun.lumenapi.message.remote.model.NotificationMessage
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WebSocketEventListener(val remoteService: RemoteService, val messagingTemplate: SimpMessagingTemplate) {
    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = headerAccessor.sessionId ?: return

        val userInfo = remoteService.getUserInfo(sessionId) ?: return
        val memberList = remoteService.handleDisconnectedSessionList(userInfo) ?: return

        if (!memberList.existHost() && userInfo.username == memberList.hostName) {
            val destination = "/topic/remote/${memberList.roomId}/notification"
            val body = NotificationMessage(NotificationMessage.Status.WARNING, "호스트와의 연결이 끊어졌습니다. 방은 임시로 유지됩니다.")
            messagingTemplate.convertAndSend(destination, body)
        }

        val destination = "/topic/remote/${memberList.roomId}/memberList"
        messagingTemplate.convertAndSend(destination, memberList)
    }
}