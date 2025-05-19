package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.TripRequestDTO;
import kr.co.inntavern.dripking.dto.response.TripContainCountryResponseDTO;
import kr.co.inntavern.dripking.dto.response.TripResponseDTO;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.TripRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    public TripService(TripRepository tripRepository, UserRepository userRepository, CountryRepository countryRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
    }

    public TripResponseDTO getTripById(Long id){
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행이 존재하지 않습니다."));
        return mapToTripResponseDTO(trip);
    }

    public Page<TripResponseDTO> getAllTripByUserId(int page, int size, Long userId){
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findAllByUserId(userId, pageable);
        return trips.map(this::mapToTripResponseDTO);
    }

    public Page<TripContainCountryResponseDTO> getAllTripCountyByUserId(int page, int size, Long userId){
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findAllByUserId(userId, pageable);
        return trips.map(trip -> {
            TripContainCountryResponseDTO responseDTO = new TripContainCountryResponseDTO();
            responseDTO.setId(trip.getId());
            responseDTO.setUserId(trip.getUser().getId());
            responseDTO.setName(trip.getName());
            responseDTO.setDescription(trip.getDescription());
            responseDTO.setStartDate(trip.getStartDate());
            responseDTO.setEndDate(trip.getEndDate());
            responseDTO.setCountryName(trip.getCountry().getName());
            responseDTO.setCountryLat(trip.getCountry().getLatitude());
            responseDTO.setCountryLng(trip.getCountry().getLongitude());
            return responseDTO;
        });
    }
 
    public TripResponseDTO createTrip(TripRequestDTO tripRequestDTO){
        User user = userRepository.findById(tripRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
        Country country = countryRepository.findByName(tripRequestDTO.getCountryName());

        Trip trip  = new Trip();
        trip.setUser(user); //user 에 관한 내용은 login 기능이 구현되면 추가할 예정
        trip.setUser(userRepository.findById(tripRequestDTO.getUserId()).orElse(null));
        trip.setName(tripRequestDTO.getName());
        trip.setDescription(tripRequestDTO.getDescription());
        trip.setStartDate(tripRequestDTO.getStartDate());
        trip.setEndDate(tripRequestDTO.getEndDate());
        trip.setCountry(country);
        
        return mapToTripResponseDTO(tripRepository.save(trip));
    }

    public void updateTrip(Long id, TripRequestDTO tripRequestDTO){
        Optional<Trip> trip = tripRepository.findById(id);
        if(trip.isEmpty()){
            throw new IllegalArgumentException("해당 ID의 리뷰가 존재하지 않습니다.");
        }

        trip.get().setUser(userRepository.findById(tripRequestDTO.getUserId()).orElse(null));
        trip.get().setName(tripRequestDTO.getName());
        trip.get().setDescription(tripRequestDTO.getDescription());
        trip.get().setStartDate(tripRequestDTO.getStartDate());
        trip.get().setEndDate(tripRequestDTO.getEndDate());
        tripRepository.save(trip.orElse(null));
    }

    public void deleteTripById(Long id){
        tripRepository.deleteById(id);
    }

    private TripResponseDTO mapToTripResponseDTO(Trip trip){
        TripResponseDTO responseDTO = new TripResponseDTO();
        responseDTO.setId(trip.getId());
        responseDTO.setUserId(trip.getUser().getId());
        responseDTO.setName(trip.getName());
        responseDTO.setDescription(trip.getDescription());
        responseDTO.setStartDate(trip.getStartDate());
        responseDTO.setEndDate(trip.getEndDate());
        return responseDTO;
    }
}
