package edu.modulith.lichchieu.domain;
import edu.modulith.phim.domain.Phim;
import edu.modulith.rap.domain.Phong;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
@Entity @Table(name="lich_chieu")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LichChieu {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ma_lich_chieu")
  private Long maLichChieu;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ma_phim", nullable = false)
  private Phim phim;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ma_phong", nullable = true)
  private Phong phong;

  @Column(name = "ngay_bat_dau", nullable = false)
  private LocalDate ngayBatDau;

  @Column(name = "ngay_ket_thuc", nullable = false)
  private LocalDate ngayKetThuc;

  @Column(name = "gio_bat_dau", nullable = true)
  private LocalDateTime gioBatDau;

  @Column(name = "gio_ket_thuc", nullable = true)
  private LocalDateTime gioKetThuc;

  @Column(name = "gia_co_so", nullable = true)
  private BigDecimal giaCoSo;

  @Column(name = "dinh_dang", nullable = true)
  private String dinhDang;

  @Column(name = "hinh_thuc_dich", nullable = true)
  private String hinhThucDich;

  @Column(name = "trang_thai", nullable = true)
  private String trangThai;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
