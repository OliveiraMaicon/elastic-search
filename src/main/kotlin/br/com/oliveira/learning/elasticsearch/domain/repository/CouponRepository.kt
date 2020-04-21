package br.com.oliveira.learning.elasticsearch.domain.repository

import br.com.oliveira.learning.elasticsearch.domain.model.Coupon
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository

interface CouponRepository : ElasticsearchCrudRepository<Coupon, Int> {

    fun findByCouponIdt(couponIdt : Int): Coupon

    @Query("{\"bool\":{\"must\":[{\"range\":{\"coupon_dat_start_exhibition\":{\"lte\": \"?1\"}}},{\"range\":{\"coupon_dat_end_exhibition\":{\"gte\": \"?1\"}}},{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"match\":{\"coupon_ind_coupon_type\":\"S\"}},{\"range\":{\"campaign_dat_start\":{\"lte\": \"?1\"}}},{\"range\":{\"campaign_dat_end\":{\"gte\": \"?1\"}}}]}},{\"match\":{\"coupon_ind_coupon_type\":\"C\"}},{\"match\":{\"coupon_ind_coupon_type\":\"E\"}}]}}]}}")
    fun findCouponInProgress(now: Long?): List<Coupon>

    @Query("{\"bool\":{\"must\":[{\"match\":{\"campaign_idt\": \"?0\"}},{\"range\":{\"coupon_dat_start_exhibition\":{\"lte\": \"?1\"}}},{\"range\":{\"coupon_dat_end_exhibition\":{\"gte\": \"?1\"}}},{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"match\":{\"coupon_ind_coupon_type\":\"S\"}},{\"range\":{\"campaign_dat_start\":{\"lte\": \"?1\"}}},{\"range\":{\"campaign_dat_end\":{\"gte\": \"?1\"}}}]}},{\"match\":{\"coupon_ind_coupon_type\":\"C\"}},{\"match\":{\"coupon_ind_coupon_type\":\"E\"}}]}}]}}")
    fun findCouponInProgressByCampaignIdt(campaignIdt: Int, now: Long): List<Coupon>
}