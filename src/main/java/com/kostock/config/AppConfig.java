package com.kostock.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader()
                                             .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties 파일을 찾을 수 없습니다.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("config.properties 로딩 실패", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
