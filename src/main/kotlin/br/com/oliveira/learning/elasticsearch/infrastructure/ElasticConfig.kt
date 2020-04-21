package br.com.oliveira.learning.elasticsearch.infrastructure

import br.com.oliveira.learning.elasticsearch.domain.model.Coupon
import org.apache.logging.log4j.Logger
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.EntityMapper
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder
import java.net.InetAddress
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer

@Configuration
class ElasticConfig(private val logger: Logger) : ElasticsearchConfigurationSupport() {

    private var sort = false


    @Bean
    fun elasticsearchClient(): Client? {
        var client: TransportClient? = null
        try {
            val elasticsearchSettings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", "elasticsearch").build()
            client = PreBuiltTransportClient(elasticsearchSettings)
            client.addTransportAddress(TransportAddress(InetAddress.getByName("localhost"), 9300))
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return client
    }

    @Bean(name = ["elasticsearchOperations", "elasticsearchTemplate"])
    fun elasticsearchTemplate(): ElasticsearchTemplate {
        return ElasticsearchTemplate(elasticsearchClient(), entityMapper())
    }

    @Bean
    override fun entityMapper(): EntityMapper? {
        val entityMapper = ElasticsearchEntityMapper(elasticsearchMappingContext(),
                DefaultConversionService())
        entityMapper.setConversions(elasticsearchCustomConversions())
        return entityMapper
    }

    @Bean("start")
    fun start(@Qualifier("elasticsearchOperations") elasticsearchOperations: ElasticsearchOperations) : Runnable{
        return Runnable {
            val list: Collection<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            list.forEach(Consumer { idCoupon: Int ->
                val coupon = Coupon(couponIdt = idCoupon,
                campaignIdt = if (sort) 1 else 2,
                couponDesTitle = "Coupon $idCoupon",
                couponDatStartExhibition = createDate("10-02-2020"),
                couponDatEndExhibition = if (sort) createDate("30-04-2020") else createDate("30-05-2020"),
                couponIndCouponType = if(sort) "C" else "S" ,
                campaignDatStart = createDate("10-02-2020"),
                campaignDatEnd = if (sort) createDate("30-04-2020") else createDate("30-05-2020"))
                sort = !sort

                val indexQuery = IndexQueryBuilder()
                        .withId(coupon.id)
                        .withObject(coupon)
                        .build()
                elasticsearchOperations.index(indexQuery)
                logger.info("Coupoun ${coupon.couponDesTitle} adicionado.")
            })
        }
    }

    @Throws(ParseException::class)
    fun createDate(date: String): Date {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        return formatter.parse(date)
    }
}