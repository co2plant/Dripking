package kr.co.inntavern.dripking.service;

import com.sun.net.httpserver.HttpServer;
import kr.co.inntavern.dripking.config.S3StorageProperties;
import kr.co.inntavern.dripking.dto.response.ContentImageUploadResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentImageUploadServiceTest {
    private static final byte[] PNG_BYTES = new byte[] {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
    };

    @Test
    void uploadStoresImageAndReturnsUrlAndObjectKey() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicReference<String> requestMethod = new AtomicReference<>();
        AtomicReference<String> requestPath = new AtomicReference<>();
        AtomicReference<String> authorizationHeader = new AtomicReference<>();
        server.createContext("/", exchange -> {
            requestMethod.set(exchange.getRequestMethod());
            requestPath.set(exchange.getRequestURI().getPath());
            authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        try {
            S3StorageProperties properties = configuredProperties(
                    "http://127.0.0.1:" + server.getAddress().getPort(),
                    "https://cdn.example.com/dripking"
            );
            ContentImageUploadService service = new ContentImageUploadService(properties);
            MockMultipartFile file = new MockMultipartFile("file", "destination.png", "image/png", PNG_BYTES);

            ContentImageUploadResponseDTO response = service.upload(file, ItemType.DESTINATION);

            assertThat(requestMethod.get()).isEqualTo("PUT");
            assertThat(requestPath.get()).startsWith("/dripking-images/content-images/destination/");
            assertThat(authorizationHeader.get()).startsWith("AWS4-HMAC-SHA256 Credential=test-access/");
            assertThat(response.getItemType()).isEqualTo("DESTINATION");
            assertThat(response.getImgObjectKey()).startsWith("content-images/destination/");
            assertThat(response.getImgObjectKey()).endsWith(".png");
            assertThat(response.getImgUrl()).startsWith("https://cdn.example.com/dripking/content-images/destination/");
            assertThat(response.getContentType()).isEqualTo("image/png");
            assertThat(response.getSize()).isEqualTo(PNG_BYTES.length);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void uploadUsesEndpointUrlAndCustomKeyPrefixWhenPublicBaseUrlIsBlank() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        try {
            String endpoint = "http://127.0.0.1:" + server.getAddress().getPort();
            S3StorageProperties properties = configuredProperties(endpoint, " ");
            properties.setKeyPrefix("/custom-content/");
            ContentImageUploadService service = new ContentImageUploadService(properties);
            MockMultipartFile file = new MockMultipartFile("file", "alcohol.png", "image/png", PNG_BYTES);

            ContentImageUploadResponseDTO response = service.upload(file, ItemType.ALCOHOL);

            assertThat(response.getImgObjectKey()).startsWith("custom-content/alcohol/");
            assertThat(response.getImgUrl()).startsWith(endpoint + "/dripking-images/custom-content/alcohol/");
        } finally {
            server.stop(0);
        }
    }

    @Test
    void uploadRejectsMissingStorageConfiguration() {
        ContentImageUploadService service = new ContentImageUploadService(new S3StorageProperties());
        MockMultipartFile file = new MockMultipartFile("file", "alcohol.png", "image/png", PNG_BYTES);

        ContentImageUploadException exception = assertThrows(
                ContentImageUploadException.class,
                () -> service.upload(file, ItemType.ALCOHOL)
        );

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void uploadRejectsUnsupportedImageType() {
        S3StorageProperties properties = configuredProperties("http://127.0.0.1:1", "");
        ContentImageUploadService service = new ContentImageUploadService(properties);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "distillery.gif",
                "image/gif",
                "GIF89a".getBytes(StandardCharsets.UTF_8)
        );

        ContentImageUploadException exception = assertThrows(
                ContentImageUploadException.class,
                () -> service.upload(file, ItemType.DISTILLERY)
        );

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private S3StorageProperties configuredProperties(String endpoint, String publicBaseUrl) {
        S3StorageProperties properties = new S3StorageProperties();
        properties.setEndpoint(endpoint);
        properties.setRegion("ap-northeast-2");
        properties.setBucket("dripking-images");
        properties.setAccessKey("test-access");
        properties.setSecretKey("test-secret");
        properties.setPublicBaseUrl(publicBaseUrl);
        properties.setKeyPrefix("content-images");
        return properties;
    }
}
