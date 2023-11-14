package com.sergeb.gatewayservice.filters

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class HeaderPostFilter : AbstractGatewayFilterFactory<HeaderPostFilter.Config>(Config::class.java) {

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            log.info("*** Enter PostFilter ***")
            val response = exchange.response
            response.beforeCommit {
                response.headers["X-Custom-Header"] = "Powered by Gateway Service"
                Mono.empty()
            }
            chain.filter(exchange).then(Mono.fromRunnable {})
        }
    }

    class Config { // ... we don't need fields/properties for this implementation
    }

    companion object {
        private val log = LoggerFactory.getLogger("HeaderPostFilter")
    }
}

