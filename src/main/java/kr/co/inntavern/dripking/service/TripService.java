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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public TripResponseDTO getTripById(Long id, Long userId){
        return mapToTripResponseDTO(getOwnedTrip(id, userId));
    }

    public Page<TripResponseDTO> getAllTripByUserId(int page, int size, Long userId, String sortBy){
        Sort.Direction sort = sortBy.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "startDate").and(Sort.by(sort, "id")));
        Page<Trip> trips = tripRepository.findAllByUserId(userId, pageable);
        return trips.map(this::mapToTripResponseDTO);
    }

    public Page<TripContainCountryResponseDTO> getAllTripContainCountryByUserId(int page, int size, Long userId, String sortBy){
        Sort.Direction sort = sortBy.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "startDate").and(Sort.by(sort, "id")));
        Page<Trip> trips = tripRepository.findAllByUserId(userId, pageable);
        return trips.map(this::mapToTripContainCountryResponseDTO);
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
            return responseDTO;
        });
    }
 
    public TripResponseDTO createTrip(TripRequestDTO tripRequestDTO, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
        Country country = getCountry(tripRequestDTO);

        Trip trip  = new Trip();
        trip.setUser(user);
        trip.setName(tripRequestDTO.getName());
        trip.setDescription(tripRequestDTO.getDescription());
        trip.setStartDate(tripRequestDTO.getStartDate());
        trip.setEndDate(tripRequestDTO.getEndDate());
        trip.setCountry(country);
        
        return mapToTripResponseDTO(tripRepository.save(trip));
    }

    public void updateTrip(Long id, TripRequestDTO tripRequestDTO, Long userId){
        Trip trip = getOwnedTrip(id, userId);

        trip.setName(tripRequestDTO.getName());
        trip.setDescription(tripRequestDTO.getDescription());
        trip.setStartDate(tripRequestDTO.getStartDate());
        trip.setEndDate(tripRequestDTO.getEndDate());
        trip.setCountry(getCountry(tripRequestDTO));
        tripRepository.save(trip);
    }

    public void deleteTripById(Long id, Long userId){
        Trip trip = getOwnedTrip(id, userId);
        tripRepository.delete(trip);
    }

    private TripResponseDTO mapToTripResponseDTO(Trip trip){
        TripResponseDTO responseDTO = new TripResponseDTO();
        responseDTO.setId(trip.getId());
        responseDTO.setUserId(trip.getUser().getId());
        responseDTO.setName(trip.getName());
        responseDTO.setDescription(trip.getDescription());
        responseDTO.setStartDate(trip.getStartDate());
        responseDTO.setEndDate(trip.getEndDate());
        if(trip.getCountry() != null){
            responseDTO.setCountryId(trip.getCountry().getId());
            responseDTO.setCountryName(trip.getCountry().getName());
        }
        return responseDTO;
    }

    private TripContainCountryResponseDTO mapToTripContainCountryResponseDTO(Trip trip){
        TripContainCountryResponseDTO responseDTO = new TripContainCountryResponseDTO();
        responseDTO.setId(trip.getId());
        responseDTO.setUserId(trip.getUser().getId());
        responseDTO.setName(trip.getName());
        responseDTO.setDescription(trip.getDescription());
        responseDTO.setStartDate(trip.getStartDate());
        responseDTO.setEndDate(trip.getEndDate());
        responseDTO.setCountryName(trip.getCountry().getName());
        return responseDTO;
    }

    private Trip getOwnedTrip(Long tripId, Long userId){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 여행이 존재하지 않습니다."));

        if(trip.getUser() == null || !trip.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("해당 여행에 접근할 권한이 없습니다.");
        }

        return trip;
    }

    private Country getCountryByName(String countryName){
        Country country = countryRepository.findByName(countryName);
        if(country == null){
            throw new IllegalArgumentException("해당 이름의 국가가 존재하지 않습니다.");
        }

        return country;
    }

    private Country getCountry(TripRequestDTO tripRequestDTO){
        if(tripRequestDTO.getCountryId() != null){
            return countryRepository.findById(tripRequestDTO.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 국가가 존재하지 않습니다."));
        }

        return getCountryByName(tripRequestDTO.getCountryName());
    }
}
