package dev.alexandrevieira.manager.exception

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GlobalGrpcExceptionHandler : ExceptionHandler<StatusRuntimeException, HttpResponse<JsonError>> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun handle(request: HttpRequest<*>, e: StatusRuntimeException): HttpResponse<JsonError> {
        val code: Status.Code = e.status.code
        val description: String = e.status.description ?: ""

        log.error("Handling '${e::class.simpleName}' with cod' '$code', message '$description' and cause '${e.cause}'")

        val (httpStatus: HttpStatus, message: String) = when (code) {
            Status.Code.INVALID_ARGUMENT -> Pair(HttpStatus.BAD_REQUEST, description)
            Status.Code.FAILED_PRECONDITION -> Pair(HttpStatus.BAD_REQUEST, description)
            Status.Code.PERMISSION_DENIED -> Pair(HttpStatus.FORBIDDEN, description)
            Status.Code.NOT_FOUND -> Pair(HttpStatus.NOT_FOUND, description)
            Status.Code.ALREADY_EXISTS -> Pair(HttpStatus.UNPROCESSABLE_ENTITY, description)
            Status.Code.UNAVAILABLE -> Pair(HttpStatus.SERVICE_UNAVAILABLE, description)
            else -> {
                log.error("Erro inesperado '${e.javaClass.simpleName}'")
                Pair(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado")
            }
        }

        return HttpResponse.status<JsonError>(httpStatus).body(JsonError(message))
    }
}