package kr.tareun.lumenapi.message.remote

import kr.tareun.lumenapi.message.remote.model.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

private val logger = LoggerFactory.getLogger(RemoteController::class.java)

@Service
class RemoteService {

    private val roomMap = mutableMapOf<String, RemoteRoomVO>()
    private val playerCodeMap = mutableMapOf<String, Boolean>()
    private val roomIdMap = mutableMapOf<String, RemoteRoomVO>()

    private val inviteCodeLength = 10;

    fun createRoom(board: BoardVO): CreatedRoomVO {
        val playerCode = generateInviteCode(inviteCodeLength, true)
        val observerCode = generateInviteCode(inviteCodeLength, false)
        val roomId = UUID.randomUUID().toString()
        val remoteRoom = RemoteRoomVO(roomId, mutableListOf(), mutableListOf(), playerCode, observerCode, board)
        roomMap[playerCode] = remoteRoom
        roomMap[observerCode] = remoteRoom
        roomIdMap[roomId] = remoteRoom

        val result = CreatedRoomVO(roomId, playerCode, observerCode)
        logger.debug("created: {}", result)
        return result
    }

    fun findRoomAsInviteCode(name: String, inviteCode: String): JoinedRoomVO? {
        val isPlayer = playerCodeMap.containsKey(inviteCode)
        val room = roomMap[inviteCode] ?: return null

        val playerList = room.playerList
        val observerList = room.observerList

        var assignedName = name
        var dubNumber = 0
        if (playerList.contains(assignedName) || observerList.contains(assignedName)) {
            while (playerList.contains(assignedName + "_" + dubNumber) && observerList.contains(assignedName + "_" + dubNumber)) {
                dubNumber++
            }
            assignedName = assignedName + "_" + dubNumber
        }

        if (isPlayer) playerList.add(assignedName)
        else observerList.add(assignedName)

        return JoinedRoomVO(assignedName, room.roomId, playerList, observerList, room.board);
    }

    fun updateBoard(board: BoardVO, roomId: String): BoardVO {
        val room = roomIdMap[roomId] ?: return board;
        room.board = board
        return room.board;
    }

    fun disconnect(roomId: String, userName: String): MemberListVO {
        val room = roomIdMap[roomId] ?: return MemberListVO(listOf(), listOf())
        room.playerList.remove(userName)
        room.observerList.remove(userName)

        return MemberListVO(room.playerList, room.observerList)
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