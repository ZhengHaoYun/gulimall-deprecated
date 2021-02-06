package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ZhengHaoYun  2021/1/14 17:06
 */
// 指明需要调用哪个服务的功能
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    //注意写全地址
    @RequestMapping("/coupon/coupon/member/list")
    R membercoupons();//得到一个R对象
}
