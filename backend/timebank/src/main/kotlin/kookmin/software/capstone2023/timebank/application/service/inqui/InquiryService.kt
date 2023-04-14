package kookmin.software.capstone2023.timebank.application.service.inqui


import kookmin.software.capstone2023.timebank.application.exception.NotFoundException
import kookmin.software.capstone2023.timebank.application.exception.UnauthorizedException
import kookmin.software.capstone2023.timebank.domain.model.Inquiry
import kookmin.software.capstone2023.timebank.domain.model.InquiryStatus
import kookmin.software.capstone2023.timebank.domain.model.Period
import kookmin.software.capstone2023.timebank.domain.repository.InquiryRepository
import kookmin.software.capstone2023.timebank.domain.repository.UserJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InquiryService(
        private val inquiryRepository: InquiryRepository,
        private val userJpaRepository: UserJpaRepository
) {

    /**
     * Dto 클래스 정의
     */

    data class InquiryDto(
            val inquiryid: Long,
            val title: String,
            val content: String,
            val inquiryDate: LocalDateTime,
            val replyStatus: InquiryStatus,
            val userId: Long
    )

    /**
     * 문의 생성Dto
     */
    data class InquiryCreateRequest(
            val title: String,
            val content: String,
            val userId: Long,
            val inquiryDate: LocalDateTime = LocalDateTime.now()
    )

    /**
     * 수정 Dto
     */
    data class InquiryUpdateRequest(
            val updateTitle: String?,
            val updateContent: String?,
            val userId: Long,
            val updateDate: LocalDateTime? = LocalDateTime.now()
    )

    /**
     * 서비스 메소드 정의
     */

    /**
     * 문의 생성 service
     */
    @Transactional
    fun createInquiry(request: InquiryCreateRequest): InquiryDto {
        val user = userJpaRepository.findByIdOrNull(request.userId)
                ?: throw NotFoundException(message = "\"User not found with id: ${request.userId}\"")
        val inquiry = Inquiry(title = request.title, content = request.content, user = user, inquiryDate = request.inquiryDate)
        val savedInquiry = inquiryRepository.save(inquiry)
        return inquiryToDto(savedInquiry)
    }

    /**
     * 전체 문의 검색 service
     */
    fun getInquiries(): List<InquiryDto> {
        val inquiries = inquiryRepository.findAll()
        return inquiries.map(::inquiryToDto)
    }

    /**
     * 문의ID검색 service
     */
    fun getInquiryById(id: Long): InquiryDto {
        val inquiry = inquiryRepository.findById(id)
                .orElseThrow { NotFoundException(message = "\"Inquiry not found with id: $id\"") }
        return inquiryToDto(inquiry)
    }

    /**
     * userId 검색 service
     */
    fun getInquiriesByUserId(userId: Long): List<InquiryDto> {
        val user = userJpaRepository.findById(userId).orElseThrow { NotFoundException(message = "\"User not found with id: $userId\"") }
        val inquiries = inquiryRepository.findByUser(user)

        return inquiries.map { inquiry -> inquiryToDto(inquiry) }
    }

    /**
     * 기간별 조회 service
     */
    fun getInquiriesByPeriod(period: Period): List<InquiryDto> {
        val end = LocalDateTime.now()
        val start = end.minusMonths(period.months)
        val inquiries = inquiryRepository.findByInquiryDateBetween(start, end)
        return inquiries.map { inquiryToDto(it) }
    }
    /**
     * 기간별 조회 for user
     */
    fun getUserInquiriesByPeriod(period: Period, userId: Long): List<InquiryDto>{
        val end = LocalDateTime.now()
        val start = end.minusMonths(period.months)
        val inquiries = inquiryRepository.findByInquiryDateBetweenAndUserId(start, end, userId)
        return inquiries.map { inquiryToDto(it) }
    }

    /**
     * 문의 제목 검색 service
     */
    fun getInquiryByTitle(title: String): List<InquiryDto>{

        val inquiries = inquiryRepository.findByTitleContainingIgnoreCase(title)
        return inquiries.map { inquiryToDto(it) }
    }
    /**
     * 문의 제목 검색 for user
     */
    fun getUserInquiryByTitle(title: String, userId: Long): List<InquiryDto>{
        val inquiries = inquiryRepository.findByTitleContainingIgnoreCaseAndUserId(title, userId)
        return inquiries.map { inquiryToDto(it) }
    }

    /**
     * 문의 수정 service
     */
    fun updateInquiry(id: Long, request: InquiryUpdateRequest): InquiryDto {
        val inquiry = inquiryRepository.findById(id)
                .orElseThrow { NotFoundException(message = "\"Inquiry not found with id: $id\"") }
//        if (inquiry.user.id != userContext.userId) {
//            throw UnauthorizedException(message = "You are not allowed to update this inquiry")
//        }
        if (inquiry.user.id != request.userId) {
            throw UnauthorizedException(message = "수정 권한이 없습니다.")
        }
        inquiry.content = request.updateContent ?: inquiry.content
        inquiry.title = request.updateTitle ?: inquiry.title
        inquiry.inquiryDate = request.updateDate ?: inquiry.inquiryDate
        val updatedInquiry = inquiryRepository.save(inquiry)
        return inquiryToDto(updatedInquiry)
    }

    /**
     * 문의삭제 service
     */
    fun deleteInquiryByUserId(userId: Long, inquiryId: Long) {
        val user = userJpaRepository.findById(userId).orElseThrow { NotFoundException(message = "\"User not found with id: $userId\"") }
        val inquiry = inquiryRepository.findById(inquiryId).orElseThrow { NotFoundException(message = "\"Inquiry not found with id: $inquiryId\"") }

        if (inquiry.user.id != user.id) {
            throw UnauthorizedException(message = "User does not have permission to delete this inquiry")
        }

        inquiryRepository.deleteById(inquiryId)
    }


    /**
     * 유틸 메소드 정의
     */
    private fun inquiryToDto(inquiry: Inquiry): InquiryDto {
        return InquiryDto(
                inquiryid = inquiry.id,
                title = inquiry.title,
                content = inquiry.content,
                inquiryDate = inquiry.inquiryDate,
                replyStatus = inquiry.replyStatus,
                userId = inquiry.user.id
        )
    }
}
