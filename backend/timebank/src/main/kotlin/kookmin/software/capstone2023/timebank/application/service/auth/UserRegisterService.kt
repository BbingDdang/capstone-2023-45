package kookmin.software.capstone2023.timebank.application.service.auth

import kookmin.software.capstone2023.timebank.application.exception.ConflictException
import kookmin.software.capstone2023.timebank.application.service.auth.model.AuthenticationRequest
import kookmin.software.capstone2023.timebank.domain.model.Account
import kookmin.software.capstone2023.timebank.domain.model.AccountType
import kookmin.software.capstone2023.timebank.domain.model.User
import kookmin.software.capstone2023.timebank.domain.model.auth.PasswordAuthentication
import kookmin.software.capstone2023.timebank.domain.model.auth.SocialAuthentication
import kookmin.software.capstone2023.timebank.domain.repository.AccountJpaRepository
import kookmin.software.capstone2023.timebank.domain.repository.PasswordAuthenticationJpaRepository
import kookmin.software.capstone2023.timebank.domain.repository.SocialAuthenticationJpaRepository
import kookmin.software.capstone2023.timebank.domain.repository.UserJpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegisterService(
    private val socialPlatformUserFindService: SocialPlatformUserFindService,
    private val passwordEncoder: PasswordEncoder,
    private val userJpaRepository: UserJpaRepository,
    private val accountJpaRepository: AccountJpaRepository,
    private val socialAuthenticationJpaRepository: SocialAuthenticationJpaRepository,
    private val passwordAuthenticationJpaRepository: PasswordAuthenticationJpaRepository,
) {
    @Transactional
    fun register(
        authentication: AuthenticationRequest,
        name: String,
        phoneNumber: String,
        accountType: AccountType,
    ) {
        validateDuplicatedRegistration(authentication)

        val account = accountJpaRepository.save(
            Account(type = accountType, profile = null)
        )

        val user = userJpaRepository.save(
            User(
                authenticationType = authentication.type,
                accountId = account.id,
                name = name,
                phoneNumber = phoneNumber,
                lastLoginAt = null,
            )
        )

        when (authentication) {
            is AuthenticationRequest.SocialAuthenticationRequest -> {
                val socialUser = socialPlatformUserFindService.getUser(
                    type = authentication.socialPlatformType,
                    accessToken = authentication.accessToken,
                )

                socialAuthenticationJpaRepository.save(
                    SocialAuthentication(
                        userId = user.id,
                        platformType = authentication.socialPlatformType,
                        platformUserId = socialUser.id,
                    )
                )
            }

            is AuthenticationRequest.PasswordAuthenticationRequest -> {
                val encodedPassword = passwordEncoder.encode(
                    authentication.password
                )

                passwordAuthenticationJpaRepository.save(
                    PasswordAuthentication(
                        userId = user.id,
                        username = authentication.username,
                        password = encodedPassword,
                    )
                )
            }
        }
    }

    fun validateDuplicatedRegistration(authentication: AuthenticationRequest) {
        when (authentication) {
            is AuthenticationRequest.SocialAuthenticationRequest -> {
                val socialUser = socialPlatformUserFindService.getUser(
                    type = authentication.socialPlatformType,
                    accessToken = authentication.accessToken,
                )

                val socialAuthentication = socialAuthenticationJpaRepository.findByPlatformTypeAndPlatformUserId(
                    platformType = authentication.socialPlatformType,
                    platformUserId = socialUser.id,
                )

                if (socialAuthentication != null) {
                    throw ConflictException(message = "이미 등록된 사용자입니다.")
                }
            }

            is AuthenticationRequest.PasswordAuthenticationRequest -> {
                val existsAuthentication = passwordAuthenticationJpaRepository.existsByUsername(
                    username = authentication.username,
                )

                if (existsAuthentication) {
                    throw ConflictException(message = "이미 등록된 사용자입니다.")
                }
            }
        }
    }
}