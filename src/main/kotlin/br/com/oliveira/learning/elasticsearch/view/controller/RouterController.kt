package br.com.oliveira.learning.elasticsearch.view.controller

import br.com.oliveira.learning.elasticsearch.domain.model.Coupon
import br.com.oliveira.learning.elasticsearch.domain.repository.CouponRepository
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.GetQuery
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@Component
@Order(-1)
class RouterController(@Qualifier("elasticsearchOperations")
                       private val elasticsearchOperations: ElasticsearchOperations,
                       @Qualifier("start")
                       private val start: Runnable,
                       private val couponRepository: CouponRepository,
                       private val logger: Logger) {


    /* @Bean
     fun routes() = RouterFunctions.route(
             RequestPredicates.GET("/elastic"),
             HandlerFunction {
                 ServerResponse.accepted().bodyValue("OK")
             }
     )*/

    @Bean
    fun route() = router {
        GET("/elastic") { ServerResponse.ok().body(Mono.just("OK")) }

        POST("/elastic") {
            start.run()
            ServerResponse.ok().body(Mono.just("Objetos inseridos"))
        }

        GET("/elastic/coupon/{id}") {
            val id = it.pathVariable("id")
            val coupon: Coupon = couponRepository.findByCouponIdt(id.toInt())
            ServerResponse.ok().body(Mono.just(coupon))
        }


        GET("/elastic/search/coupon/campaign/{id}/inprogress") {
            val id = it.pathVariable("id")
            val now = Date().time
            val coupons: List<Coupon> = couponRepository.findCouponInProgressByCampaignIdt(id.toInt(), now)
            ServerResponse.ok().body(Flux.just(coupons))
        }

        GET("/elastic/search/coupon/campaign/inprogress") {
            val now = Date().time
            val coupons: List<Coupon> = couponRepository.findCouponInProgress(now)
            ServerResponse.ok().body(Flux.just(coupons))
        }

    }
}