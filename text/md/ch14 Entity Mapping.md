# Entity Mapping

- 객체와 테이블 매핑: @Entity, @Table 
- 필드와 컬럼 매핑: @Column 
- 기본 키 매핑: @Id 
- 연관관계 매핑: @ManyToOne,@JoinColumn



## @Entity

JPA로 테이블에 매핑할 클래스에 정의

**기본 생성자 필수** (public, protected)

final, enum, interface, inner class 모두 안됨. 평범한 클래스여야 함.



## @Table

Entity 와 매핑할 특정 테이블을 지정

| 속성              | 기능                              | default     |
| ----------------- | --------------------------------- | ----------- |
| name              | 매핑할 테이블 이름                | 엔티티 이름 |
| catalog           | DB catalog 이름                   |             |
| schema            | DB schema 이름                    |             |
| uniqueConstraints | DDL 생성 시 유니크 제약 조건 생성 |             |

#### uniqueConstraints 의 예

```java
@Column(name="column" , unique=true)
int column;
```

이렇게 하면 특정 컬럼 1개에만 unique가 적용됨.

여러 컬럼을 unique 적용하려면...

```java
@Entity
@Table(
	name="entities",
	uniqueConstraints={
		@UniqueConstraint(
			columnNames={"column1","column2"}
		)
	}
)
@Data
public class Entity1 {
	@Column(name="column1")
	int column1;
	@Column(name="column2")
	int column2;
}
```



