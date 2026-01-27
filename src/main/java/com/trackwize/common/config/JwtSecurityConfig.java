package com.trackwize.common.config;

public final class JwtSecurityConfig {

    private JwtSecurityConfig() {}

    /**
     * true  -> JWT stored in Cookie
     * false -> JWT in Authorization header
     */
    public static final boolean USE_COOKIE = true;

    /**
     * true  -> cookie.setSecure(true) (production / local with https)
     * false -> cookie.setSecure(false) (localhost / HTTP)
     */
    public static final boolean COOKIE_SECURE = false;
}
