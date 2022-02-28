package com.airwallex.demo

import com.airwallex.grpc.error.Error
import com.airwallex.grpc.error.singleResult
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import demo.UserServiceRpc
import io.grpc.Status.Code.INVALID_ARGUMENT
import java.util.UUID
import javax.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val repo: UserRepository) : UserServiceRpc {

    override suspend fun create(@Valid request: User): Result<UUID, Error> {
        if (request.name == "admin") {
            return Err(
                Error.of(
                    statusCode = INVALID_ARGUMENT,
                    description = "cannot create admin",
                    details = mapOf("invalid_name" to request.name)
                )
            )
        }

        return repo.save(request).map { it.id!! }.singleResult()
    }

    override suspend fun get(request: UUID): Result<User, Error> {
        return repo.findById(request).singleResult()
    }
}
