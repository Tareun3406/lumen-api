package kr.tareun.lumenapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class LumenCalcApiApplication

fun main(args: Array<String>) {
	runApplication<LumenCalcApiApplication>(*args)
}
