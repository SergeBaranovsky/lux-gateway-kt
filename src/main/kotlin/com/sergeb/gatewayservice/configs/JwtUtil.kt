package com.sergeb.gatewayservice.configs

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    private val secret: String = "7f3ee5912d5145766a9f8ee55e6fc0528a5eb31e07bd06a7fde4b8a733c2276a4290f6ba0f3d7a37c16be00ea134187d4fe16917f89a8a3b38e9929f724c1171"

    private var key: Key? = null

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    // TODO Add Error Handling for when the Token is invalid
    fun getAllClaimsFromToken(token: String?): Claims {
        key = Keys.hmacShaKeyFor(secret.toByteArray())
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
    }

    private fun isTokenExpired(token: String): Boolean {
        var expired = getAllClaimsFromToken(token).getExpiration().before(Date())
        return expired
    }

    fun isInvalid(token: String): Boolean {
        return isTokenExpired(token)
    }
    companion object {
        private val log = LoggerFactory.getLogger("JwkUtil")
    }
}