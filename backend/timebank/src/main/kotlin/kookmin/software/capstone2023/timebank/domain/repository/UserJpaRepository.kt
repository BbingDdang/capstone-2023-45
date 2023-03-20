package kookmin.software.capstone2023.timebank.domain.repository

import kookmin.software.capstone2023.timebank.domain.model.SocialPlatformType
import kookmin.software.capstone2023.timebank.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<User, Long> {
    fun findBySocialLoginProviderAndSocialUserId(socialLoginProvider: SocialPlatformType, socialUserId: String): User?
}