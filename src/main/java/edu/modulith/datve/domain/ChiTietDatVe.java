package edu.modulith.datve.domain;
import edu.modulith.lichchieu.domain.LichChieu;
import edu.modulith.rap.domain.Ghe;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name="chi_tiet_dat_ve")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChiTietDatVe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ma_chi_tiet_dat_ve")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ma_dat_ve", nullable = false)
  private DatVe datVe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ma_suat_chieu", nullable = false)
  private LichChieu lichChieu;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ma_ghe", nullable = false)
  private Ghe ghe;

  @Column(name = "nhan_ghe", nullable = false)
  private String nhanGhe;

  @Column(name = "gia_chot", nullable = false)
  private BigDecimal giaChot;

  @Column(name = "loai_ghe", nullable = false)
  private String loaiGhe;

}
