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

    public Page<TripResponseDTO> getAllTripByUserId(int page, int size, Long user_id){
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findAllByUserId(user_id, pageable);
        return trips.map(this::mapToTripResponseDTO);
    }

    public Page<TripContainCountryResponseDTO> getAllTripCountyByUserId(int page, int size, Long user_id){
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findAllByUserId(user_id, pageable);
        return trips.map(trip -> {
            TripContainCountryResponseDTO responseDTO = new TripContainCountryResponseDTO();
            responseDTO.setId(trip.getId());
            responseDTO.setUser_id(trip.getUser().getId());
            responseDTO.setName(trip.getName());
            responseDTO.setDescription(trip.getDescription());
            responseDTO.setStart_date(trip.getStart_date());
            responseDTO.setEnd_date(trip.getEnd_date());
            responseDTO.setItemType(trip.getItemType());
            responseDTO.setCountry_name(trip.getCountry().getName());
            responseDTO.setCountry_lat(trip.getCountry().getLat());
            responseDTO.setCountry_lng(trip.getCountry().getLng());
            return responseDTO;
        });
    }
 
    public TripResponseDTO createTrip(TripRequestDTO tripRequestDTO){
        User user = userRepository.findById(tripRequestDTO.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
        Country country = countryRepository.findByName(tripRequestDTO.getCountry_name());

        Trip trip  = new Trip();
        trip.setUser(user); //user 에 관한 내용은 login 기능이 구현되면 추가할 예정
        trip.setUser(userRepository.findById(tripRequestDTO.getUser_id()).orElse(null));
        trip.setName(tripRequestDTO.getName());
        trip.setDescription(tripRequestDTO.getDescription());
        trip.setStart_date(tripRequestDTO.getStart_date());
        trip.setEnd_date(tripRequestDTO.getEnd_date());
        trip.setCountry(country);
        
        return mapToTripResponseDTO(tripRepository.save(trip));
    }

    public void updateTrip(Long id, TripRequestDTO tripRequestDTO){
        Optional<Trip> trip = tripRepository.findById(id);
        if(trip.isEmpty()){
            throw new IllegalArgumentException("해당 ID의 리뷰가 존재하지 않습니다.");
        }

        trip.get().setUser(userRepository.findById(tripRequestDTO.getUser_id()).orElse(null));
        trip.get().setName(tripRequestDTO.getName());
        trip.get().setDescription(tripRequestDTO.getDescription());
        trip.get().setStart_date(tripRequestDTO.getStart_date());
        trip.get().setEnd_date(tripRequestDTO.getEnd_date());
        tripRepository.save(trip.orElse(null));
    }

    public void deleteTripById(Long id){
        tripRepository.deleteById(id);
    }

    private TripResponseDTO mapToTripResponseDTO(Trip trip){
        TripResponseDTO responseDTO = new TripResponseDTO();
        responseDTO.setId(trip.getId());
        responseDTO.setUser_id(trip.getUser().getId());
        responseDTO.setName(trip.getName());
        responseDTO.setDescription(trip.getDescription());
        responseDTO.setStart_date(trip.getStart_date());
        responseDTO.setEnd_date(trip.getEnd_date());
        return responseDTO;
    }
}
