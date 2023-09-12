package de.htw.songservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "songs")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String artist;
    private String label;
    private int released;
}
