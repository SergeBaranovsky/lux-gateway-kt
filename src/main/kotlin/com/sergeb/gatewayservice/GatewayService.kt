package com.sergeb.gatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GatewayService

fun main(args: Array<String>) {
    runApplication<GatewayService>(*args)
}
