package com.store.cloud.order.api.dto;

/** 订单列表项（与订单服务 REST 返回结构一致）。 */
public record OrderView(String orderId, String title) {}
