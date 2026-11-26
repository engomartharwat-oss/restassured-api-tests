package com.omartharwat.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads config.properties. A value can be overridden with -Dkey=value. */
public final class ApiConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ApiConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            PROPS.load(in);
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
    }

    private ApiConfig() {
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        return override != null ? override : PROPS.getProperty(key);
    }

    public static long getLong(String key) {
        return Long.parseLong(get(key).trim());
    }
}
