package com.store.cloud.core.response.api;

/**
 * <b>全项目可调</b>：成功外层 {@code code} 的默认值。
 * <p>
 * {@link #SUCCESS_CODE_REST} 为数字串 {@value #SUCCESS_CODE_REST}，与同仓库约定的「正确」码段
 *下限 {@value #SUCCESS_TIER_MIN_INCLUSIVE} 对齐（与 core-web 侧 {@code ErrorCodes} 语义一致）。
 * {@link #SUCCESS_TIER_MIN_INCLUSIVE}&nbsp;≤&nbsp;{@code Integer.parseInt(code)}&nbsp;&lt;&nbsp;
 *{@value #SUCCESS_TIER_ERROR_BOUND_EXCLUSIVE} 时视为外层成功语义。
 */
public final class ApiEnvelopeConstants {

    private ApiEnvelopeConstants() {}

    /**
     * 成功码区间下限（含）。与全局 {@code ErrorCodes.SUCCESS}（默认 {@link #SUCCESS_CODE_REST}）同段含义。
     */
    public static final int SUCCESS_TIER_MIN_INCLUSIVE = 20000;

    /**
     * 成功区间上界（不含）；达到该值及以上的数字码视为<strong>错误</strong>语义（与外层 {@link #SUCCESS_CODE_REST}
     *无关：仍可由 {@link ApiEnvelope#success} 传入任意字符串 {@code code}）。
     */
    public static final int SUCCESS_TIER_ERROR_BOUND_EXCLUSIVE = 40000;

    /** 与全局「通用成功」数字码对齐（JSON 中仍为字符串字段 {@code code}） */
    public static final String SUCCESS_CODE_REST =
            Integer.toString(SUCCESS_TIER_MIN_INCLUSIVE);

    /** 常见「{@code code=0} 表示成功」（兼容旧客户端） */
    public static final String SUCCESS_CODE_LEGACY_ZERO = "0";

    public static final String MESSAGE_SUCCESS_DEFAULT = "success";

    public static final String MESSAGE_OK = "OK";
}
