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

    fun joinRoomAsInviteCode(joinRequest: JoinRequestVO, sessionId: String): JoinedRoomVO? {
        val isPlayer = playerCodeMap[joinRequest.inviteCode] ?: return null // todo 예외처리. 이하 같음
        val room = roomJoinCodeMap[joinRequest.inviteCode] ?: return null

        val playerList = room.playerList
        val observerList = room.observerList
        val hostName = room.hostName

        val assignedName = getSignedName(joinRequest.name, playerList, observerList)

        if (isPlayer) playerList.add(RoomMemberVO(assignedName, sessionId))
        else observerList.add(RoomMemberVO(assignedName, sessionId))
        sessionIdUserInfoMap[sessionId] = SessionUserInfoVO(sessionId, assignedName, room.roomId)

        val playerNameList = playerList.map { it.name }
        val observerNameList = observerList.map { it.name }
        return JoinedRoomVO(assignedName, room.roomId, hostName, playerNameList, observerNameList, room.board, isPlayer)
    }

    fun joinWithReconnect(joinRequest: JoinRequestVO, sessionId: String): JoinedRoomVO? {
        val isPlayer = playerCodeMap[joinRequest.inviteCode] ?: return null // todo 예외처리. 이하 같음
        val room = roomJoinCodeMap[joinRequest.inviteCode] ?: return null

        val memberList = if (isPlayer) room.playerList else room.observerList
        val member = memberList.find { it.name == joinRequest.name }
        if (member == null) {
            memberList.add(RoomMemberVO(joinRequest.name, sessionId))
        } else {
            sessionIdUserInfoMap.remove(member.sessionId)
            sessionIdUserInfoMap[sessionId] = SessionUserInfoVO(sessionId, joinRequest.name, room.roomId)
            member.sessionId = sessionId
        }
        sessionIdUserInfoMap[sessionId] = SessionUserInfoVO(sessionId, joinRequest.name, room.roomId)

        val playerNameList = room.playerList.map { it.name }
        val observerNameList = room.observerList.map { it.name }
        return JoinedRoomVO(joinRequest.name, room.roomId, room.hostName, playerNameList, observerNameList, room.board, isPlayer)
    }

    fun updateBoard(board: BoardVO, roomId: String): BoardVO {
        val room = roomIdMap[roomId] ?: return board;
        room.board = board
        room.lastUpdateTime = LocalDateTime.now()
        return room.board;
    }

    fun getUserInfo(sessionId: String): SessionUserInfoVO?{
        return sessionIdUserInfoMap[sessionId]
    }

    fun handleDisconnectedSessionList(userInfo: SessionUserInfoVO): MemberListVO? {
        val room = roomIdMap[userInfo.joinedRoomId] ?: return MemberListVO("", listOf(), listOf(), "")
        room.playerList.removeIf { it.sessionId == userInfo.sessionId || it.name == userInfo.username }
        room.observerList.removeIf { it.sessionId == userInfo.sessionId || it.name == userInfo.username }
        sessionIdUserInfoMap.remove(userInfo.sessionId)

        if (room.playerList.isEmpty() && room.observerList.isEmpty()) {
            cleaningRoom(userInfo.joinedRoomId)
        }

        val playerNameList = room.playerList.map { it.name }
        val observerNameList = room.observerList.map { it.name }
        return MemberListVO(room.hostName, playerNameList, observerNameList, room.roomId)
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

    private fun getSignedName(username: String, playerList: List<RoomMemberVO>, observerList: List<RoomMemberVO>): String {
        var assignedName = username
        var dubNumber = 0
        if (playerList.map { it.name }.contains(assignedName) || observerList.map { it.name }.contains(assignedName)) {
            while (playerList.map { it.name }.contains(assignedName + "_" + dubNumber)
                || observerList.map { it.name }.contains(assignedName + "_" + dubNumber)) {
                dubNumber++
            }
            assignedName = assignedName + "_" + dubNumber
        }
        return assignedName
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