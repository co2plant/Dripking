package kr.co.inntavern.dripking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.storage.s3")
public class S3StorageProperties {
    private String endpoint;
    private String region;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private String publicBaseUrl;
    private String keyPrefix = "content-images";

    public boolean isConfigured() {
        return hasText(region) && hasText(bucket) && hasText(accessKey) && hasText(secretKey);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
