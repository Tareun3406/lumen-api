package kr.tareun.lumenapi.message.remote

import kr.tareun.lumenapi.message.remote.model.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

private val logger = LoggerFactory.getLogger(RemoteController::class.java)

@Service
class RemoteService {

    // todo 맵 구조 개선
    private val roomJoinCodeMap = mutableMapOf<String, RemoteRoomVO>()
    private val playerCodeMap = mutableMapOf<String, Boolean>()
    private val roomIdMap = mutableMapOf<String, RemoteRoomVO>()

    private val sessionIdUserInfoMap = mutableMapOf<String, SessionUserInfoVO>()

    private val inviteCodeLength = 10;

    fun createRoom(request: CreateRequestVO): CreatedRoomVO {
        val playerCode = generateInviteCode(inviteCodeLength, true)
        val observerCode = generateInviteCode(inviteCodeLength, false)
        val roomId = UUID.randomUUID().toString()
        val remoteRoom = RemoteRoomVO(roomId, request.hostName, mutableListOf(), mutableListOf(), playerCode, observerCode, request.board, LocalDateTime.now())
        roomJoinCodeMap[playerCode] = remoteRoom
        roomJoinCodeMap[observerCode] = remoteRoom
        roomIdMap[roomId] = remoteRoom

        val result = CreatedRoomVO(roomId, playerCode, observerCode)
        logger.debug("created: {}", result)
        return result
    }

    fun findRoomAsInviteCode(joinRequest: JoinRequestVO, sessionId: String): JoinedRoomVO? {
        val isPlayer = playerCodeMap[joinRequest.inviteCode] ?: return null // todo 예외처리. 이하 같음
        val room = roomJoinCodeMap[joinRequest.inviteCode] ?: return null

        val playerList = room.playerList
        val observerList = room.observerList
        var hostName = room.hostName

        var assignedName = joinRequest.name
        var dubNumber = 0
        if (playerList.contains(assignedName) || observerList.contains(assignedName)) {
            while (playerList.contains(assignedName + "_" + dubNumber) && observerList.contains(assignedName + "_" + dubNumber)) {
                dubNumber++
            }
            assignedName = assignedName + "_" + dubNumber
        }

        if (isPlayer) playerList.add(assignedName)
        else observerList.add(assignedName)
        sessionIdUserInfoMap[sessionId] = SessionUserInfoVO(assignedName, room.roomId)

        return JoinedRoomVO(assignedName, room.roomId, hostName, playerList, observerList, room.board, isPlayer)
    }

    fun updateBoard(board: BoardVO, roomId: String): BoardVO {
        val room = roomIdMap[roomId] ?: return board;
        room.board = board
        room.lastUpdateTime = LocalDateTime.now()
        return room.board;
    }

    fun disconnect(userName: String, roomId: String, sessionId: String): MemberListVO {
        val room = roomIdMap[roomId] ?: return MemberListVO(listOf(), listOf(), "")
        room.playerList.remove(userName)
        room.observerList.remove(userName)
        sessionIdUserInfoMap.remove(sessionId)

        if (room.hostName == userName) {
            room.playerList.clear()
            room.observerList.clear()
            cleaningRoom(roomId)
        }

        return MemberListVO(room.playerList, room.observerList, roomId)
    }

    fun handleConnectionList(sessionId: String): MemberListVO? {
        val userInfo = sessionIdUserInfoMap[sessionId] ?: return null

        val room = roomIdMap[userInfo.joinedRoomId] ?: return MemberListVO(listOf(), listOf(), "")
        room.playerList.remove(userInfo.username)
        room.observerList.remove(userInfo.username)
        sessionIdUserInfoMap.remove(sessionId)

        return MemberListVO(room.playerList, room.observerList, room.roomId)
    }

    fun cleaningRoom(roomId: String) {
        val room = roomIdMap[roomId] ?: return
        roomJoinCodeMap.remove(room.playerInviteCode)
        roomJoinCodeMap.remove(room.observerInviteCode)
        playerCodeMap.remove(room.playerInviteCode)
        playerCodeMap.remove(room.observerInviteCode)
        roomIdMap.remove(roomId)
    }

    fun getRoomIdSet(): MutableCollection<RemoteRoomVO> {
        return roomIdMap.values
    }

    private fun generateInviteCode(length: Int, isPlayerCode: Boolean): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        var inviteCode: String
        do {
            inviteCode = (1..length)
                .map { chars.random() }
                .joinToString("")
        } while (playerCodeMap.containsKey(inviteCode))

        playerCodeMap[inviteCode] = isPlayerCode
        return inviteCode
    }
}