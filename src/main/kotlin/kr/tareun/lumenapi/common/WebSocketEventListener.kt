package kr.tareun.lumenapi.common

import kr.tareun.lumenapi.message.remote.RemoteService
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WebSocketEventListener(val remoteService: RemoteService, val messagingTemplate: SimpMessagingTemplate) {
//    @EventListener
//    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
//        val headerAccessor = StompHeaderAccessor.wrap(event.message)
//        val sessionId = headerAccessor.sessionId
//        println("Connected with session ID: $sessionId")
//    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = headerAccessor.sessionId ?: return

        val memberList = remoteService.disconnect(sessionId) ?: return

        val destination = "/topic/remote/${memberList.roomId}/memberList"
        messagingTemplate.convertAndSend(destination, memberList)
    }
}