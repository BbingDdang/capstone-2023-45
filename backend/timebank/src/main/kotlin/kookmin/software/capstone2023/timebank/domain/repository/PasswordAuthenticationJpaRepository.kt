package kookmin.software.capstone2023.timebank.domain.repository

import kookmin.software.capstone2023.timebank.domain.model.auth.PasswordAuthentication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordAuthenticationJpaRepository : JpaRepository<PasswordAuthentication, Long> {
    fun findByUsername(username: String): PasswordAuthentication?
    fun existsByUsername(username: String): Boolean
}