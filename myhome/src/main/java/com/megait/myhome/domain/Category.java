package com.megait.myhome.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

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
