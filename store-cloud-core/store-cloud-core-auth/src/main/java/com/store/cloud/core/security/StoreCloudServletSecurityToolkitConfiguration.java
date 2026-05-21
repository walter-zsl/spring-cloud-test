package com.store.cloud.core.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * {@code store-cloud-core-auth} 的<strong>总装配入口</strong>：在 <b>Servlet</b>（spring-boot-starter-web）应用里自动启用一套可复用的安全零件。
 * <p><b>谁在左、谁在右（Inbound / Outbound）</b>
 * <ul>
 *     <li><b>左侧 Inbound</b>：请求是否带 Bearer？→ {@link JwtDecoderServletConfiguration}
 *         （内含默认 {@link org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter}）
 *         + 各业务 Jar 里的 {@link org.springframework.security.config.annotation.web.configuration.EnableWebSecurity} 链。</li>
 *     <li><b>右侧 Outbound</b>：是否要把「已认证的主体」编成 JWT Token 返回？
 *         → {@link JwtEncoderServletConfiguration} + {@link StoreCloudJwtAccessTokenIssuer}。</li>
 *     <li><b>密钥与策略（贯穿左右）</b>：对称密钥 HS256 → {@link JwtSecretPolicyConfiguration}：长度、占位串等。</li>
 *     <li><b>口令哈希</b>：左侧表单/HTTP Basic（若有）解码对照 → {@link SecurityPrimitivesConfiguration}。</li>
 *     <li><b>演示账号</b>（仅极少数场景）：YAML 列出内存用户 → {@link BootstrapUserDetailsConfiguration}
 *         需 {@link StoreSecurityProperties.UserSource#bootstrap}。</li>
 * </ul>
 * <p><b>不在此装配里的</b>：Gateway（Reactive）链路；也请勿在网关工程依赖本 Starter 否则会误拉起 Servlet 专属 Bean。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(StoreSecurityProperties.class)
@Import({
    JwtSecretPolicyConfiguration.class,
    JwtEncoderServletConfiguration.class,
    JwtDecoderServletConfiguration.class,
    SecurityPrimitivesConfiguration.class,
    BootstrapUserDetailsConfiguration.class,
})
public class StoreCloudServletSecurityToolkitConfiguration {}
