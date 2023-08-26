package zipgo.review.application;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import zipgo.brand.domain.Brand;
import zipgo.brand.domain.repository.BrandRepository;
import zipgo.common.service.QueryServiceTest;
import zipgo.member.domain.Member;
import zipgo.member.domain.repository.MemberRepository;
import zipgo.pet.domain.Breeds;
import zipgo.pet.domain.Pet;
import zipgo.pet.domain.PetSize;
import zipgo.pet.domain.repository.BreedsRepository;
import zipgo.pet.domain.repository.PetRepository;
import zipgo.pet.domain.repository.PetSizeRepository;
import zipgo.petfood.domain.PetFood;
import zipgo.petfood.domain.repository.PetFoodRepository;
import zipgo.review.domain.Review;
import zipgo.review.domain.repository.ReviewRepository;
import zipgo.review.domain.repository.dto.FindReviewsFilterRequest;
import zipgo.review.dto.response.GetReviewMetadataResponse;
import zipgo.review.dto.response.GetReviewResponse;
import zipgo.review.dto.response.GetReviewsResponse;
import zipgo.review.dto.response.GetReviewsSummaryResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static zipgo.brand.domain.fixture.BrandFixture.아카나_식품_브랜드_생성;
import static zipgo.pet.domain.fixture.BreedsFixture.견종;
import static zipgo.pet.domain.fixture.PetFixture.*;
import static zipgo.pet.domain.fixture.PetSizeFixture.소형견;
import static zipgo.petfood.domain.fixture.PetFoodFixture.모든_영양기준_만족_식품;
import static zipgo.review.domain.type.AdverseReactionType.NONE;
import static zipgo.review.domain.type.StoolCondition.SOFT_MOIST;
import static zipgo.review.domain.type.TastePreference.EATS_VERY_WELL;
import static zipgo.review.fixture.AdverseReactionFixture.눈물_이상반응;
import static zipgo.review.fixture.AdverseReactionFixture.먹고_토_이상반응;
import static zipgo.review.fixture.MemberFixture.멤버_이름;
import static zipgo.review.fixture.MemberFixture.무민;
import static zipgo.review.fixture.ReviewFixture.극찬_리뷰_생성;
import static zipgo.review.fixture.ReviewFixture.혹평_리뷰_생성;

class ReviewQueryServiceTest extends QueryServiceTest {

    @Autowired
    private PetFoodRepository petFoodRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PetSizeRepository petSizeRepository;

    @Autowired
    private BreedsRepository breedsRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ReviewQueryService reviewQueryService;

    @Nested
    class 리뷰_목록_조회 {

        @Test
        void 사이즈로_리뷰_목록_조회() {
            //given
            PetFood 식품 = petFoodRepository.save(모든_영양기준_만족_식품(브랜드_조회하기()));
            Review 리뷰1 = 리뷰1_생성(식품);
            Review 리뷰2 = 리뷰2_생성(식품);
            GetReviewResponse 리뷰1_dto = GetReviewResponse.from(리뷰1, null);
            GetReviewResponse 리뷰2_dto = GetReviewResponse.from(리뷰2, null);

            //when
            var 요청 = FindReviewsFilterRequest.builder()
                    .petFoodId(식품.getId())
                    .size(2)
                    .sortBy(SortBy.RECENT)
                    .build();
            GetReviewsResponse 리뷰_리스트 = reviewQueryService.getReviews(요청);

            //then
            assertAll(
                    () -> assertThat(리뷰_리스트.reviews().size()).isEqualTo(2),
                    () -> assertThat(리뷰_리스트.reviews().get(0)).usingRecursiveComparison().isEqualTo(리뷰2_dto),
                    () -> assertThat(리뷰_리스트.reviews().get(1)).usingRecursiveComparison().isEqualTo(리뷰1_dto)
            );
        }

    }

    private Review 리뷰1_생성(PetFood 식품) {
        Member 멤버 = memberRepository.save(무민());
        PetSize 사이즈 = petSizeRepository.save(소형견());
        Breeds 종류 = breedsRepository.save(견종(사이즈));
        Pet 반려동물 = petRepository.save(반려동물(멤버, 종류));
        Review review = reviewRepository.save(극찬_리뷰_생성(반려동물, 식품, List.of("없어요")));
        식품.addReview(review);
        return review;
    }

    private Review 리뷰2_생성(PetFood 식품) {
        PetSize 사이즈 = petSizeRepository.save(소형견());
        Member 멤버2 = memberRepository.save(멤버_이름("무민2"));
        Breeds 종류 = breedsRepository.save(견종(사이즈));
        Pet 반려동물2 = petRepository.save(반려동물(멤버2, 종류));
        Review review = reviewRepository.save(
                혹평_리뷰_생성(반려동물2, 식품, List.of(눈물_이상반응().getAdverseReactionType().getDescription(),
                        먹고_토_이상반응().getAdverseReactionType().getDescription())));
        식품.addReview(review);
        return review;
    }

    private Brand 브랜드_조회하기() {
        return brandRepository.save(아카나_식품_브랜드_생성());
    }

    @Test
    void getReview() {
        //given
        PetFood 식품 = 모든_영양기준_만족_식품(브랜드_조회하기());
        petFoodRepository.save(식품);
        Member 멤버 = memberRepository.save(무민());
        PetSize 사이즈 = petSizeRepository.save(소형견());
        Breeds 종류 = breedsRepository.save(견종(사이즈));
        Pet 반려동물 = petRepository.save(반려동물(멤버, 종류));
        Review 극찬_리뷰 = reviewRepository.save(극찬_리뷰_생성(반려동물, 식품, List.of("없어요")));

        //when
        Review review = reviewQueryService.getReview(극찬_리뷰.getId());

        //then
        assertAll(() -> assertThat(review.getPet().getOwner().getName()).isEqualTo("무민"),
                () -> assertThat(review.getPet().getName()).isEqualTo("무민이"),
                () -> assertThat(review.getRating()).isEqualTo(5),
                () -> assertThat(review.getComment()).isEqualTo("우리 아이랑 너무 잘 맞아요!"),
                () -> assertThat(review.getTastePreference()).isEqualTo(EATS_VERY_WELL),
                () -> assertThat(review.getStoolCondition()).isEqualTo(SOFT_MOIST),
                () -> assertThat(review.getAdverseReactions().get(0).getAdverseReactionType()).isEqualTo(NONE));
    }

    @Test
    void 리뷰_메타데이터를_조회할_수_있다() {
        //given
        요구되는_리뷰_메타데이터_저장();

        //when
        GetReviewMetadataResponse 응답 = reviewQueryService.getReviewMetadata();

        //then
        assertAll(() -> assertThat(응답.petSizes()).hasSize(3), () -> assertThat(응답.sortBy()).hasSize(4),
                () -> assertThat(응답.ageGroups()).hasSize(3), () -> assertThat(응답.breeds()).isNotEmpty());
    }

    private void 요구되는_리뷰_메타데이터_저장() {
        PetSize 소형견 = new PetSize(null, "소형견");
        PetSize 중형견 = new PetSize(null, "중형견");
        PetSize 대형견 = new PetSize(null, "대형견");
        List<PetSize> petSizes = List.of(소형견, 중형견, 대형견);
        petSizeRepository.saveAll(petSizes);
        List<Breeds> breeds = List.of(new Breeds(null, "푸들", 소형견), new Breeds(null, "말티즈", 소형견),
                new Breeds(null, "진돗개", 중형견), new Breeds(null, "시바견", 중형견), new Breeds(null, "리트리버", 대형견),
                new Breeds(null, "허스키", 대형견));
        breedsRepository.saveAll(breeds);
    }

    @Test
    void 리뷰_요약을_조회할_수_있다() {
        //given
        PetFood 식품 = petFoodRepository.save(모든_영양기준_만족_식품(브랜드_조회하기()));
        Review 평점_5점_정말_잘_먹어요_촉촉_말랑해요_없어요_리뷰 = 리뷰1_생성(식품);
        Review 평점_1점_전혀_안_먹어요_설사를_해요_눈물_이상_먹고_토_반응_리뷰 = 리뷰2_생성(식품);

        //when
        GetReviewsSummaryResponse reviewsSummary = reviewQueryService.getReviewsSummary(식품.getId());

        //then
        assertAll(
                () -> assertThat(reviewsSummary.rating().average()).isEqualTo(3.0),
                () -> assertThat(reviewsSummary.rating().rating().get(0).name()).isEqualTo("5"),
                () -> assertThat(reviewsSummary.rating().rating().get(0).percentage()).isEqualTo(50),
                () -> assertThat(reviewsSummary.rating().rating().get(1).name()).isEqualTo("4"),
                () -> assertThat(reviewsSummary.rating().rating().get(1).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.rating().rating().get(2).name()).isEqualTo("3"),
                () -> assertThat(reviewsSummary.rating().rating().get(2).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.rating().rating().get(3).name()).isEqualTo("2"),
                () -> assertThat(reviewsSummary.rating().rating().get(3).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.rating().rating().get(4).name()).isEqualTo("1"),
                () -> assertThat(reviewsSummary.rating().rating().get(4).percentage()).isEqualTo(50),
                () -> assertThat(reviewsSummary.rating().rating().get(4).name()).isEqualTo("1"),
                () -> assertThat(reviewsSummary.rating().rating().get(4).percentage()).isEqualTo(50),
                () -> assertThat(reviewsSummary.tastePreference().get(0).name()).isEqualTo("정말 잘 먹어요"),
                () -> assertThat(reviewsSummary.tastePreference().get(0).percentage()).isEqualTo(50),
                () -> assertThat(reviewsSummary.tastePreference().get(1).name()).isEqualTo("잘 먹는 편이에요"),
                () -> assertThat(reviewsSummary.tastePreference().get(1).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.tastePreference().get(2).name()).isEqualTo("잘 안 먹어요"),
                () -> assertThat(reviewsSummary.tastePreference().get(2).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.tastePreference().get(3).name()).isEqualTo("전혀 안 먹어요"),
                () -> assertThat(reviewsSummary.tastePreference().get(3).percentage()).isEqualTo(50),
                () -> assertThat(reviewsSummary.stoolCondition().get(0).name()).isEqualTo("촉촉 말랑해요"),
                () -> assertThat(reviewsSummary.stoolCondition().get(0).percentage()).isEqualTo(50),
                () -> assertThat(reviewsSummary.stoolCondition().get(1).name()).isEqualTo("설사를 해요"),
                () -> assertThat(reviewsSummary.stoolCondition().get(1).percentage()).isEqualTo(50),
                () -> assertThat(reviewsSummary.stoolCondition().get(2).name()).isEqualTo("딱딱해요"),
                () -> assertThat(reviewsSummary.stoolCondition().get(2).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.stoolCondition().get(3).name()).isEqualTo("잘 모르겠어요"),
                () -> assertThat(reviewsSummary.stoolCondition().get(3).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.adverseReaction().get(0).name()).isEqualTo("털이 푸석해요"),
                () -> assertThat(reviewsSummary.adverseReaction().get(0).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.adverseReaction().get(1).name()).isEqualTo("먹고 토해요"),
                () -> assertThat(reviewsSummary.adverseReaction().get(1).percentage()).isEqualTo(33),
                () -> assertThat(reviewsSummary.adverseReaction().get(2).name()).isEqualTo("눈물이 나요"),
                () -> assertThat(reviewsSummary.adverseReaction().get(2).percentage()).isEqualTo(33),
                () -> assertThat(reviewsSummary.adverseReaction().get(3).name()).isEqualTo("몸을 긁어요"),
                () -> assertThat(reviewsSummary.adverseReaction().get(3).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.adverseReaction().get(4).name()).isEqualTo("발을 핥아요"),
                () -> assertThat(reviewsSummary.adverseReaction().get(4).percentage()).isEqualTo(0),
                () -> assertThat(reviewsSummary.adverseReaction().get(5).name()).isEqualTo("없어요"),
                () -> assertThat(reviewsSummary.adverseReaction().get(5).percentage()).isEqualTo(33)
        );
    }

}
