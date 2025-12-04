package edu.modulith.theloaiphim.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "the_loai_phim")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheLoaiPhim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_the_loai")
    private Long id;

    @Column(name = "ten_the_loai", nullable = false)
    private String tenTheLoai;
}

