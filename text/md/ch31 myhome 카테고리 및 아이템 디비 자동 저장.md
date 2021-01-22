**Book**이 없었다..

```java
package com.megait.myhome.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter
@NoArgsConstructor
public class Book extends Item{
    private String isbn;
}

```

****

**Album**도 없었다..

```java
package com.megait.myhome.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A")
@Getter
@Setter
public class Album extends Item{
    private String title;
    private String artist;
}

```

****

**CategoryService**생성

```java
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
}
```

****

**CategoryRepository** 생성

```java
@Repository
public interface CategoryRepository  extends JpaRepository<Category, Long> {

}
```

****

**Category** 수정 

- `@Builder` 추가
- `addChildCategory()` 의 리턴 자료형을 `void` 에서 `Category`로
- `null comparison` 추가

```java
package com.megait.myhome.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder  // 이 부분!
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children;

    @ManyToMany
    @JoinTable(name="category_item",
    joinColumns = @JoinColumn(name = "category_id"),
    inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();


    public Category addChildCategory(Category child){ // 이 부분!
        if(child == null) // Null comparison 추가
            return this;
        if(children == null) // Null comparison 추가
             children = new ArrayList<>();
        children.add(child);
        child.parent = this;
        return this;
    }

    public Category setParent(Category parent){
        if(parent == null) // Null comparison 추가
            return this;

        this.parent = parent;
        parent.addChildCategory(this);
        return this;
    }
}

```

****

**CategoryService** 수정

```java
@PostConstruct
    @Transactional
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
```

****

**item 데이터.xlsx** 의 `book`, `album` 을 **CSV**로 내보내기

- 구분자를 `,`가 아닌 `|`로 할 것! (제어판 Region)
- UTF-8로 저장할 것 (메모장으로 다시 열어서 `다른 이름으로 저장` > 인코딩 UTF-8)
- book.CSV
- album.CSV
- 위에서 만들어진 두 개의 CSV 파일을 `\resources` 안에 추가 (바로 하단에 추가 하면 된다.)

****

**ItemRespository** 추가

```java
package com.megait.myhome.repository;


import com.megait.myhome.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRespository extends JpaRepository<Item, Long> {
}
```

****

**ItemService** 추가

```java
package com.megait.myhome.service;

import com.megait.myhome.domain.Album;
import com.megait.myhome.domain.Book;
import com.megait.myhome.domain.Item;
import com.megait.myhome.repository.ItemRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRespository itemRespository;

    @PostConstruct
    public void initBookItems() throws IOException {
        Resource resource = new ClassPathResource("book.CSV");
        List<Item> bookList= Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                .map(line->{
                    String[] split = line.split("\\|");
                    Book book = new Book();
                    book.setName(split[0]);
                    book.setImageUrl(split[1]);
                    book.setPrice(Integer.parseInt(split[2]));
                    return book;
                }).collect(Collectors.toList());
        itemRespository.saveAll(bookList);


    }

    @PostConstruct
    public void initAlbumItems() throws IOException {
        Resource resource = new ClassPathResource("album.CSV");
        List<Item> itemList= Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                .map(line->{
                    String[] split = line.split("\\|");
                    Album album = new Album();
                    album.setName(split[0]);
                    album.setImageUrl(split[1]);
                    album.setPrice(Integer.parseInt(split[2]));
                    return album;
                }).collect(Collectors.toList());
        itemRespository.saveAll(itemList);
    }

}
```



