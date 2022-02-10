package com.airwallex.demo

import demo.UserServiceApi
import java.util.UUID
import javax.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val repo: UserRepository) : UserServiceApi {

    override fun create(@Valid request: User): UUID = repo.save(request).id!!

    override fun get(request: UUID): User = repo.findById(request).get()
}
