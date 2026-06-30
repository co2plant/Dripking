package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.config.CourseGenerationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CaptchaVerificationService {
    private final CourseGenerationProperties properties;
    private final RestTemplate restTemplate;

    @Autowired
    public CaptchaVerificationService(CourseGenerationProperties properties) {
        this(properties, new RestTemplate());
    }

    CaptchaVerificationService(CourseGenerationProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public void verify(String captchaToken) {
        if (!properties.getCaptcha().isEnabled()) {
            return;
        }
        String token = trimToNull(captchaToken);
        if (token == null) {
            throw CourseGenerationGateException.captchaRequired();
        }
        if (matchesDevToken(token)) {
            return;
        }
        if (trimToNull(properties.getCaptcha().getSecret()) == null) {
            throw CourseGenerationGateException.captchaFailed();
        }
        verifyWithProvider(token);
    }

    private boolean matchesDevToken(String token) {
        String devToken = trimToNull(properties.getCaptcha().getDevToken());
        return devToken != null && devToken.equals(token);
    }

    private void verifyWithProvider(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", properties.getCaptcha().getSecret());
        body.add("response", token);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    properties.getCaptcha().getVerifyUrl(),
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            Object success = response.getBody() == null ? null : response.getBody().get("success");
            if (!Boolean.TRUE.equals(success)) {
                throw CourseGenerationGateException.captchaFailed();
            }
        } catch (RestClientException exception) {
            throw CourseGenerationGateException.captchaFailed();
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
