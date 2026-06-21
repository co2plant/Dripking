package kr.co.inntavern.dripking.util;

import java.text.Normalizer;
import java.util.List;

public final class PlainTextSecurityUtils {
    public record Rule(String label, int maxLength, boolean multiline) {
    }

    public static final Rule REVIEW_CONTENTS = new Rule("리뷰 내용", 1200, true);
    public static final Rule REVIEW_REPORT_REASON = new Rule("신고 사유", 80, false);
    public static final Rule REVIEW_REPORT_MEMO = new Rule("신고 메모", 1000, true);
    public static final Rule TASTING_NOTE_PAIRING = new Rule("페어링", 160, false);
    public static final Rule TASTING_NOTE_MEMO = new Rule("메모", 1200, true);

    private static final List<String> SCRIPTABLE_TOKENS = List.of(
            "javascript:",
            "vbscript:",
            "data:text/html",
            "onerror=",
            "onload=",
            "onclick=",
            "onmouseover=",
            "onfocus=",
            "onmouseenter=",
            "onmouseleave=",
            "onanimationstart="
    );

    private PlainTextSecurityUtils() {
    }

    public static String validateAndNormalize(String value, Rule rule) {
        String normalizedValue = normalize(value, rule);

        if (normalizedValue.length() > rule.maxLength()) {
            throw new IllegalArgumentException(rule.label() + "은 " + rule.maxLength() + "자 이하로 입력해주세요.");
        }

        String xssReason = findXssReason(normalizedValue);
        if (!xssReason.isBlank()) {
            throw new IllegalArgumentException(rule.label() + "에 허용되지 않는 XSS 위험 패턴이 포함되어 있습니다: " + xssReason);
        }

        return normalizedValue;
    }

    public static String validateAndNormalizeOptional(String value, Rule rule) {
        if (value == null) {
            return null;
        }

        String normalizedValue = validateAndNormalize(value, rule);
        return normalizedValue.isBlank() ? null : normalizedValue;
    }

    public static String normalize(String value, Rule rule) {
        String normalizedValue = normalizeNewlines(value);
        normalizedValue = Normalizer.normalize(normalizedValue, Normalizer.Form.NFKC);
        normalizedValue = removeControlCharacters(normalizedValue, rule);
        return normalizedValue.trim();
    }

    private static String normalizeNewlines(String value) {
        if (value == null) {
            return "";
        }

        String normalizedValue = value.replace("\r\n", "\n");
        return normalizedValue.replace('\r', '\n');
    }

    private static String removeControlCharacters(String value, Rule rule) {
        StringBuilder builder = new StringBuilder(value.length());

        for (int index = 0; index < value.length(); index += 1) {
            char character = value.charAt(index);
            if (character == '\n') {
                if (rule.multiline()) {
                    builder.append(character);
                }
                continue;
            }

            if (character == '\t') {
                builder.append(' ');
                continue;
            }

            if (character >= 32) {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    private static String findXssReason(String value) {
        String lowerValue = value.toLowerCase();
        String compactedValue = compactForScan(lowerValue);

        if (hasHtmlTagStart(lowerValue)) {
            return "HTML 태그";
        }

        for (String token : SCRIPTABLE_TOKENS) {
            if (compactedValue.contains(token)) {
                return token;
            }
        }

        return "";
    }

    private static String compactForScan(String value) {
        StringBuilder builder = new StringBuilder(value.length());

        for (int index = 0; index < value.length(); index += 1) {
            char character = value.charAt(index);
            if (character > 32) {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    private static boolean hasHtmlTagStart(String value) {
        for (int index = 0; index < value.length() - 1; index += 1) {
            if (value.charAt(index) != '<') {
                continue;
            }

            int nextIndex = value.charAt(index + 1) == '/' ? index + 2 : index + 1;
            if (nextIndex < value.length() && isAsciiLetter(value.charAt(nextIndex))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAsciiLetter(char character) {
        return (character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z');
    }

}
