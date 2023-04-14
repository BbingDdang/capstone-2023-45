package kookmin.software.capstone2023.timebank.domain.repository

import kookmin.software.capstone2023.timebank.domain.model.Inquiry
import kookmin.software.capstone2023.timebank.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface InquiryRepository : JpaRepository<Inquiry, Long>{
    fun findByUser(user: User): List<Inquiry>
    fun findByUserId(userId: Long): List<Inquiry>
    fun findByTitleContainingIgnoreCase(title: String): List<Inquiry>
    fun findByTitleContainingIgnoreCaseAndUserId(title: String, userId: Long): List<Inquiry>
    fun findByInquiryDateBetween(start: LocalDateTime, end: LocalDateTime): List<Inquiry>
}
