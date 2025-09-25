package cn.mairuf.shizhong.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API版本配置
 *
 * @author 阿麦
 * @date 2025-09-25
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    @Value("${api.version.prefix:/api/v1}")
    private String apiVersionPrefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(apiVersionPrefix,
                c -> c.isAnnotationPresent(RestController.class));
    }
}