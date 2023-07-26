package zipgo.petfood.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static zipgo.petfood.domain.fixture.PetFoodFixture.키워드_없이_식품_초기화;
import static zipgo.petfood.domain.fixture.PetFoodFixture.키워드_있는_식품_초기화;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import zipgo.petfood.domain.Keyword;
import zipgo.petfood.domain.PetFood;

@DataJpaTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class PetFoodRepositoryTest {

    @Autowired
    private PetFoodRepository petFoodRepository;

    @Autowired
    KeywordRepository keywordRepository;

    @Test
    void 모든_식품을_조회할_수_있다() {
        // given
        PetFood 반려동물_식품_1 = petFoodRepository.save(키워드_없이_식품_초기화());
        PetFood 반려동물_식품_2 = petFoodRepository.save(키워드_없이_식품_초기화());

        // when
        List<PetFood> petFoods = petFoodRepository.findAll();

        // then
        assertThat(petFoods).contains(반려동물_식품_1, 반려동물_식품_2);
    }

    @Test
    void 키워드로_식품을_조회할_수_있다() {
        // given
        PetFood 키워드가_없는_식품 = petFoodRepository.save(키워드_없이_식품_초기화());
        PetFood 키워드가_있는_식품 = 키워드있는_식품_생성하기();

        // when
        List<PetFood> 조회된_식품 = petFoodRepository.findByKeyword(키워드가_있는_식품.getKeyword());

        // then
        assertThat(조회된_식품).contains(키워드가_있는_식품);
        assertThat(조회된_식품).doesNotContain(키워드가_없는_식품);
    }

    private PetFood 키워드있는_식품_생성하기() {
        Keyword 키워드 = keywordRepository.findAll().get(0);
        return petFoodRepository.save(키워드_있는_식품_초기화(키워드));
    }

}
