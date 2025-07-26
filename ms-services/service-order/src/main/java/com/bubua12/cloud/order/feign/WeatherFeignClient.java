package com.bubua12.cloud.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * fixme 这里url使用https的为啥不行？
 *
 * @author bubua12
 * @since 2025/7/26 16:15
 */
@FeignClient(value = "weather-client", url = "http://aliv18.data.moji.com")
public interface WeatherFeignClient {

    /**
     *
     * @param auth 放到请求头上，并且请求头叫Authorization
     * @param cityId 放到请求参数上
     * @param token 放到请求参数上
     */
    @PostMapping(value = "/whapi/json/alicityweather/condition")
    String getWeather(@RequestHeader("Authorization") String auth,
                      @RequestParam("cityId") String cityId,
                      @RequestParam("token") String token);
}
