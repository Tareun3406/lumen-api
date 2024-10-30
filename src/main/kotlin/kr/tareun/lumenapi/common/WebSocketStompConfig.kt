package kr.tareun.lumenapi.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@ConfigurationProperties(prefix = "host")
class WebSocketStompConfig :WebSocketMessageBrokerConfigurer{

    lateinit var hostNames: Array<String>

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/remote-ws")
            .setAllowedOrigins(*hostNames)
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic", "/queue")
        registry.setApplicationDestinationPrefixes("/app")
    }
}