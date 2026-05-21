package com.store.cloud.order.service;

import java.util.List;

import com.store.cloud.order.api.dto.OrderView;

public interface OrderService {

    List<OrderView> listOrders();
}
