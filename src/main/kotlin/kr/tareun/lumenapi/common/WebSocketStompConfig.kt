package kr.tareun.lumenapi.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@ConfigurationProperties(prefix = "host")
class WebSocketStompConfig :WebSocketMessageBrokerConfigurer{

    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 5 // 동시에 실행 가능한 스레드 수
        scheduler.setThreadNamePrefix("WebSocketHeartbeat-")
        scheduler.initialize()
        return scheduler
    }

    lateinit var hostNames: Array<String>

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/remote-ws")
            .setAllowedOrigins(*hostNames)
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic", "/queue")
            .setHeartbeatValue(longArrayOf(5000, 5000))
            .setTaskScheduler(taskScheduler())
        registry.setApplicationDestinationPrefixes("/app")
    }
}