package kr.tareun.lumenapi.message.remote

import kr.tareun.lumenapi.message.remote.model.CreatedRoomVO
import kr.tareun.lumenapi.message.remote.model.JoinedRoomVO
import kr.tareun.lumenapi.message.remote.model.MemberListVO
import kr.tareun.lumenapi.message.remote.model.RemoteRoomVO
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RemoteService {

    private val roomMap = mutableMapOf<String, RemoteRoomVO>()
    private val playerCodeMap = mutableMapOf<String, Boolean>()
    private val roomIdMap = mutableMapOf<String, RemoteRoomVO>()

    private val inviteCodeLength = 10;

    fun createRoom(board: Map<String, Any>): CreatedRoomVO {
        val playerCode = generateInviteCode(inviteCodeLength, true)
        val observerCode = generateInviteCode(inviteCodeLength, false)

        val roomId = UUID.randomUUID().toString()
        val remoteRoom = RemoteRoomVO(roomId, mutableListOf(), mutableListOf(), playerCode, observerCode, board)
        roomMap[playerCode] = remoteRoom
        roomMap[observerCode] = remoteRoom
        roomIdMap[roomId] = remoteRoom

        return CreatedRoomVO(roomId, playerCode, observerCode)
    }

    fun findRoomAsInviteCode(name: String, inviteCode: String): JoinedRoomVO {
        val isPlayer = playerCodeMap.containsKey(inviteCode)
        val room = roomMap[inviteCode] ?: return JoinedRoomVO("", "", listOf(), listOf())

        val playerList = room.playerList
        val observerList = room.observerList

        var assignedName = name
        var dubNumber = 0
        if (playerList.contains(assignedName) || observerList.contains(assignedName)) {
            while (!playerList.contains(assignedName + "_" + dubNumber) && !observerList.contains(assignedName + "_" + dubNumber)) {
                dubNumber++
            }
            assignedName = assignedName + "_" + dubNumber
        }

        if (isPlayer) playerList.add(assignedName)
        else observerList.add(assignedName)

        return JoinedRoomVO(assignedName, room.roomId, playerList, observerList)
    }

    fun updateBoard(board: Any, roomId: String): Any {
        val room = roomIdMap[roomId] ?: return ""
        room.board = board
        return room;
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
        } while (!playerCodeMap.containsKey(inviteCode))

        playerCodeMap[inviteCode] = isPlayerCode
        return inviteCode
    }
}