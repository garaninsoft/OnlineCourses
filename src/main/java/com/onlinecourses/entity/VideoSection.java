package com.onlinecourses.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "video_sections")
public class VideoSection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_title")
    private String videoTitle;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
}
