package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.model.enumType.ReviewStatus;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewRatingService {
    private final ReviewRepository reviewRepository;
    private final AlcoholRepository alcoholRepository;
    private final DistilleryRepository distilleryRepository;
    private final DestinationRepository destinationRepository;

    public ReviewRatingService(ReviewRepository reviewRepository,
                               AlcoholRepository alcoholRepository,
                               DistilleryRepository distilleryRepository,
                               DestinationRepository destinationRepository) {
        this.reviewRepository = reviewRepository;
        this.alcoholRepository = alcoholRepository;
        this.distilleryRepository = distilleryRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional
    public void recalculate(ItemType itemType, Long targetId) {
        if (itemType == null || targetId == null) {
            throw new IllegalArgumentException("평점 집계 대상 정보가 필요합니다.");
        }

        float rating = reviewRepository.calculateVisibleAverageRating(targetId, itemType, ReviewStatus.VISIBLE)
                .floatValue();

        switch (itemType) {
            case ALCOHOL -> alcoholRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 술이 존재하지 않습니다."))
                    .setRating(rating);
            case DISTILLERY -> distilleryRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 양조장이 존재하지 않습니다."))
                    .setRating(rating);
            case DESTINATION -> destinationRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 목적지가 존재하지 않습니다."))
                    .setRating(rating);
            default -> throw new IllegalArgumentException("평점을 집계할 수 없는 itemType입니다.");
        }
    }
}
