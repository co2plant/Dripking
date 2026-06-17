package kr.co.inntavern.dripking;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class DevSeedSqlTest {

    @Test
    void seedSqlContainsMvpCategoriesAndCountries() throws IOException {
        String sql = readDevSeedSql();

        assertThat(sql).contains("위스키", "럼", "보드카", "진", "데킬라", "브랜디", "리큐르", "맥주", "사케", "전통주");
        assertThat(sql).contains("일본", "한국", "미국");
        assertThat(sql).contains("TASTING_AROMA", "TASTING_PALATE", "TASTING_FINISH", "과일", "단맛", "긴 여운");
    }

    @Test
    void seedSqlIsSafeForPersistentPostgresVolume() throws IOException {
        String sql = readDevSeedSql();

        assertThat(sql).contains("ON CONFLICT");
        assertThat(sql).contains("setval(pg_get_serial_sequence");
    }

    @Test
    void seedSqlUsesPostgresBatchGenerationInsteadOfJavaDataLoader() throws IOException {
        String sql = readDevSeedSql();

        assertThat(sql).contains("generate_series(1, 1000)");
        assertThat(sql).contains("INSERT INTO destination");
        assertThat(sql).contains("INSERT INTO alcohol");
    }

    private String readDevSeedSql() throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("db/seed/dev-seed.sql")) {
            assertThat(inputStream).isNotNull();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
