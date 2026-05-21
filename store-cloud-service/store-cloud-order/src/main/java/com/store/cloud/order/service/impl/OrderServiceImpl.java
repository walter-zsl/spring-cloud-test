package com.store.cloud.order.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.store.cloud.order.api.dto.OrderView;
import com.store.cloud.order.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public List<OrderView> listOrders() {
        return List.of(
                new OrderView("ORD-001", "示例订单 A"),
                new OrderView("ORD-002", "示例订单 B"));
    }
}
