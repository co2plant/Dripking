package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.Request.TripRequestDTO;
import kr.co.inntavern.dripking.model.Trip;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.TripRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripService(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------------------
    // Create Methods: 엔티티를 생성하는 메서드
    // ---------------------------------------------------------------------
    public void createTrip(TripRequestDTO tripRequestDTO){
        User user = userRepository.findById(tripRequestDTO.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
        Trip trip  = new Trip();
        trip.setUser(user); //user 에 관한 내용은 login 기능이 구현되면 추가할 예정

        trip.setUser(userRepository.findById(tripRequestDTO.getUser_id()).orElse(null));
        trip.setName(tripRequestDTO.getName());
        trip.setDescription(tripRequestDTO.getDescription());
        trip.setStart_date(tripRequestDTO.getStart_date());
        trip.setEnd_date(tripRequestDTO.getEnd_date());
        tripRepository.save(trip);
    }

    // ---------------------------------------------------------------------
    // Update Methods: 엔티티를 수정하는 메서드
    // ---------------------------------------------------------------------
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

    // ---------------------------------------------------------------------
    // Delete Methods: 엔티티를 삭제하는 메서드
    // ---------------------------------------------------------------------
    public void deleteTripById(Long id){
        tripRepository.deleteById(id);
    }
}
