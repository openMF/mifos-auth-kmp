package org.mifos.auth.kmp.core.network

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HeadersBuilder
import io.ktor.http.ParametersBuilder
import io.ktor.http.headers
import io.ktor.http.parameters
import org.mifos.auth.kmp.core.common.utils.ServerConfig
import org.mifos.auth.kmp.core.network.model.authentication.PostAuthenticationRequest
import org.mifos.auth.kmp.core.network.model.authentication.PostAuthenticationResponse


private fun defaultHeaders(): HeadersBuilder =
    HeadersBuilder().apply {
        append("Accept", "application/json")
        append("Content-Type", "application/json")
        append("Fineract-Platform-TenantId", "default")
    }


class Authentication(
    private val serverConfig: ServerConfig,
) {
    private val httpClient = KtorHttpClient

    suspend fun authenticate(
        username: String,
        password: String,
        queryParameters: ParametersBuilder.()-> Unit ={},
        additionalHeaders: HeadersBuilder.() -> Unit = {}
    ): PostAuthenticationResponse {
        val result: PostAuthenticationResponse = httpClient.post {
            url(serverConfig.baseUrl + "/authentication").apply {
                parameters(queryParameters)
            }
            val builder = defaultHeaders()
            additionalHeaders.invoke(builder)
            headers {
                appendAll(builder.build())
            }

            setBody(
                PostAuthenticationRequest(
                    username,
                    password,
                )
            )

        }.body()

        return result
    }
}