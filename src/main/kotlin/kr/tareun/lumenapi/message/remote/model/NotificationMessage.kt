package kr.tareun.lumenapi.message.remote.model

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationMessage(val status: Status, val message: String) {
    enum class Status {
        @JsonProperty("success")
        SUCCESS,

        @JsonProperty("info")
        INFO,

        @JsonProperty("warning")
        WARNING,

        @JsonProperty("error")
        ERROR
    }
}
