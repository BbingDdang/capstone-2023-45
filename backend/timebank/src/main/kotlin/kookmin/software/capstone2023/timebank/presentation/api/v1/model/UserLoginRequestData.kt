package kookmin.software.capstone2023.timebank.presentation.api.v1.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.NotBlank
import kookmin.software.capstone2023.timebank.application.service.auth.model.AuthenticationRequest
import kookmin.software.capstone2023.timebank.domain.model.auth.AuthenticationType
import kookmin.software.capstone2023.timebank.domain.model.auth.SocialPlatformType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "authenticationType",
)
sealed class UserLoginRequestData(
    val authenticationType: AuthenticationType,
) {
    open fun toAuthenticationRequest(): AuthenticationRequest {
        return when (this) {
            is SocialLoginRequestData -> toAuthenticationRequest()
            is PasswordLoginRequestData -> toAuthenticationRequest()
        }
    }

    @JsonTypeName("social")
    data class SocialLoginRequestData(
        val socialPlatformType: SocialPlatformType,

        @field:NotBlank(message = "액세스 토큰은 필수입니다.")
        val accessToken: String,
    ) : UserLoginRequestData(AuthenticationType.SOCIAL) {
        override fun toAuthenticationRequest() = AuthenticationRequest.SocialAuthenticationRequest(
            socialPlatformType = socialPlatformType,
            accessToken = accessToken,
        )
    }

    @JsonTypeName("password")
    data class PasswordLoginRequestData(
        @field:NotBlank(message = "아이디는 필수입니다.")
        val username: String,

        @field:NotBlank(message = "비밀번호는 필수입니다.")
        val password: String,
    ) : UserLoginRequestData(AuthenticationType.PASSWORD) {
        override fun toAuthenticationRequest() = AuthenticationRequest.PasswordAuthenticationRequest(
            username = username,
            password = password,
        )
    }
}
