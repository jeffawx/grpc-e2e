package com.airwallex.demo

import demo.UserServiceApi
import java.util.UUID
import javax.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional
class UserService(private val repo: UserRepository) : UserServiceApi {

    override fun create(@Valid request: User): Mono<UUID> = repo.save(request).mapNotNull { it.id }

    override fun get(request: UUID): Mono<User> = repo.findById(request)
}
