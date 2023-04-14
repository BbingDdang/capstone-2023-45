package kookmin.software.capstone2023.timebank.presentation.api.v1

import kookmin.software.capstone2023.timebank.application.service.auth.UserLoginService
import kookmin.software.capstone2023.timebank.application.service.auth.UserRegisterService
import kookmin.software.capstone2023.timebank.domain.model.AccountType
import kookmin.software.capstone2023.timebank.presentation.api.v1.model.UserLoginRequestData
import kookmin.software.capstone2023.timebank.presentation.api.v1.model.UserLoginResponseData
import kookmin.software.capstone2023.timebank.presentation.api.v1.model.UserRegisterRequestData
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/users")
class UserController(
    private val userLoginService: UserLoginService,
    private val userRegisterService: UserRegisterService,
) {
    @PostMapping("login")
    fun loginUser(
        @Validated @RequestBody data: UserLoginRequestData,
    ): UserLoginResponseData {
        val loginData = userLoginService.login(data.toAuthenticationRequest())

        return UserLoginResponseData(
            accessToken = loginData.accessToken,
        )
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(
        @Validated @RequestBody data: UserRegisterRequestData
    ) {
        userRegisterService.register(
            authentication = data.toAuthenticationRequest(),
            name = data.name,
            phoneNumber = data.phoneNumber,
            accountType = AccountType.INDIVIDUAL,
        )
    }
}