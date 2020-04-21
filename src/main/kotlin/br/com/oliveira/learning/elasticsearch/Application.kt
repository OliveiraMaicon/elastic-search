package br.com.oliveira.learning.elasticsearch

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.InjectionPoint
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@SpringBootApplication
class Application {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }

    @Bean
    fun webClient(logger: Logger): WebClient {
        return WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(reactorResourceFactory(), httpClient()))
                /*.filter { clientRequest, next ->
                    if (clients.any { clientRequest.url().host.contains(it) }) {
                        val filtered = ClientRequest.from(clientRequest).headers {
                            logger.info(it.toSingleValueMap().toString())
                        }.build()
                        next.exchange(filtered)
                    } else {
                        next.exchange(clientRequest)
                    }
                }*/
                .filter { clientRequest, next ->
                    clientRequest
                            .headers()
                            .forEach {
                                logger.debug("Key ${it.key} Value ${it.value}")
                            }
                    next.exchange(clientRequest)
                }.exchangeStrategies(ExchangeStrategies.builder()
                        .codecs {
                            it.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)
                        }.build())

                .build()
    }

    @Bean
    fun reactorResourceFactory() = ReactorResourceFactory().apply {
        isUseGlobalResources = false
    }

    @Bean
    fun httpClient() : (HttpClient) -> HttpClient {
        return {
            it.tcpConfiguration { tcpClient ->
                tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                tcpClient.doOnConnected { conn ->
                    conn.addHandlerLast(ReadTimeoutHandler(5))
                    conn.addHandlerLast(WriteTimeoutHandler(5))
                }
            }
        }
    }

    @Bean
    @Scope("prototype")
    fun logger(injectionPoint: InjectionPoint): Logger = LogManager.getLogger(injectionPoint.methodParameter?.containingClass)
}


