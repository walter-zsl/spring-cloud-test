package com.store.cloud.core.response.api;

import java.util.List;

/** 分页数据块，常作为 {@link ApiEnvelope#data()} */
public record PagedPayload<T>(List<T> records, PageMeta meta) {

    public static <T> PagedPayload<T> of(List<T> records, PageMeta meta) {
        List<T> list = records != null ? List.copyOf(records) : List.of();
        return new PagedPayload<>(list, meta);
    }

    public static <T> PagedPayload<T> of(List<T> records, long total, int pageNum, int pageSize) {
        return of(records, PageMeta.of(total, pageNum, pageSize));
    }
}
