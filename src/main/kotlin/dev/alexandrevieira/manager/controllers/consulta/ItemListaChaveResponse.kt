package dev.alexandrevieira.manager.controllers.consulta

import dev.alexandrevieira.manager.controllers.registra.dto.TipoChave
import dev.alexandrevieira.manager.controllers.registra.dto.TipoConta
import dev.alexandrevieira.stubs.ListaChaveResponse
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Introspected
data class ItemListaChaveResponse(
    val chavePixId: String,
    val clienteId: String,
    val tipoChave: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val criadaEm: ZonedDateTime
) {
    companion object {
        fun of(chave: ListaChaveResponse.ChaveInfo): ItemListaChaveResponse {
            val instant = Instant.ofEpochSecond(chave.criadaEm.seconds, chave.criadaEm.nanos.toLong())

            return ItemListaChaveResponse(
                chavePixId = chave.chavePixId,
                clienteId = chave.clienteId,
                tipoChave = TipoChave.valueOf(chave.tipo.name),
                chave = chave.chave,
                tipoConta = TipoConta.valueOf(chave.tipoConta.name),
                criadaEm = ZonedDateTime.ofInstant(instant, ZoneId.of("America/Sao_Paulo"))
            )
        }
    }
}