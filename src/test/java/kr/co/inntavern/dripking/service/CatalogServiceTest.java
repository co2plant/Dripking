package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.AlcoholResponseDTO;
import kr.co.inntavern.dripking.dto.response.DestinationResponseDTO;
import kr.co.inntavern.dripking.dto.response.DistilleryResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.City;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.CityRepository;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class CatalogServiceTest {

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private DistilleryService distilleryService;

    @Autowired
    private AlcoholService alcoholService;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private DistilleryRepository distilleryRepository;

    @Autowired
    private AlcoholRepository alcoholRepository;

    @Test
    void destinationCountryFilterUsesCityCountryAndReturnsDtoFields() {
        Category category = saveCategory("위스키");
        Country japan = saveCountry("일본");
        Country korea = saveCountry("한국");
        City osaka = saveCity("오사카", japan);
        City seoul = saveCity("서울", korea);
        Destination japanDestination = saveDestination("Yamazaki Area", osaka, category, 34.9d, 135.6d);
        saveDestination("Seoul Bar Area", seoul, category, 37.5d, 127.0d);

        assertThat(destinationService.getAllDestinationsByCountryId(0, 10, "ASC", japan.getId()).getContent())
                .extracting(DestinationResponseDTO::getName)
                .containsExactly("Yamazaki Area");

        DestinationResponseDTO detail = destinationService.getDestinationById(japanDestination.getId());
        assertThat(detail.getId()).isEqualTo(japanDestination.getId());
        assertThat(detail.getCountryId()).isEqualTo(japan.getId());
        assertThat(detail.getCountryName()).isEqualTo("일본");
        assertThat(detail.getCityId()).isEqualTo(osaka.getId());
        assertThat(detail.getCityName()).isEqualTo("오사카");
        assertThat(detail.getCategoryId()).isEqualTo(category.getId());
        assertThat(detail.getLatitude()).isEqualTo(34.9d);
        assertThat(detail.getLongitude()).isEqualTo(135.6d);
    }

    @Test
    void distilleryDestinationFilterAndAlcoholDistilleryFilterReturnDtoFields() {
        Category whisky = saveCategory("위스키");
        Category beer = saveCategory("맥주");
        Country japan = saveCountry("일본");
        City osaka = saveCity("오사카", japan);
        Destination destination = saveDestination("Yamazaki Area", osaka, whisky, 34.9d, 135.6d);
        Destination otherDestination = saveDestination("Other Area", osaka, whisky, 35.0d, 136.0d);
        Distillery yamazaki = saveDistillery("Yamazaki Distillery", destination);
        saveDistillery("Other Distillery", otherDestination);
        Alcohol singleMalt = saveAlcohol("Single Malt", whisky, yamazaki);
        saveAlcohol("Local Beer", beer, yamazaki);

        assertThat(distilleryService.getAllDistilleriesByDestinationId(0, 10, "ASC", destination.getId()).getContent())
                .extracting(DistilleryResponseDTO::getName)
                .containsExactly("Yamazaki Distillery");

        assertThat(alcoholService.getAllAlcoholsByDistilleryId(0, 10, "ASC", yamazaki.getId()).getContent())
                .extracting(AlcoholResponseDTO::getName)
                .containsExactly("Single Malt", "Local Beer");

        assertThat(alcoholService.getAllAlcoholsByCategoryId(0, 10, "ASC", whisky.getId()).getContent())
                .extracting(AlcoholResponseDTO::getName)
                .containsExactly("Single Malt");

        DistilleryResponseDTO distilleryDetail = distilleryService.getDistilleryById(yamazaki.getId());
        assertThat(distilleryDetail.getDestinationId()).isEqualTo(destination.getId());
        assertThat(distilleryDetail.getAddress()).isEqualTo("Yamazaki Distillery address");

        AlcoholResponseDTO alcoholDetail = alcoholService.getAlcoholById(singleMalt.getId());
        assertThat(alcoholDetail.getCategoryId()).isEqualTo(whisky.getId());
        assertThat(alcoholDetail.getDistilleryId()).isEqualTo(yamazaki.getId());
        assertThat(alcoholDetail.getImgUrl()).isEqualTo("https://example.com/Single Malt.jpg");
    }

    @Test
    void markerQueriesOmitCoordinateMissingItemsButListRowsRemainAvailable() {
        Category whisky = saveCategory("위스키");
        Country japan = saveCountry("일본");
        City osaka = saveCity("오사카", japan);
        Destination markerDestination = saveDestination("Marker destination", osaka, whisky, 34.9d, 135.6d);
        Destination missingLatitudeDestination = saveDestination("Missing latitude destination", osaka, whisky, null, 135.7d);
        Destination missingLongitudeDestination = saveDestination("Missing longitude destination", osaka, whisky, 35.0d, null);
        saveDistillery("Marker distillery", markerDestination);
        saveDistillery("Missing latitude distillery", markerDestination, null, 135.5d);
        saveDistillery("Missing longitude distillery", markerDestination, 34.8d, null);

        assertThat(destinationService.getDestinationMarkers())
                .extracting(DestinationResponseDTO::getName)
                .containsExactly("Marker destination");
        assertThat(distilleryService.getDistilleryMarkers())
                .extracting(DistilleryResponseDTO::getName)
                .containsExactly("Marker distillery");

        assertThat(destinationService.getAllDestinations(0, 10, "ASC").getContent())
                .extracting(DestinationResponseDTO::getId)
                .contains(
                        markerDestination.getId(),
                        missingLatitudeDestination.getId(),
                        missingLongitudeDestination.getId()
                );
    }

    private Category saveCategory(String name) {
        return categoryRepository.save(Category.builder()
                .name(name)
                .description(name + " category")
                .build());
    }

    private Country saveCountry(String name) {
        return countryRepository.save(Country.builder()
                .name(name)
                .description(name + " country")
                .build());
    }

    private City saveCity(String name, Country country) {
        return cityRepository.save(City.builder()
                .name(name)
                .description(name + " city")
                .country(country)
                .build());
    }

    private Destination saveDestination(String name, City city, Category category, Double latitude, Double longitude) {
        return destinationRepository.save(Destination.builder()
                .name(name)
                .description(name + " destination")
                .imgUrl("https://example.com/" + name + ".jpg")
                .city(city)
                .category(category)
                .latitude(latitude)
                .longitude(longitude)
                .build());
    }

    private Distillery saveDistillery(String name, Destination destination) {
        return saveDistillery(name, destination, 34.8d, 135.5d);
    }

    private Distillery saveDistillery(String name, Destination destination, Double latitude, Double longitude) {
        return distilleryRepository.save(Distillery.builder()
                .name(name)
                .description(name + " distillery")
                .imgUrl("https://example.com/" + name + ".jpg")
                .address(name + " address")
                .latitude(latitude)
                .longitude(longitude)
                .destination(destination)
                .build());
    }

    private Alcohol saveAlcohol(String name, Category category, Distillery distillery) {
        return alcoholRepository.save(Alcohol.builder()
                .name(name)
                .description(name + " alcohol")
                .imgUrl("https://example.com/" + name + ".jpg")
                .category(category)
                .distillery(distillery)
                .strength(46.0f)
                .stated_age("12")
                .size(700.0f)
                .build());
    }
}
