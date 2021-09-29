package dev.alexandrevieira.manager.exception

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class GlobalGrpcExceptionHandlerTest {
    private val handler: GlobalGrpcExceptionHandler = GlobalGrpcExceptionHandler()

    @Test
    @DisplayName("Deve retornar 422 para ALREADY_EXISTS")
    internal fun deveRetornar422() {
        val request = HttpRequest.POST("", Void::class.java)
        val exception = StatusRuntimeException(Status.ALREADY_EXISTS)
        val response = handler.handle(request, exception)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.status)
    }

    @Test
    @DisplayName("Deve retornar 400 para INVALID_ARGUMENT")
    internal fun deveRetornar400ParaInvalidArgument() {
        val request = HttpRequest.POST("", Void::class.java)
        val exception = StatusRuntimeException(Status.INVALID_ARGUMENT)
        val response = handler.handle(request, exception)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
    }

    @Test
    @DisplayName("Deve retornar 400 para FAILED_PRECONDITION")
    internal fun deveRetornar400ParaFailedPrecondition() {
        val request = HttpRequest.POST("", Void::class.java)
        val exception = StatusRuntimeException(Status.FAILED_PRECONDITION)
        val response = handler.handle(request, exception)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
    }

    @Test
    @DisplayName("Deve retornar 403 para PERMISSION_DENIED")
    internal fun deveRetornar403ParaPermissionDenied() {
        val request = HttpRequest.POST("", Void::class.java)
        val exception = StatusRuntimeException(Status.PERMISSION_DENIED)
        val response = handler.handle(request, exception)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.status)
    }

    @Test
    @DisplayName("Deve retornar 404 para NOT_FOUND")
    internal fun deveRetornar404ParaNotFound() {
        val request = HttpRequest.POST("", Void::class.java)
        val exception = StatusRuntimeException(Status.NOT_FOUND)
        val response = handler.handle(request, exception)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.status)
    }

    @Test
    @DisplayName("Deve retornar 503 para UNAVAILABLE")
    internal fun deveRetornar503ParaUnavailable() {
        val request = HttpRequest.POST("", Void::class.java)
        val exception = StatusRuntimeException(Status.UNAVAILABLE)
        val response = handler.handle(request, exception)

        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.status)
    }

    @Test
    @DisplayName("Deve retornar 500 para INTERNAL")
    internal fun deveRetornar503ParaInternal() {
        val request = HttpRequest.POST("", Void::class.java)
        val exception = StatusRuntimeException(Status.INTERNAL)
        val response = handler.handle(request, exception)

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.status)
    }
}