package edu.modulith.datve.domain;
import edu.modulith.auth.domain.TaiKhoan;
import edu.modulith.lichchieu.domain.LichChieu;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
@Entity @Table(name="dat_ve")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DatVe {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_dat_ve")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ma_tai_khoan", nullable = false)
  private TaiKhoan taiKhoan;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ma_lich_chieu", nullable = false)
  private LichChieu lichChieu;

  @Column(name = "tong_tien", nullable = false)
  private BigDecimal tongTien;

  @Column(name = "trang_thai", nullable = false)
  private String trangThai;   // PENDING/PAID/...

  @Column(name = "idempotency_key")
  private String idempotencyKey;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  @Version
  @Column(name = "version")
  private Integer version;
}
