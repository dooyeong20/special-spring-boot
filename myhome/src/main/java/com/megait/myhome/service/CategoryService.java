package com.megait.myhome.service;

import com.megait.myhome.domain.Category;
import com.megait.myhome.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void InitBookCategory() {

        Category b0_1 = Category.builder().name("소설/시/희곡").build();
        Category b0_2 = Category.builder().name("경제/경영").build();
        Category b0_3 = Category.builder().name("지식/기술/자기계발").build();
        Category b0_4 = Category.builder().name("종교").build();

        Category b1_1 = Category.builder().name("소설/시/희곡").build();
        Category b1_2 = Category.builder().name("경제/경영").build();
        Category b1_3 = Category.builder().name("지식/기술/자기계발").build();
        Category b1_4 = Category.builder().name("종교").build();

        Category b0 = Category.builder().name("국내도서").build()
                .addChildCategory(b0_1)
                .addChildCategory(b0_2)
                .addChildCategory(b0_3)
                .addChildCategory(b0_4);

        Category b1 = Category.builder().name("해외도서").build()
                .addChildCategory(b1_1)
                .addChildCategory(b1_2)
                .addChildCategory(b1_3)
                .addChildCategory(b1_4);

        Category b = Category.builder().name("도서").parent(null).build()
                .addChildCategory(b0)
                .addChildCategory(b1);

        categoryRepository.saveAll(List.of(b, b0, b1, b0_1, b0_2, b0_3, b0_4, b1_1, b1_2, b1_3, b1_4));
    }

    @PostConstruct
    @Transactional
    public void InitAlbumCategory() {

        Category b0_1 = Category.builder().name("팝").build();
        Category b0_2 = Category.builder().name("락").build();
        Category b0_3 = Category.builder().name("일렉트로닉").build();
        Category b0_4 = Category.builder().name("케이팝").build();
        Category b0_5 = Category.builder().name("힙합").build();

        Category b1_1 = Category.builder().name("오케스트라").build();
        Category b1_2 = Category.builder().name("피아노 솔로").build();
        Category b1_3 = Category.builder().name("바이올린 솔로").build();
        Category b1_4 = Category.builder().name("성악").build();


        Category b0 = Category.builder().name("대중음악").build()
                .addChildCategory(b0_1)
                .addChildCategory(b0_2)
                .addChildCategory(b0_3)
                .addChildCategory(b0_4)
                .addChildCategory(b0_5);

        Category b1 = Category.builder().name("클래식").build()
                .addChildCategory(b1_1)
                .addChildCategory(b1_2)
                .addChildCategory(b1_3)
                .addChildCategory(b1_4);

        Category b = Category.builder().name("음반").parent(null).build()
                .addChildCategory(b0)
                .addChildCategory(b1);

        categoryRepository.saveAll(List.of(b, b0, b1, b0_1, b0_2, b0_3, b0_4, b0_5, b1_1, b1_2, b1_3, b1_4));
    }
}
