package edu.modulith.thanhtoan.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="thanh_toan")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ThanhToan {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long maGiaoDich;
  private Long maDatVe;
  private java.math.BigDecimal soTien;
  private String congGiaoDich;
  private String hinhThuc;
  private String trangThai;
  private LocalDateTime ngayTao;
  private LocalDateTime createdAt;
  private LocalDateTime succeededAt;
  private LocalDateTime failedAt;
  private String providerRef;
  private String lyDo;
  private String idempotencyKey;
}
