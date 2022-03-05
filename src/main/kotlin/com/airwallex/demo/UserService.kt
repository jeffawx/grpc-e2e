package com.airwallex.demo

import com.airwallex.grpc.error.Error
import com.airwallex.grpc.error.Error.Companion.notFound
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import demo.UserServiceRpc
import java.util.UUID
import javax.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val repo: UserRepository) : UserServiceRpc {

    override fun create(/* Trigger validation rules in User class*/@Valid request: User): Result<UUID, Error> {
        if (request.name == "admin") {
            return Error.invalid( // gRPC status code inferred from helper method name
                code = "ADMIN_NOT_ALLOWED", // optional application defined code, only for demo purpose
                reason = "cannot create admin",
                details = mapOf("invalid_name" to request.name) // extra data passed back to client, optional
            )
        }

        return Ok(repo.save(request).id!!)
    }

    override fun get(request: UUID): Result<User, Error> =
        repo.findById(request).orElse(null)?.let { Ok(it) } ?: notFound()
}
