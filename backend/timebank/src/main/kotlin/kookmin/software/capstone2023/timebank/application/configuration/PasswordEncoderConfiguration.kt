package kookmin.software.capstone2023.timebank.application.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder

@Configuration
class PasswordEncoderConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8()
    }
}