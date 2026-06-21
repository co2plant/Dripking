package kr.co.inntavern.dripking;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class DevSeedSqlTest {

    @Test
    void baseSeedSqlContainsSharedLookups() throws IOException {
        String sql = readResource("db/seed/base-seed.sql");

        assertThat(sql).contains("위스키", "럼", "보드카", "진", "데킬라", "브랜디", "리큐르", "맥주", "사케", "전통주", "쇼추/아와모리");
        assertThat(sql).contains("일본", "한국", "미국");
        assertThat(sql).contains("TASTING_AROMA", "TASTING_PALATE", "TASTING_FINISH", "과일", "단맛", "긴 여운");
        assertThat(sql).contains("ZZZ Dummy Alcohol");
    }

    @Test
    void realJapanSeedSqlContainsCuratedCatalogWithoutSyntheticRows() throws IOException {
        String sql = readResource("db/seed/real-japan-seed.sql");

        assertThat(sql).contains("Yamazaki Distillery", "Hakushu Distillery", "Yoichi Distillery", "Gekkeikan Okura Sake Museum");
        assertThat(sql).contains("Yamazaki 12 Year Old", "Dassai 45", "Hitachino Nest White Ale", "Nakanaka");
        assertThat(sql).doesNotContain("generate_series");
        assertThat(sql).doesNotContain("Perf Alcohol");
    }

    @Test
    void perfSeedSqlOwnsSyntheticBatchGeneration() throws IOException {
        String sql = readResource("db/seed/perf-seed.sql");

        assertThat(sql).contains("generate_series(1, 100000)");
        assertThat(sql).contains("Perf Destination", "Perf Distillery", "ZZZ Dummy Alcohol");
        assertThat(sql).contains("INSERT INTO destination");
        assertThat(sql).contains("INSERT INTO alcohol");
    }

    @Test
    void devAndPerfProfilesLoadDifferentSeedSets() throws IOException {
        String devProperties = readResource("application-dev.properties");
        String perfProperties = readResource("application-perf.properties");

        assertThat(devProperties).contains("db/seed/base-seed.sql", "db/seed/real-japan-seed.sql");
        assertThat(devProperties).doesNotContain("db/seed/perf-seed.sql");
        assertThat(perfProperties).contains("db/seed/base-seed.sql", "db/seed/real-japan-seed.sql", "db/seed/perf-seed.sql");
        assertThat(perfProperties).doesNotContain("spring.config.import");
    }

    @Test
    void seedSqlFilesAreSafeForPersistentPostgresVolume() throws IOException {
        for (String resource : new String[]{"db/seed/base-seed.sql", "db/seed/real-japan-seed.sql", "db/seed/perf-seed.sql"}) {
            String sql = readResource(resource);

            assertThat(sql).contains("ON CONFLICT");
            assertThat(sql).contains("setval(pg_get_serial_sequence");
        }
    }

    private String readResource(String resourceName) throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(inputStream).isNotNull();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
