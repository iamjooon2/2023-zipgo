package zipgo.review.application.dto;

public record GetReviewQueryRequest(
        Long petFoodId,
        int size,
        Long lastReviewId
) {

    public GetReviewQueryRequest {
        if (petFoodId == null) {
            throw new IllegalArgumentException("petFoodId는 null이 될 수 없습니다.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size는 0보다 커야 합니다.");
        }
    }

}