package kr.tareun.lumenapi.message.scheduler

import kr.tareun.lumenapi.message.remote.RemoteService
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(UnusedRemoteCleaner::class.java)

@Component
class UnusedRemoteCleaner(val remoteService: RemoteService, val messagingTemplate: SimpMessagingTemplate) {
    @Scheduled(cron = "0 */10 * * * ?", zone = "Asia/Seoul")
    fun cleaningRemote() {
        val roomList = remoteService.getRoomIdSet()
        logger.info("remote room counts: ${roomList.size}")

        val deleteTargetList = roomList.filter { it.needDelete(45) }
        logger.info("deleting room counts: ${deleteTargetList.size}")

        deleteTargetList.forEach {
            val destination = "/topic/remote/${it.roomId}/disconnect"
            messagingTemplate.convertAndSend(destination, "오랜시간 해당 방의 조작이 없어 연결이 종료되었습니다.")
            remoteService.cleaningRoom(it.roomId)
        }
    }
}