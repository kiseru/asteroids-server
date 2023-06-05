package com.kiseru.asteroids.server.security

import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableReactiveMethodSecurity
class ApplicationSecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http {
            csrf { disable() }
            authorizeExchange {
                authorize(anyExchange, authenticated)
            }
            httpBasic { }
        }

    @Bean
    fun userDetailsService(userService: UserService): ReactiveUserDetailsService =
        ReactiveUserDetailsService { username ->
            Mono.justOrEmpty(username)
                .mapNotNull {
                    userService.findUserByUsername(it)
                        ?.let { user -> mapToUserDetails(user) }
                }
        }

    private fun mapToUserDetails(user: ApplicationUser): UserDetails =
        User.withDefaultPasswordEncoder()
            .username(user.username)
            .password("password")
            .roles("USER")
            .build()
}
