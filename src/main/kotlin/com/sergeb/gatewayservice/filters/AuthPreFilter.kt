package com.sergeb.gatewayservice.filters

import com.sergeb.gatewayservice.configs.JwtUtil
import io.jsonwebtoken.Claims
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

@Component
class AuthPreFilter : AbstractGatewayFilterFactory<AuthPreFilter.Config>(Config::class.java) {
    private val jwtUtil: JwtUtil = JwtUtil()

    override fun apply(config: Config): GatewayFilter {

        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            log.info("*** Enter PreFilter - Auth ***")
            val request = exchange.request
            val response = exchange.response

            if (isAuthMissing(request)) {
                log.info("isAuthMissing : true")
                exchange.response.setStatusCode(HttpStatus.UNAUTHORIZED)
                return@GatewayFilter exchange.response.setComplete()
            } else {
                val token = getAuthHeader(request)
                if (jwtUtil.isInvalid(token)) {
                    exchange.response.setStatusCode(HttpStatus.FORBIDDEN)
                    return@GatewayFilter exchange.response.setComplete()
                } else {
                    updateRequest(exchange, token)
                }
            }

            chain.filter(exchange)
        }
    }

    class Config { // ... we don't need fields/properties for this implementation
    }

    companion object {
        private val log = LoggerFactory.getLogger("AuthPreFilter")
    }

    private fun getAuthHeader(request: ServerHttpRequest): String {
        return request.headers.getOrEmpty("Authorization")[0]
    }

    private fun isAuthMissing(request: ServerHttpRequest): Boolean {
        return !request.headers.containsKey("Authorization")
    }

    private fun updateRequest(exchange: ServerWebExchange, token: String) {
        val claims: Claims = jwtUtil.getAllClaimsFromToken(token)
        exchange.request.mutate()
            .header("email", claims["email"].toString())
            .build()
    }

}
