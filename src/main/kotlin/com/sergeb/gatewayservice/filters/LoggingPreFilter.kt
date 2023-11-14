package com.sergeb.gatewayservice.filters

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

@Component
class LoggingPreFilter() : AbstractGatewayFilterFactory<LoggingPreFilter.Config>(Config::class.java) {

    override fun apply(config: Config): GatewayFilter {

        return OrderedGatewayFilter({ exchange: ServerWebExchange, chain: GatewayFilterChain ->
            log.info("*** Enter PreFilter - Logging ***")
            val request = exchange.request
            val headers = request.headers
            val token = headers.getValuesAsList("Authorization").toString()
            log.info(
                "{} - {} - {} - {} - {} - {}",
                request.remoteAddress.toString(),
                request.method.name(),
                headers.getValuesAsList("Host").toString(),
                request.path.value(),
                request.uri,
                headers.getValuesAsList("User-Agent").toString()
            )
            log.info("Authorization Token: {}", token)
            chain.filter(exchange)
        }, 0)
    }

    class Config { // ... we don't need fields/properties for this implementation
    }

    companion object {
        private val log = LoggerFactory.getLogger("LoggingPreFilter")
    }
}

