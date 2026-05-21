package com.store.cloud.core.response.api;

/**
 * <b>全项目可调</b>：成功外层 {@code code} 的默认值。
 * <p>若甲方要求 {@code "0"} / {@code "200"} 表示成功：改常量或改用 {@link ApiEnvelope#success(Boolean, String, String, Object, String, java.util.Map)}。</p>
 */
public final class ApiEnvelopeConstants {

    private ApiEnvelopeConstants() {}

    /** REST/内部常用：{@code OK} */
    public static final String SUCCESS_CODE_REST = "OK";

    /** 常见「{@code code=0} 表示成功」 */
    public static final String SUCCESS_CODE_LEGACY_ZERO = "0";

    public static final String MESSAGE_SUCCESS_DEFAULT = "success";

    public static final String MESSAGE_OK = "OK";
}
