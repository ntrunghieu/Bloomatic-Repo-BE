package edu.modulith.rap.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="phong")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Phong {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_phong")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ma_rap", nullable = false)
  private Rap rap;

  @Column(name = "ten_phong", nullable = false)
  private String tenPhong;

  @Column(name = "loai_phong", nullable = false)
  private String loaiPhong;

  @Column(name = "trang_thai", nullable = false)
  private Boolean trangThai;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  @Column(name = "so_hang", nullable = false) // Tên cột mới
  private Integer hang;

  @Column(name = "so_cot", nullable = false)   // Tên cột mới
  private Integer cot;
}
