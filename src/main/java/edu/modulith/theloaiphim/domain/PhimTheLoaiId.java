package edu.modulith.theloaiphim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhimTheLoaiId implements Serializable {

    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "theloai_id")
    private Long theloaiId;
}
