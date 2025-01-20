package kr.tareun.lumenapi.message.remote.model

import com.fasterxml.jackson.annotation.JsonProperty

@Suppress("unused")
data class BoardVO(
    val firstPlayer: PlayerState,
    val secondPlayer: PlayerState,
    val damageLogs: List<DamageLog>,
) {

    data class PlayerState(
        val isFirst: Boolean,
        val currentHp: Int,
        val damagedHp: Int,
        val character: Character,
        val fp: Int
    )

    data class DamageLog(
        val isFirstPlayer: Boolean,
        val type: DamageType,
        val payload: Int,
        val result: Int
    )

    enum class DamageType {
        DAMAGE, HEAL
    }

    data class Character(
        val name: CharacterName,
        val portrait: String,
        val hp: Hp,
        val tokens: List<Token>
    )

    data class Hp(
        val maxHp: Int,
        val hpHand: List<List<Int>>
    )

    data class Token(
        val name: String,
        val img: String,
        val type: TokenType,
        val toggle: Boolean? = null,
        val count: Int? = null,
        val maxCount: Int? = null,
        val toggleCount: Int? = null,
        val description: String
    )

    enum class TokenType {
        TOGGLE, COUNTER
    }

    enum class CharacterName {
        @JsonProperty("루트") ROOT,
        @JsonProperty("울프") WOLF,
        @JsonProperty("비올라") VIOLA,
        @JsonProperty("델피") DELPHI,
        @JsonProperty("키스") KISS,
        @JsonProperty("니아") NIA,
        @JsonProperty("레브") REB,
        @JsonProperty("타오") TAO,
        @JsonProperty("리타") RITA,
        @JsonProperty("선택없음") NONE
    }
}