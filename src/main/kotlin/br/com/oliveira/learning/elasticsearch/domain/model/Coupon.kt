package br.com.oliveira.learning.elasticsearch.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.util.*

@Document(indexName = "dojo", type = "coupon")
class Coupon(@Field(value = "coupon_idt", type = FieldType.Keyword)
             private var couponIdt: Int,
             @Field(value = "campaign_idt",type = FieldType.Integer)
             var campaignIdt: Int,
             @Field(value = "coupon_des_title" , type = FieldType.Text)
             var couponDesTitle: String,
             @Field(value = "coupon_dat_start_exhibition", type = FieldType.Date)
             var couponDatStartExhibition: Date,
             @Field(value = "coupon_dat_end_exhibition", type = FieldType.Date)
             var couponDatEndExhibition: Date,
             @Field(value = "coupon_ind_coupon_type", type = FieldType.Text)
             var couponIndCouponType: String,
             @Field(value = "campaign_dat_start", type = FieldType.Date)
             var campaignDatStart: Date,
             @Field(value = "campaign_dat_end", type = FieldType.Date)
             var campaignDatEnd: Date
){
    @Id
    var id: String? = null
}