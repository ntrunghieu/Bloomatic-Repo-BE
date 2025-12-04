package edu.modulith.auth.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;
@Entity @Table(name="tai_khoan")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TaiKhoan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_tai_khoan")
  private Long maTaiKhoan;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ma_phan_quyen")
  private PhanQuyen phanQuyen;

  @Column(name = "ho_ten")
  private String hoTen;

  @Column(name = "ngay_sinh")
  private LocalDate ngaySinh;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "dia_chi")
  private String diaChi;

  @Column(name = "so_dien_thoai")
  private String soDienThoai;

  @Column(name = "mat_khau")
  private String matKhau;

  @Column(name = "trang_thai")
  private String trangThai; // ACTIVE / LOCKED...

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
