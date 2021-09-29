package dev.alexandrevieira.manager.controllers.remove

import dev.alexandrevieira.manager.validation.ValidUUID
import dev.alexandrevieira.stubs.KeyManagerRemoveServiceGrpc
import dev.alexandrevieira.stubs.RemoveChaveRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank

@Controller
@Validated
class RemoveChaveController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var client: KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub

    @Delete("/api/v1/clientes/{clienteId}/pix/{chavePixId}")
    fun remove(
        @NotBlank @ValidUUID clienteId: String,
        @NotBlank @ValidUUID chavePixId: String
    ): MutableHttpResponse<Void> {
        log.info("Método 'remove' recebendo 'clienteId' $clienteId, 'chavePixId' $chavePixId")
        val grpcRequest: RemoveChaveRequest = RemoveChaveRequest.newBuilder()
            .setChavePixId(chavePixId)
            .setClienteId(clienteId)
            .build()
        log.info("Chamando serviço gRPC com 'request' $grpcRequest")
        client.remove(grpcRequest).also { grpcResponse ->
            log.info("Resposta recebida do serviço gRPC: $grpcResponse")
        }
        return HttpResponse.noContent()
    }
}