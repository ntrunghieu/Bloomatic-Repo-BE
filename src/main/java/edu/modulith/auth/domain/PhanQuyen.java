package edu.modulith.auth.domain;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="phan_quyen")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PhanQuyen {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_phan_quyen")
  private Long id;

  @Column(name = "quyen", nullable = false)
  private String quyen;
}
