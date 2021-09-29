package dev.alexandrevieira.manager.controllers

import dev.alexandrevieira.manager.controllers.dto.NovaChaveRequest
import dev.alexandrevieira.manager.validation.ValidUUID
import dev.alexandrevieira.stubs.KeyManagerRegistraServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import java.net.URI
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Controller("/api/v1")
@Validated
class NovaChaveController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var client: KeyManagerRegistraServiceGrpc.KeyManagerRegistraServiceBlockingStub

    @Post("/clientes/{clienteId}/pix")
    fun cria(
        @ValidUUID @NotBlank clienteId: String,
        @Body @Valid httpRequest: NovaChaveRequest
    ): HttpResponse<Void> {
        log.info("Método 'cria' recebendo 'clienteId' $clienteId, 'request' $httpRequest")

        httpRequest.toGrpcRequest(clienteId).also { grpcRequest ->
            log.info("Chamando serviço gRPC com 'request' $grpcRequest")
        }.let { grpcRequest ->
            client.registra(grpcRequest)
        }.also { grpcResponse ->
            log.info("Resposta recebida do servico gRPC: $grpcResponse")
        }.let { grpcResponse ->
            URI.create("/api/v1/clientes/${grpcResponse.clienteId}/pix/${grpcResponse.chavePixId}").let { uri ->
                return HttpResponse.created(uri)
            }
        }
    }
}