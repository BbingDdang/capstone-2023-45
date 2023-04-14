package kookmin.software.capstone2023.timebank.application.service.auth

import kookmin.software.capstone2023.timebank.application.exception.UnauthorizedException
import kookmin.software.capstone2023.timebank.application.service.auth.model.AuthenticationRequest
import kookmin.software.capstone2023.timebank.application.service.auth.token.AccessTokenService
import kookmin.software.capstone2023.timebank.domain.repository.PasswordAuthenticationJpaRepository
import kookmin.software.capstone2023.timebank.domain.repository.SocialAuthenticationJpaRepository
import kookmin.software.capstone2023.timebank.domain.repository.UserJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class UserLoginService(
    private val socialPlatformUserFindService: SocialPlatformUserFindService,
    private val accessTokenService: AccessTokenService,
    private val passwordEncoder: PasswordEncoder,
    private val userJpaRepository: UserJpaRepository,
    private val socialAuthenticationJpaRepository: SocialAuthenticationJpaRepository,
    private val passwordAuthenticationJpaRepository: PasswordAuthenticationJpaRepository,
) {
    data class Data(
        val accessToken: String,
        val expiresAt: Instant,
    )

    @Transactional
    fun login(authenticationRequest: AuthenticationRequest): Data {
        val userId = authenticate(authenticationRequest)

        val user = userJpaRepository.findByIdOrNull(userId)
            ?: throw UnauthorizedException(message = "등록되지 않은 사용자입니다.")

        val expiresAt = Instant.now().plus(7, ChronoUnit.DAYS)

        val accessToken = accessTokenService.issue(
            userId = user.id,
            accountId = user.accountId,
            expiresAt = expiresAt,
        )

        user.updateLastLoginAt(
            loginAt = LocalDateTime.now(),
        )

        return Data(
            accessToken = accessToken,
            expiresAt = expiresAt,
        )
    }

    fun authenticate(authenticationRequest : AuthenticationRequest): Long {
        when (authenticationRequest) {
            is AuthenticationRequest.SocialAuthenticationRequest -> {
                val socialUser = socialPlatformUserFindService.getUser(
                    type = authenticationRequest.socialPlatformType,
                    accessToken = authenticationRequest.accessToken,
                )

                val socialAuthentication = socialAuthenticationJpaRepository.findByPlatformTypeAndPlatformUserId(
                    platformType = authenticationRequest.socialPlatformType,
                    platformUserId = socialUser.id,
                ) ?: throw UnauthorizedException(message = "등록되지 않은 사용자입니다.")

                return socialAuthentication.userId
            }
            is AuthenticationRequest.PasswordAuthenticationRequest -> {
                val authentication = passwordAuthenticationJpaRepository.findByUsername(authenticationRequest.username)
                    ?: throw UnauthorizedException(message = "등록되지 않은 사용자입니다.")

                if (!passwordEncoder.matches(authenticationRequest.password, authentication.password)) {
                    throw UnauthorizedException(message = "비밀번호가 일치하지 않습니다.")
                }

                return authentication.userId
            }
        }
    }
}