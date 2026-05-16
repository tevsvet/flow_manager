package com.program.flow_manager.config.minio;

import com.program.flow_manager.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioInitializationConfig {

    private final MinioService minioService;

    @Bean
    public ApplicationRunner initializeDefaultBucket() {
        return args -> minioService.ensureBucketExists();
    }
}