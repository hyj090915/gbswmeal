package com.gbsw.meal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate mealDate;

    private String mealType;

    @Column(columnDefinition = "TEXT")
    private String dishNames;

    private String calInfo;
    private String ntrInfo;

    private LocalDateTime createdAt;

    @Transient
    private int likeCount;

    @Transient
    private int dislikeCount;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
