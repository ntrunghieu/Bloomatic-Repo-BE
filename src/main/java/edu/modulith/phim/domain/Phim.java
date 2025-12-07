package edu.modulith.phim.domain;
import edu.modulith.theloaiphim.domain.PhimTheLoai;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "phim")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phim {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_phim")
  private Long id;

  @Column(name = "ten_phim", nullable = false)
  private String tenPhim;

  @Column(name = "dao_dien")
  private String daoDien;

  @Column(name = "dien_vien", columnDefinition = "TEXT")
  private String dienVien;

  @Column(name = "thoi_luong", nullable = false)
  private Integer thoiLuong;    // phút

  @Column(name = "quoc_gia")
  private String quocGia;

  @Column(name = "ngay_khoi_chieu")
  private LocalDate ngayKhoiChieu;

  @Column(name = "ngay_ket_thuc")
  private LocalDate ngayKetThuc;

  @Column(name = "poster_url")
  private String posterUrl;

  @Column(name = "trailer_url")
  private String trailerUrl;

  @Column(name = "mo_ta", columnDefinition = "TEXT")
  private String moTa;

  @Column(name = "trang_thai", nullable = false)
  private String trangThai;      // 'Sắp chiếu' / 'Đang chiếu' / 'Đã chiếu'

  @Column(name = "gioi_han_tuoi")
  private String gioiHanTuoi;    // P / T13 / T16 / T18

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "phim", fetch = FetchType.LAZY)
  private Set<PhimTheLoai> phimTheLoaiSet;
}

