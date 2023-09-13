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

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Song otherSong)) {
            return false;
        }
        if (!(this.id.equals(otherSong.id))) {
            return false;
        }
        if (!(this.artist.equals(otherSong.artist))) {
            return false;
        }
        if (!(this.title.equals(otherSong.title))) {
            return false;
        }
        if (!(this.label.equals(otherSong.label))) {
            return false;
        }
        return this.released == otherSong.released;
    }
}
