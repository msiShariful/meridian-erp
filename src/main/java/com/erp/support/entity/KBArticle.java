package com.erp.support.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sup_kb_articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KBArticle extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 80)
    private String category;

    @Column(length = 8000)
    private String body;

    @Column(length = 255)
    private String tags;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = false;

    @Column(nullable = false)
    @Builder.Default
    private int helpfulYes = 0;

    @Column(nullable = false)
    @Builder.Default
    private int helpfulNo = 0;

    public int getHelpfulScore() {
        return helpfulYes - helpfulNo;
    }
}
