package com.store.cloud.order.api.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.store.cloud.core.response.api.ApiEnvelope;
import com.store.cloud.order.api.dto.OrderView;

/**
 * 调用订单服务列表；一般需携带 JWT，内部调用可置空 Authorization（由目标服务安全策略决定）。
 */
@FeignClient(name = "store-order-service", contextId = "orderQueryFeignClient", path = "/api/v1/orders")
public interface OrderQueryFeignClient {

    @GetMapping
    ApiEnvelope<List<OrderView>> list(@RequestHeader(name = "Authorization", required = false) String authorization);
}
