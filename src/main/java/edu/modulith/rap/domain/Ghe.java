package edu.modulith.rap.domain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity @Table(name="ghe")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ghe {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_ghe")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ma_phong", nullable = false)
  private Phong phong;

  @Column(name = "hang", nullable = false)
  private String hang;

  @Column(name = "cot", nullable = false)
  private Integer cot;

  @Column(name = "nhan_ghe", nullable = false)
  private String nhanGhe;   // A01…

  @Column(name = "loai_ghe", nullable = false)
  private String loaiGhe;   // Thường / VIP / COUPLE

  @Column(name = "nhom_couple")
  private String nhomCouple;

  @Column(name = "hoat_dong", nullable = false)
  private Boolean hoatDong;

  @Column(name = "heso_gia", nullable = false)
  private BigDecimal heSoGia;

  @Column(name = "couple_role")
  private String coupleRole;
}
