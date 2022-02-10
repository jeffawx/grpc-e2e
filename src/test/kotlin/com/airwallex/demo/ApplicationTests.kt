package com.airwallex.demo

import com.airwallex.grpc.annotations.GrpcClient
import com.google.rpc.BadRequest
import demo.UserServiceApi
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    "grpc.server.name=test",
    "grpc.server.port=-1", // -1 for in-process server
    "grpc.client.channels.userClient.in-process=true",
    "grpc.client.channels.userClient.server-name=test"
)
class ApplicationTests {

    @Autowired
    @GrpcClient
    private lateinit var userClient: UserServiceApi

    @Test
    fun `can create user and load`() {
        val user = User(name = "Jeff")
        val userId = userClient.create(user).block()!!
        assertEquals(user.name, userClient.get(userId).block()?.name)
    }

    @Test
    fun `create user validation failed`() {
        val user = User(name = "J")

        val exception = assertThrows<Throwable> {
            userClient.create(user).block()
        }

        val error = StatusProto.fromThrowable(exception)!!
        assertEquals(Status.Code.INVALID_ARGUMENT.value(), error.code)

        val details = error.detailsList.first {
            it.`is`(BadRequest::class.java)
        }.unpack(BadRequest::class.java)

        val nameError = details.fieldViolationsList.first()
        assertEquals("name", nameError.field)
        assertEquals("invalid name", nameError.description)
    }
}
