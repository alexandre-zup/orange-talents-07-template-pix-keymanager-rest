package dev.alexandrevieira.manager.controllers.consulta

import dev.alexandrevieira.manager.validation.ValidUUID
import dev.alexandrevieira.stubs.KeyManagerListaServiceGrpc
import dev.alexandrevieira.stubs.ListaChaveRequest
import dev.alexandrevieira.stubs.ListaChaveResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank

@Controller
class ListaChavesController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var client: KeyManagerListaServiceGrpc.KeyManagerListaServiceBlockingStub

    @Get("/api/v1/clientes/{clienteId}/pix")
    fun lista(@NotBlank @ValidUUID clienteId: String): HttpResponse<List<ItemListaChaveResponse>> {
        log.info("Método 'lista' recebendo 'clienteId' $clienteId")

        val list: List<ListaChaveResponse.ChaveInfo> = grpcRequest(clienteId).also { request ->
            log.info("Chamando serviço gRPC com 'request' $request")
        }.let { request ->
            client.lista(request)
        }.also { response ->
            log.info("Resposta recebida do serviço gRPC: $response")
        }.chavesList

        return list.map {
            ItemListaChaveResponse.of(it)
        }.let { mappedList ->
            HttpResponse.ok(mappedList)
        }
    }

    private fun grpcRequest(clienteId: String): ListaChaveRequest {
        return ListaChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .build()
    }
}