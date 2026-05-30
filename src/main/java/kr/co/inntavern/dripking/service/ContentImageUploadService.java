package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.config.S3StorageProperties;
import kr.co.inntavern.dripking.dto.response.ContentImageUploadResponseDTO;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ContentImageUploadService {
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final DateTimeFormatter DATE_STAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter AMZ_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter KEY_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneOffset.UTC);

    private final S3StorageProperties properties;
    private final HttpClient httpClient;

    public ContentImageUploadService(S3StorageProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public ContentImageUploadResponseDTO upload(MultipartFile file, ItemType itemType) {
        validateItemType(itemType);
        validateStorageConfiguration();
        validateFile(file);

        byte[] bytes = readBytes(file);
        String extension = resolveExtension(file, bytes);
        String contentType = contentTypeForExtension(extension);
        String objectKey = buildObjectKey(itemType, extension);
        URI putUri = buildPutUri(objectKey);

        putObject(putUri, bytes, contentType);

        return new ContentImageUploadResponseDTO(
                itemType.name(),
                objectKey,
                publicUrlFor(objectKey, putUri),
                contentType,
                bytes.length
        );
    }

    private void validateItemType(ItemType itemType) {
        if (itemType != ItemType.ALCOHOL && itemType != ItemType.DISTILLERY && itemType != ItemType.DESTINATION) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "이미지를 업로드할 수 없는 콘텐츠 타입입니다.");
        }
    }

    private void validateStorageConfiguration() {
        if (!properties.isConfigured()) {
            throw new ContentImageUploadException(HttpStatus.SERVICE_UNAVAILABLE, "S3 이미지 저장소 설정이 필요합니다.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "업로드할 이미지 파일이 필요합니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "이미지 파일은 5MB 이하여야 합니다.");
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "이미지 파일을 읽을 수 없습니다.", exception);
        }
    }

    private String resolveExtension(MultipartFile file, byte[] bytes) {
        String extension = extensionFromFilename(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "jpg, jpeg, png, webp 이미지만 업로드할 수 있습니다.");
        }

        String canonicalExtension = extension.equals("jpeg") ? "jpg" : extension;
        String contentType = file.getContentType();
        String expectedContentType = contentTypeForExtension(canonicalExtension);
        if (contentType != null && !contentType.isBlank() && !expectedContentType.equals(contentType.toLowerCase(Locale.ROOT))) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "이미지 파일 형식과 Content-Type이 일치하지 않습니다.");
        }
        if (!matchesMagicBytes(canonicalExtension, bytes)) {
            throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 파일 형식입니다.");
        }

        return canonicalExtension;
    }

    private String extensionFromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private boolean matchesMagicBytes(String extension, byte[] bytes) {
        if ("jpg".equals(extension)) {
            return bytes.length >= 3
                    && (bytes[0] & 0xff) == 0xff
                    && (bytes[1] & 0xff) == 0xd8
                    && (bytes[2] & 0xff) == 0xff;
        }
        if ("png".equals(extension)) {
            return bytes.length >= 8
                    && (bytes[0] & 0xff) == 0x89
                    && bytes[1] == 0x50
                    && bytes[2] == 0x4e
                    && bytes[3] == 0x47
                    && bytes[4] == 0x0d
                    && bytes[5] == 0x0a
                    && bytes[6] == 0x1a
                    && bytes[7] == 0x0a;
        }
        return bytes.length >= 12
                && bytes[0] == 0x52
                && bytes[1] == 0x49
                && bytes[2] == 0x46
                && bytes[3] == 0x46
                && bytes[8] == 0x57
                && bytes[9] == 0x45
                && bytes[10] == 0x42
                && bytes[11] == 0x50;
    }

    private String contentTypeForExtension(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> throw new ContentImageUploadException(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 파일 형식입니다.");
        };
    }

    private String buildObjectKey(ItemType itemType, String extension) {
        String prefix = normalizeKeyPrefix(properties.getKeyPrefix());
        String typeSegment = itemType.name().toLowerCase(Locale.ROOT);
        String dateSegment = KEY_DATE_FORMATTER.format(Instant.now());
        return prefix + "/" + typeSegment + "/" + dateSegment + "/" + UUID.randomUUID() + "." + extension;
    }

    private String normalizeKeyPrefix(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isBlank()) {
            return "content-images";
        }
        return keyPrefix.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private URI buildPutUri(String objectKey) {
        String endpoint = properties.getEndpoint();
        String baseEndpoint = endpoint == null || endpoint.isBlank()
                ? "https://s3." + properties.getRegion() + ".amazonaws.com"
                : endpoint.replaceAll("/+$", "");
        return URI.create(baseEndpoint + "/" + properties.getBucket() + "/" + objectKey);
    }

    private void putObject(URI uri, byte[] bytes, String contentType) {
        Instant now = Instant.now();
        String payloadHash = hex(sha256(bytes));
        String amzDate = AMZ_DATE_FORMATTER.format(now);
        String dateStamp = DATE_STAMP_FORMATTER.format(now);
        String authorization = buildAuthorizationHeader(uri, contentType, payloadHash, amzDate, dateStamp);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", contentType)
                .header("X-Amz-Content-Sha256", payloadHash)
                .header("X-Amz-Date", amzDate)
                .header("Authorization", authorization)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ContentImageUploadException(HttpStatus.BAD_GATEWAY, "S3 이미지 업로드에 실패했습니다.");
            }
        } catch (IOException exception) {
            throw new ContentImageUploadException(HttpStatus.BAD_GATEWAY, "S3 이미지 저장소에 연결할 수 없습니다.", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ContentImageUploadException(HttpStatus.BAD_GATEWAY, "S3 이미지 업로드가 중단되었습니다.", exception);
        }
    }

    private String buildAuthorizationHeader(URI uri, String contentType, String payloadHash, String amzDate, String dateStamp) {
        String credentialScope = dateStamp + "/" + properties.getRegion() + "/s3/aws4_request";
        String signedHeaders = "content-type;host;x-amz-content-sha256;x-amz-date";
        String canonicalRequest = String.join("\n",
                "PUT",
                uri.getRawPath(),
                "",
                "content-type:" + contentType,
                "host:" + hostHeader(uri),
                "x-amz-content-sha256:" + payloadHash,
                "x-amz-date:" + amzDate,
                "",
                signedHeaders,
                payloadHash
        );
        String stringToSign = String.join("\n",
                "AWS4-HMAC-SHA256",
                amzDate,
                credentialScope,
                hex(sha256(canonicalRequest.getBytes(StandardCharsets.UTF_8)))
        );
        byte[] signingKey = signingKey(dateStamp);
        String signature = hex(hmac(signingKey, stringToSign));
        return "AWS4-HMAC-SHA256 Credential=" + properties.getAccessKey() + "/" + credentialScope
                + ", SignedHeaders=" + signedHeaders
                + ", Signature=" + signature;
    }

    private String hostHeader(URI uri) {
        int port = uri.getPort();
        boolean defaultPort = port == -1
                || ("http".equals(uri.getScheme()) && port == 80)
                || ("https".equals(uri.getScheme()) && port == 443);
        return defaultPort ? uri.getHost() : uri.getHost() + ":" + port;
    }

    private byte[] signingKey(String dateStamp) {
        byte[] dateKey = hmac(("AWS4" + properties.getSecretKey()).getBytes(StandardCharsets.UTF_8), dateStamp);
        byte[] regionKey = hmac(dateKey, properties.getRegion());
        byte[] serviceKey = hmac(regionKey, "s3");
        return hmac(serviceKey, "aws4_request");
    }

    private byte[] sha256(byte[] value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    private byte[] hmac(byte[] key, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new IllegalStateException("HmacSHA256 is not available.", exception);
        }
    }

    private String hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value & 0xff));
        }
        return builder.toString();
    }

    private String publicUrlFor(String objectKey, URI putUri) {
        String publicBaseUrl = properties.getPublicBaseUrl();
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            return putUri.toString();
        }
        return publicBaseUrl.replaceAll("/+$", "") + "/" + objectKey;
    }
}
