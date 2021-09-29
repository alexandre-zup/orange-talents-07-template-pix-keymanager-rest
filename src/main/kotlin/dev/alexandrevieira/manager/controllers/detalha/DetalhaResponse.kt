package dev.alexandrevieira.manager.controllers.detalha

import com.fasterxml.jackson.annotation.JsonFormat
import dev.alexandrevieira.manager.controllers.registra.dto.TipoChave
import dev.alexandrevieira.manager.controllers.registra.dto.TipoConta
import dev.alexandrevieira.stubs.ConsultaChaveResponse
import io.micronaut.core.annotation.Introspected
import java.time.Instant

@Introspected
data class DetalhaResponse(
    val chavePixId: String,
    val tipo: TipoChave,
    val chave: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
    val criadaEm: Instant,
    val conta: ContaResponse
) {
    @Introspected
    data class ContaResponse(
        val agencia: String,
        val numero: String,
        val tipo: TipoConta,
        val instituicao: InstituicaoResponse,
        val titular: TitularResponse
    ) {
        @Introspected
        data class TitularResponse(
            val id: String,
            val nome: String,
            val cpf: String
        )

        @Introspected
        data class InstituicaoResponse(
            val ispb: String,
            val nome: String
        )

        companion object {
            fun of(conta: ConsultaChaveResponse.ContaInfo): ContaResponse {
                val instituicao = InstituicaoResponse(conta.instituicao.ispb, conta.instituicao.nome)
                val titular = TitularResponse(conta.titular.id, conta.titular.nome, conta.titular.cpf)
                return ContaResponse(
                    conta.agencia,
                    conta.numero,
                    TipoConta.valueOf(conta.tipo.name),
                    instituicao,
                    titular
                )
            }
        }
    }

    companion object {
        fun of(chave: ConsultaChaveResponse): DetalhaResponse {
            return DetalhaResponse(
                chave.chavePixId,
                TipoChave.valueOf(chave.tipo.name),
                chave.chave,
                Instant.ofEpochSecond(chave.criadaEm.seconds, chave.criadaEm.nanos.toLong()),
                ContaResponse.of(chave.conta)
            )
        }
    }
}
