package edu.modulith.theloaiphim.domain;
import edu.modulith.phim.domain.Phim;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "phim_the_loai")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhimTheLoai {

    @EmbeddedId
    private PhimTheLoaiId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Phim phim;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("theloaiId")
    @JoinColumn(name = "theloai_id")
    private TheLoaiPhim theLoai;
}

