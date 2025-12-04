package edu.modulith.ve.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="ve")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ve {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long maVe;
  private Long maChiTietDatVe;
  private String maSoVe;
  @Lob
  private String duLieuQR;
  private String chuKy;
  private String trangThai;
  private LocalDateTime thoiDiemPhatHanh;
  private LocalDateTime thoiDiemSuDung;
}
