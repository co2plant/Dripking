package kr.co.inntavern.dripking.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableUtils {
    private static final String DEFAULT_SORT_PROPERTY = "id";

    private PageableUtils() {
    }

    public static Pageable pageRequest(int page, int size, String sortParam) {
        Sort sort = parseSort(sortParam);
        return PageRequest.of(page, size, sort);
    }

    private static Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.DESC, DEFAULT_SORT_PROPERTY);
        }

        String[] parts = sortParam.split(",");
        if (parts.length == 1) {
            Sort.Direction direction = Sort.Direction.fromOptionalString(parts[0])
                    .orElse(Sort.Direction.DESC);
            String property = Sort.Direction.fromOptionalString(parts[0]).isPresent()
                    ? DEFAULT_SORT_PROPERTY
                    : parts[0].trim();

            return Sort.by(direction, property);
        }

        String property = parts[0].trim();
        Sort.Direction direction = Sort.Direction.fromOptionalString(parts[1])
                .orElse(Sort.Direction.DESC);

        return Sort.by(direction, property.isBlank() ? DEFAULT_SORT_PROPERTY : property);
    }
}
