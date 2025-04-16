package kr.co.inntavern.dripking.service.dashboard;

import kr.co.inntavern.dripking.dto.response.dashboard.DestinationDashboardResponseDTO;
import kr.co.inntavern.dripking.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DestinationDashboardService {
    private final DestinationRepository destinationRepository;

    public DestinationDashboardService(DestinationRepository destinationRepository){
        this.destinationRepository = destinationRepository;
    }

    public Page<DestinationDashboardResponseDTO> findAllDestinationForDashboard(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return destinationRepository.findAllForDestinationDashboard(pageable);
    }
}
