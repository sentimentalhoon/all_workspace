package com.psmo.service

import com.psmo.dto.*
import com.psmo.model.*
import com.psmo.security.JwtConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

class AuthService(private val jwtConfig: JwtConfig) {

    fun register(request: RegisterRequest): AuthResponse {
        // 이메일 중복 체크
        val existingUser = transaction {
            Users.selectAll().where { Users.email eq request.email }.singleOrNull()
        }

        if (existingUser != null) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다.")
        }

        // 사용자 생성
        val userId = transaction {
            Users.insert {
                it[name] = request.name
                it[email] = request.email
                it[password] = BCrypt.hashpw(request.password, BCrypt.gensalt())
                it[role] = "USER"
            } get Users.id
        }

        // 토큰 생성
        val token = jwtConfig.generateToken(request.email)
        val refreshToken = jwtConfig.generateRefreshToken(request.email)

        // Refresh token 저장
        saveRefreshToken(userId.value, refreshToken)

        val user = transaction {
            Users.selectAll().where { Users.id eq userId }
                .single()
                .let {
                    UserDto(
                        id = it[Users.id].value,
                        name = it[Users.name],
                        email = it[Users.email],
                        role = it[Users.role]
                    )
                }
        }

        return AuthResponse(token, refreshToken, user)
    }

    fun login(request: LoginRequest): AuthResponse {
        // 사용자 조회
        val user = transaction {
            Users.selectAll().where { Users.email eq request.email }.singleOrNull()
        } ?: throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")

        // 비밀번호 확인
        if (!BCrypt.checkpw(request.password, user[Users.password])) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }

        val userId = user[Users.id].value

        // 토큰 생성
        val token = jwtConfig.generateToken(request.email)
        val refreshToken = jwtConfig.generateRefreshToken(request.email)

        // 기존 Refresh token 삭제 후 새로 저장
        transaction {
            RefreshTokens.deleteWhere { RefreshTokens.userId eq userId }
        }
        saveRefreshToken(userId, refreshToken)

        val userDto = UserDto(
            id = user[Users.id].value,
            name = user[Users.name],
            email = user[Users.email],
            role = user[Users.role]
        )

        return AuthResponse(token, refreshToken, userDto)
    }

    fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        val refreshTokenValue = request.refreshToken

        // Refresh token 조회
        val tokenData = transaction {
            RefreshTokens.selectAll().where { RefreshTokens.token eq refreshTokenValue }
                .singleOrNull()
        } ?: throw IllegalArgumentException("유효하지 않은 Refresh Token입니다.")

        // 만료 체크
        if (LocalDateTime.now().isAfter(tokenData[RefreshTokens.expiryDate])) {
            transaction {
                RefreshTokens.deleteWhere { RefreshTokens.token eq refreshTokenValue }
            }
            throw IllegalArgumentException("만료된 Refresh Token입니다.")
        }

        // 사용자 조회
        val userId = tokenData[RefreshTokens.userId].value
        val user = transaction {
            Users.selectAll().where { Users.id eq userId }
                .single()
        }

        val email = user[Users.email]

        // 새 토큰 생성
        val newToken = jwtConfig.generateToken(email)
        val newRefreshToken = jwtConfig.generateRefreshToken(email)

        // Refresh token 갱신
        transaction {
            RefreshTokens.deleteWhere { RefreshTokens.token eq refreshTokenValue }
        }
        saveRefreshToken(userId, newRefreshToken)

        val userDto = UserDto(
            id = user[Users.id].value,
            name = user[Users.name],
            email = user[Users.email],
            role = user[Users.role]
        )

        return AuthResponse(newToken, newRefreshToken, userDto)
    }

    fun getCurrentUser(email: String): UserDto {
        return transaction {
            Users.selectAll().where { Users.email eq email }
                .singleOrNull()
                ?.let {
                    UserDto(
                        id = it[Users.id].value,
                        name = it[Users.name],
                        email = it[Users.email],
                        role = it[Users.role]
                    )
                } ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
        }
    }

    fun logout(email: String) {
        transaction {
            val user = Users.selectAll().where { Users.email eq email }.singleOrNull()
            if (user != null) {
                RefreshTokens.deleteWhere { RefreshTokens.userId eq user[Users.id] }
            }
        }
    }

    private fun saveRefreshToken(userId: Long, tokenValue: String) {
        transaction {
            RefreshTokens.insert {
                it[token] = tokenValue
                it[RefreshTokens.userId] = userId
                it[expiryDate] = LocalDateTime.now().plusDays(7)
            }
        }
    }
}
