package com.store.cloud.core.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 「配置绑定」本项目所有 Servlet 应用的鉴权相关开关与密钥（由各服务的 {@code application*.yml} 注入）。
 * <p><b>与运行时的分工（谁在左、谁在右）</b>
 * <ul>
 *     <li><b>左侧 / Inbound（请求进来）</b>：网关或客户端打到本服务 → 是否要校验 HTTP 请求里的
 *         {@code Authorization: Bearer &lt;jwt&gt;}？由 {@link JwtSettings#setValidateIncomingJwt(boolean)} /
 *         {@code store.security.jwt.validate-incoming-jwt} 控制；关闭后<strong>不会</strong>注册 {@code JwtDecoder}，
 *         避免 OAuth2 Resource Server 自动装配出第二条安全链（认证中心必须为 {@code false}）。</li>
 *     <li><b>右侧 / Outbound（响应出去）</b>：口令、客户端等校验通过后 → 是否要<strong>签发</strong> JWT 给调用方？
 *         由 {@link JwtSettings#setIssueTokens(boolean)} / {@code store.security.jwt.issue-tokens} 控制；
 *         仅 {@code store-cloud-auth} 通常为 {@code true}，其它业务 JVM 通常为 {@code false}。</li>
 *     <li><b>Bootstrap 用户</b>：仅<strong>需要做「用户名+口令」校验</strong>一侧（本项目即认证中心）；业务服务若只做 Bearer 校验一般用
 *         {@link UserSource#custom} 并不配置 {@link #bootstrapUsers}。</li>
 * </ul>
 *
 * @see org.springframework.security.crypto.factory.PasswordEncoderFactories
 */
@ConfigurationProperties(prefix = "store.security")
public class StoreSecurityProperties {

    /**
     * bootstrap：读取 {@link #bootstrapUsers}；custom：由业务模块自行注册 {@link
     * org.springframework.security.core.userdetails.UserDetailsService} Bean。
     */
    private UserSource userSource = UserSource.bootstrap;

    private JwtSettings jwt = new JwtSettings();

    private List<BootstrapAccount> bootstrapUsers = new ArrayList<>();

    public UserSource getUserSource() {
        return userSource;
    }

    public void setUserSource(UserSource userSource) {
        this.userSource = userSource;
    }

    public JwtSettings getJwt() {
        return jwt;
    }

    public void setJwt(JwtSettings jwt) {
        this.jwt = jwt;
    }

    public List<BootstrapAccount> getBootstrapUsers() {
        return bootstrapUsers;
    }

    public void setBootstrapUsers(List<BootstrapAccount> bootstrapUsers) {
        this.bootstrapUsers = bootstrapUsers;
    }

    public enum UserSource {
        /** 左侧口令源：按 YAML 里的 {@link StoreSecurityProperties#getBootstrapUsers()} 建内存账号（仅限开发/极小场景）。 */
        bootstrap,
        /** 不使用内置账号清单；由业务 Jar 自行提供 {@link org.springframework.security.core.userdetails.UserDetailsService}（或仅存 Bearer 校验、无表单登录）。 */
        custom,
    }

    public static final class JwtSettings {
        /** 对称密钥 UTF-8 至少 32 字节（HS256）；生产请用环境变量/配置中心下发 */
        private String secret = "";
        /** Access Token TTL（分钟） */
        private long expirationMinutes = 60;
        /** JWT iss claim */
        private String issuer = "store-cloud";
        /**
         * 为 true（默认）：拒绝弱占位密钥；本地可在 application-local.yml 显式关闭。
         * 上线务必保持 true，并经 {@link #secret} / JWT_SECRET 注入强随机密钥。
         */
        private boolean enforceStrongSecret = true;

        /**
         * <b>Inbound（左侧）</b>：是否注册 {@link org.springframework.security.oauth2.jwt.JwtDecoder}
         * 校验请求头里的 Bearer JWT。
         * <ul>
         *     <li>{@code true}（默认）：用户、订单等资源服务需要从认证中心拿到的 Token 放行业务接口。</li>
         *     <li>{@code false}：<strong>不</strong>注册 {@code JwtDecoder}，避免出现 OAuth2 Resource Server 的<strong>另一条</strong>
         *         安全过滤器链与你的登录链<strong>争抢</strong>——{@code store-cloud-auth} <strong>必须</strong>为 {@code false}。</li>
         * </ul>
         */
        private boolean validateIncomingJwt = true;

        /**
         * <b>Outbound（右侧）</b>：是否注册 {@link org.springframework.security.oauth2.jwt.JwtEncoder}
         * 在用户口令等校验通过后签发 JWT。
         */
        private boolean issueTokens = true;

        public boolean isValidateIncomingJwt() {
            return validateIncomingJwt;
        }

        public void setValidateIncomingJwt(boolean validateIncomingJwt) {
            this.validateIncomingJwt = validateIncomingJwt;
        }

        public boolean isIssueTokens() {
            return issueTokens;
        }

        public void setIssueTokens(boolean issueTokens) {
            this.issueTokens = issueTokens;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationMinutes() {
            return expirationMinutes;
        }

        public void setExpirationMinutes(long expirationMinutes) {
            this.expirationMinutes = expirationMinutes;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public boolean isEnforceStrongSecret() {
            return enforceStrongSecret;
        }

        public void setEnforceStrongSecret(boolean enforceStrongSecret) {
            this.enforceStrongSecret = enforceStrongSecret;
        }
    }

    public static final class BootstrapAccount {
        /** 用户名 */
        private String username;
        /** 已编码口令，形如 {noop}demo、{bcrypt}$2a$... */
        private String passwordEncoded;
        /** 角色名不含 ROLE_ 前缀（与 UserDetails#roles） */
        private List<String> roles = new ArrayList<>(List.of("USER"));

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswordEncoded() {
            return passwordEncoded;
        }

        public void setPasswordEncoded(String passwordEncoded) {
            this.passwordEncoded = passwordEncoded;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
