package com.lyml.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Value("${path.upload}")
    private String path;//获取yml文件中的值

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //String os = System.getProperty("os.name");//获取操作系统类型 win linux mac

        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/upload/**").addResourceLocations("file:"+path);//配置静态资源，将upload解析为本地C:\\upload
    }
}
