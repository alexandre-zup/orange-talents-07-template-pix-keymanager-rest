package dev.alexandrevieira.manager.controllers.detalha

import dev.alexandrevieira.manager.validation.ValidUUID
import dev.alexandrevieira.stubs.ConsultaChaveRequest
import dev.alexandrevieira.stubs.KeyManagerConsultaServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import java.net.URI
import javax.validation.constraints.NotBlank

@Controller
@Validated
class DetalhaChaveController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var client: KeyManagerConsultaServiceGrpc.KeyManagerConsultaServiceBlockingStub

    @Get("/api/v1/clientes/{clienteId}/pix/{chavePixId}")
    fun detalha(
        @NotBlank @ValidUUID clienteId: String,
        @NotBlank @ValidUUID chavePixId: String
    ): HttpResponse<DetalhaResponse> {
        log.info("Método 'detalha' recebendo 'clienteId' $clienteId, 'chavePixId' $chavePixId")

        return grpcRequest(clienteId, chavePixId).also { request ->
            log.info("Chamando serviço gRPC com 'request' $request")
        }.let { request ->
            client.consulta(request)
        }.also { response ->
            log.info("Resposta recebida do serviço gRPC: $response")
        }.let { response ->
            HttpResponse.ok(DetalhaResponse.of(response))
        }
    }

    private fun grpcRequest(clienteId: String, chavePixId: String): ConsultaChaveRequest {
        val pixId = ConsultaChaveRequest.FiltroPorId.newBuilder()
            .setChavePixId(chavePixId)
            .setClienteId(clienteId)

        return ConsultaChaveRequest.newBuilder()
            .setPixId(pixId)
            .build()
    }
}