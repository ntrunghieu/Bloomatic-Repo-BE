package edu.modulith.rap.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="rap")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Rap {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_rap")
  private Long id;

  @Column(name = "ten_rap", nullable = false)
  private String tenRap;

  @Column(name = "dia_chi", nullable = false)
  private String diaChi;

  @Column(name = "dien_thoai")
  private String dienThoai;

  @Column(name = "email")
  private String email;

  @Column(name = "trang_thai", nullable = false)
  private Boolean trangThai;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
