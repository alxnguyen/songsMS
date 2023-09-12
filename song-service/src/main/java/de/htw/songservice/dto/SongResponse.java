package de.htw.songservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SongResponse {
    private Integer id;
    private String title;
    private String artist;
    private String label;
    private int released;
}
