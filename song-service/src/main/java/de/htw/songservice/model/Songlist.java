package de.htw.songservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "songlists")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "userId", "name", "isPrivate", "songs"})
public class Songlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private String name;
    private boolean isPrivate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "songlists_songs",
            joinColumns = {@JoinColumn(name = "songlist_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "song_id", referencedColumnName = "id")}
    )
    @JsonProperty("songList")
    private List<Song> songs;

    @JsonProperty(value = "isPrivate")
    public boolean isPrivate() {
        return isPrivate;
    }
}
