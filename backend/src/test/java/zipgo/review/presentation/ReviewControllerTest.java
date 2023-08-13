package zipgo.review.presentation;

import com.epages.restdocs.apispec.ResourceSnippetDetails;
import com.epages.restdocs.apispec.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import zipgo.acceptance.AcceptanceTest;
import zipgo.member.domain.fixture.MemberFixture;
import zipgo.member.domain.repository.MemberRepository;
import zipgo.petfood.domain.PetFood;
import zipgo.petfood.domain.repository.PetFoodRepository;
import zipgo.review.domain.Review;
import zipgo.review.domain.repository.ReviewRepository;
import zipgo.review.fixture.ReviewFixture;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.resourceDetails;
import static com.epages.restdocs.apispec.Schema.schema;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static zipgo.review.fixture.ReviewFixture.리뷰_생성_요청;
import static zipgo.review.fixture.ReviewFixture.리뷰_수정_요청;


public class ReviewControllerTest extends AcceptanceTest {

    private Long petFoodId = 1L;
    private Long reviewId = 1L;
    private Long 잘못된_id = 123456789L;

    @Autowired
    private PetFoodRepository petFoodRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Nested
    @DisplayName("리뷰 전체 목록 조회 API")
    class GetReviews {

        private Schema 성공_응답_형식 = schema("GetReviewsResponse");
        private ResourceSnippetDetails 문서_정보 = resourceDetails().summary("리뷰 전체 목록 조회하기")
                .description("해당 반려동물 식품에 대한 모든 리뷰를 조회합니다.");

        @Test
        void 사이즈_없이_리뷰를_조회하면_10개_이하가_온다() {
            // given
            리뷰_여러개_생성();
            var 요청_준비 = given(spec).contentType(JSON).filter(리뷰_전체_목록_조회_API_문서_생성("리뷰 전체 조회 - 성공"));

            // when
            var 응답 = 요청_준비.when().pathParam("id", petFoodId).get("/pet-foods/{id}/reviews");

            // then
            응답.then()
                    .assertThat().statusCode(OK.value())
                    .assertThat().body("reviews.size()", is(10));
        }

        @Test
        void 리뷰를_원하는_사이즈만큼_반환() {
            //given
            리뷰_여러개_생성();

            var 요청_준비 = given(spec).contentType(JSON);

            // when
            var 응답 = 요청_준비.when()
                    .queryParam("size", 10)
                    .pathParam("id", petFoodId).get("/pet-foods/{id}/reviews");

            // then
            응답.then()
                    .assertThat().statusCode(OK.value())
                    .assertThat().body("reviews.size()", is(10));
        }

        @Test
        void 마지막일_경우() {
            //given
            리뷰_하나빼고_삭제();
            var 요청_준비 = given(spec).contentType(JSON);

            // when
            var 응답 = 요청_준비.when()
                    .queryParam("size", 10)
                    .pathParam("id", petFoodId).get("/pet-foods/{id}/reviews");

            // then
            응답.then()
                    .assertThat().statusCode(OK.value())
                    .assertThat().body("reviews.size()", lessThan(10));
        }

        private RestDocumentationFilter 리뷰_전체_목록_조회_API_문서_생성(String uniqueName) {
            return document(uniqueName, 문서_정보.responseSchema(성공_응답_형식),
                    queryParameters(parameterWithName("size").description("조회하고자 하는 리뷰의 개수").optional(),
                            parameterWithName("lastReviewId").description("이전 페이지의 마지막 리뷰 id").optional()),
                    pathParameters(parameterWithName("id").description("식품 id")),
                    responseFields(fieldWithPath("reviews[].id").description("리뷰 id").type(JsonFieldType.NUMBER),
                            fieldWithPath("reviews[].reviewerName").description("리뷰 작성자 이름").type(JsonFieldType.STRING),
                            fieldWithPath("reviews[].rating").description("리뷰 별점").type(JsonFieldType.NUMBER),
                            fieldWithPath("reviews[].date").description("리뷰 생성일").type(JsonFieldType.STRING),
                            fieldWithPath("reviews[].comment").description("리뷰 코멘트").type(JsonFieldType.STRING),
                            fieldWithPath("reviews[].tastePreference").description("기호성").type(JsonFieldType.STRING),
                            fieldWithPath("reviews[].stoolCondition").description("대변 상태").type(JsonFieldType.STRING),
                            fieldWithPath("reviews[].adverseReactions").description("이상 반응들")
                                    .type(JsonFieldType.ARRAY)));
        }

        private void 리뷰_여러개_생성() {
            PetFood 식품 = petFoodRepository.findById(petFoodId).orElseThrow(IllegalArgumentException::new);
            List<Review> 리뷰들 = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                Review 리뷰 = ReviewFixture.극찬_리뷰_생성(memberRepository.save(MemberFixture.식별자_없는_멤버("email" + i)), 식품,
                        List.of("없어요"));
                리뷰들.add(리뷰);
            }
            reviewRepository.saveAll(리뷰들);
        }

        private void 리뷰_하나빼고_삭제() {
            reviewRepository.deleteAll(reviewRepository.findAll().stream().skip(1).collect(Collectors.toList()));
        }

        @Test
        void 사이즈를_0으로_했을_때() {
            //given
            var 요청_준비 = given(spec)
                    .contentType(JSON)
                    .filter(API_예외응답_문서_생성("실패 - size가 0일 때"));

            // when
            var 응답 = 요청_준비.when()
                    .queryParam("size", 0)
                    .pathParam("id", petFoodId)
                    .get("/pet-foods/{id}/reviews");

            // then
            응답.then()
                    .assertThat().statusCode(BAD_REQUEST.value())
                    .assertThat().body("message", is("size는 0보다 커야 합니다."));
        }

        private RestDocumentationFilter API_예외응답_문서_생성(String uniqueName) {
            return document(uniqueName, 문서_정보.responseSchema(에러_응답_형식));
        }

    }

    @Nested
    @DisplayName("리뷰 개별 조회 API")
    class GetReview {

        private Schema 성공_응답_형식 = schema("GetReviewResponse");
        private ResourceSnippetDetails 문서_정보 = resourceDetails().summary("리뷰 개별 조회하기")
                .description("해당 반려동물 식품에 대한 개별 리뷰를 조회합니다.");

        @Test
        void 리뷰를_조회하면_201_반환() {
            // given
            var 요청_준비 = given(spec).contentType(JSON).filter(리뷰_개별_API_문서_생성());

            // when
            var 응답 = 요청_준비.when().pathParam("reviewId", reviewId).get("/reviews/{reviewId}");

            // then
            응답.then().assertThat().statusCode(OK.value());
        }

        private RestDocumentationFilter 리뷰_개별_API_문서_생성() {
            return document("리뷰 개별 조회 - 성공", 문서_정보.responseSchema(성공_응답_형식),
                    pathParameters(parameterWithName("reviewId").description("리뷰 id")),
                    responseFields(fieldWithPath("id").description("리뷰 id").type(JsonFieldType.NUMBER),
                            fieldWithPath("reviewerName").description("리뷰 작성자 이름").type(JsonFieldType.STRING),
                            fieldWithPath("rating").description("리뷰 별점").type(JsonFieldType.NUMBER),
                            fieldWithPath("date").description("리뷰 생성일").type(JsonFieldType.STRING),
                            fieldWithPath("comment").description("리뷰 코멘트").type(JsonFieldType.STRING),
                            fieldWithPath("tastePreference").description("기호성").type(JsonFieldType.STRING),
                            fieldWithPath("stoolCondition").description("대변 상태").type(JsonFieldType.STRING),
                            fieldWithPath("adverseReactions").description("이상 반응들").type(JsonFieldType.ARRAY)));
        }

    }

    @Nested
    @DisplayName("리뷰 생성 API")
    class CreateReviews {

        private ResourceSnippetDetails 문서_정보 = resourceDetails().summary("리뷰 생성하기")
                .description("해당 반려동물 식품에 대한 리뷰를 생성합니다.");
        private Schema 요청_형식 = schema("CreateReviewRequest");
        private Schema 성공_응답_형식 = schema("CreateReviewResponse");

        @Test
        void 리뷰를_성공적으로_생성하면_201_반환() {
            // given
            var token = jwtProvider.create("1");
            var 요청_준비 = given(spec).header("Authorization", "Bearer " + token).body(리뷰_생성_요청(petFoodId))
                    .contentType(JSON).filter(리뷰_생성_API_문서_생성());

            // when
            var 응답 = 요청_준비.when().post("/reviews");

            // then
            응답.then().assertThat().statusCode(CREATED.value());
        }

        private RestDocumentationFilter 리뷰_생성_API_문서_생성() {
            return document("리뷰 생성 - 성공", 문서_정보.requestSchema(요청_형식).responseSchema(성공_응답_형식),
                    requestHeaders(headerWithName("Authorization").description("인증을 위한 JWT")),
                    requestFields(fieldWithPath("petFoodId").description("식품 id").type(JsonFieldType.NUMBER),
                            fieldWithPath("rating").description("리뷰 별점").type(JsonFieldType.NUMBER),
                            fieldWithPath("comment").description("리뷰 코멘트").optional().type(JsonFieldType.STRING),
                            fieldWithPath("tastePreference").description("기호성").type(JsonFieldType.STRING),
                            fieldWithPath("stoolCondition").description("대변 상태").type(JsonFieldType.STRING),
                            fieldWithPath("adverseReactions").description("이상 반응들").type(JsonFieldType.ARRAY)));
        }

        @Test
        void 없는_식품에_대해_리뷰를_생성하면_404_반환() {
            // given
            var token = jwtProvider.create("1");
            var 요청_준비 = given(spec).header("Authorization", "Bearer " + token).body(리뷰_생성_요청(잘못된_id)).contentType(JSON)
                    .filter(API_예외응답_문서_생성());

            // when
            var 응답 = 요청_준비.when().post("/reviews");

            // then
            응답.then().assertThat().statusCode(NOT_FOUND.value());
        }

        private RestDocumentationFilter API_예외응답_문서_생성() {
            return document("리뷰 생성 - 실패(없는 식품)", 문서_정보.responseSchema(에러_응답_형식),
                    requestHeaders(headerWithName("Authorization").description("인증을 위한 JWT")));
        }

    }

    @Nested
    @DisplayName("리뷰 수정 API")
    class UpdateReviews {

        private Schema 요청_형식 = schema("UpdateReviewRequest");
        private Schema 성공_응답_형식 = schema("UpdateReviewResponse");
        private ResourceSnippetDetails 문서_정보 = resourceDetails().summary("리뷰 수정하기")
                .description("해당 반려동물 식품에 대한 리뷰를 수정합니다.");

        @Test
        void 리뷰를_성공적으로_수정하면_204_반환() {
            // given
            var token = jwtProvider.create("1");
            var 요청_준비 = given(spec).header("Authorization", "Bearer " + token).body(리뷰_수정_요청()).contentType(JSON)
                    .filter(리뷰_수정_API_문서_생성());

            // when
            var 응답 = 요청_준비.when().pathParam("reviewId", reviewId).put("/reviews/{reviewId}");

            // then
            응답.then().assertThat().statusCode(NO_CONTENT.value());
        }

        private RestDocumentationFilter 리뷰_수정_API_문서_생성() {
            return document("리뷰 수정 - 성공", 문서_정보.requestSchema(요청_형식).responseSchema(성공_응답_형식),
                    requestHeaders(headerWithName("Authorization").description("인증을 위한 JWT")),
                    pathParameters(parameterWithName("reviewId").description("리뷰 id")),
                    requestFields(fieldWithPath("rating").description("리뷰 별점").type(JsonFieldType.NUMBER),
                            fieldWithPath("comment").description("리뷰 코멘트").optional().type(JsonFieldType.STRING),
                            fieldWithPath("tastePreference").description("기호성").type(JsonFieldType.STRING),
                            fieldWithPath("stoolCondition").description("대변 상태").type(JsonFieldType.STRING),
                            fieldWithPath("adverseReactions").description("이상 반응들").type(JsonFieldType.ARRAY)));
        }

        @Test
        void 리뷰를_쓴_사람이_아닌_멤버가_리뷰를_수정하면_403_반환() {
            // given
            var notOwnerToken = jwtProvider.create("2");
            var 요청_준비 = given(spec).header("Authorization", "Bearer " + notOwnerToken).body(리뷰_생성_요청(petFoodId))
                    .contentType(JSON).filter(API_예외응답_문서_생성());

            // when
            var 응답 = 요청_준비.when().pathParam("reviewId", reviewId).put("/reviews/{reviewId}");

            // then
            응답.then().assertThat().statusCode(FORBIDDEN.value());
        }

        private RestDocumentationFilter API_예외응답_문서_생성() {
            return document("리뷰 수정 - 실패(Forbidden)", 문서_정보.responseSchema(에러_응답_형식),
                    requestHeaders(headerWithName("Authorization").description("인증을 위한 JWT")));
        }

    }


    @Nested
    @DisplayName("리뷰 삭제 API")
    class DeleteReviews {

        private Schema 성공_응답_형식 = schema("DeleteReviewResponse");
        private ResourceSnippetDetails 문서_정보 = resourceDetails().summary("리뷰 삭제하기")
                .description("해당 반려동물 식품에 대한 리뷰를 삭제합니다.");

        @Test
        void 리뷰를_성공적으로_삭제하면_204_반환() {
            // given
            var token = jwtProvider.create("1");
            var 요청_준비 = given(spec).header("Authorization", "Bearer " + token).body(리뷰_수정_요청()).contentType(JSON)
                    .filter(리뷰_삭제_API_문서_생성());

            // when
            var 응답 = 요청_준비.when().pathParam("reviewId", reviewId).delete("/reviews/{reviewId}");

            // then
            응답.then().assertThat().statusCode(NO_CONTENT.value());
        }

        private RestDocumentationFilter 리뷰_삭제_API_문서_생성() {
            return document("리뷰 삭제 - 성공", 문서_정보.responseSchema(성공_응답_형식),
                    pathParameters(parameterWithName("reviewId").description("리뷰 id")),
                    requestHeaders(headerWithName("Authorization").description("인증을 위한 JWT")));
        }

        @Test
        void 리뷰를_쓴_사람이_아닌_멤버가_리뷰를_삭제하면_403_반환() {
            // given
            var notOwnerToken = jwtProvider.create("2");
            var 요청_준비 = given(spec).header("Authorization", "Bearer " + notOwnerToken).body(리뷰_생성_요청(petFoodId))
                    .contentType(JSON).filter(API_예외응답_문서_생성());

            // when
            var 응답 = 요청_준비.when().pathParam("reviewId", reviewId).delete("/reviews/{reviewId}");

            // then
            응답.then().assertThat().statusCode(FORBIDDEN.value());
        }

        private RestDocumentationFilter API_예외응답_문서_생성() {
            return document("리뷰 삭제 - 실패(Forbidden)", 문서_정보.responseSchema(에러_응답_형식),
                    requestHeaders(headerWithName("Authorization").description("인증을 위한 JWT")));
        }

    }

}
