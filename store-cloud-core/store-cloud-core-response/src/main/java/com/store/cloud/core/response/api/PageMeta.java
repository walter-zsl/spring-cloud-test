package com.store.cloud.core.response.api;

/**
 * 分页元数据。
 *
 * @param total      符合条件总条数
 * @param pageNum    当前页（从 {@code 1} 起）
 * @param pageSize   每页大小
 * @param totalPages 总页数
 */
public record PageMeta(long total, int pageNum, int pageSize, long totalPages) {

    public static PageMeta of(long total, int pageNum, int pageSize) {
        int pn = pageNum < 1 ? 1 : pageNum;
        int ps = pageSize < 1 ? 10 : pageSize;
        long pages = total <= 0 ? 0 : (total + ps - 1) / ps;
        return new PageMeta(total, pn, ps, pages);
    }
}
