package edu.modulith.thanhtoan.service;
import edu.modulith.auth.domain.TaiKhoan;
import edu.modulith.auth.domain.TaiKhoanRepo;
import edu.modulith.datve.domain.ChiTietDatVe;
import edu.modulith.datve.domain.ChiTietDatVeRepo;
import edu.modulith.datve.domain.DatVe;
import edu.modulith.datve.domain.DatVeRepo;
import edu.modulith.lichchieu.domain.LichChieu;
import edu.modulith.lichchieu.domain.LichChieuRepo;
import edu.modulith.rap.domain.Ghe;
import edu.modulith.rap.domain.GheRepo;
import edu.modulith.thanhtoan.domain.ThanhToan;
import edu.modulith.thanhtoan.domain.ThanhToanRepo;
import edu.modulith.thanhtoan.dto.CapturePaypalRequest;
import edu.modulith.thanhtoan.dto.CreatePaypalOrderRequest;
import edu.modulith.thanhtoan.dto.CreatePaypalOrderResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThanhToanService {

    private final RestTemplate restTemplate;
    private final LichChieuRepo lichChieuRepo;
    private final GheRepo gheRepo;
    private final DatVeRepo datVeRepo;
    private final ChiTietDatVeRepo chiTietDatVeRepo;
    private final ThanhToanRepo thanhToanRepo;
    private final TaiKhoanRepo taiKhoanRepo;
    private final PaypalClient paypalClient; // lớp wrapper call PayPal

    @Transactional
    public CreatePaypalOrderResponse createOrder(Long userId, CreatePaypalOrderRequest req) throws Exception {
        TaiKhoan tk = taiKhoanRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        LichChieu lc = lichChieuRepo.findById(req.lichChieuId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch chiếu"));

        // 1. Lấy danh sách ghế
        List<Ghe> gheList = gheRepo.findAllById(req.seatIds());
        if (gheList.size() != req.seatIds().size()) {
            throw new IllegalArgumentException("Một số ghế không tồn tại");
        }

        // 2. Tính tổng tiền từ BE (không tin FE)
        BigDecimal base = lc.getGiaCoSo();
        BigDecimal total = gheList.stream()
                .map(g -> base.multiply(g.getHeSoGia()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Optional: so sánh với clientAmount
        if (req.clientAmount() != null &&
                req.clientAmount().compareTo(total) != 0) {
            throw new IllegalArgumentException("Số tiền không khớp");
        }

        // 3. Tạo DatVe PENDING
        DatVe dv = new DatVe();
        dv.setTaiKhoan(tk);
        dv.setLichChieu(lc);
        dv.setTongTien(total);
        dv.setTrangThai("PENDING");
        dv.setIdempotencyKey(UUID.randomUUID().toString());
        dv = datVeRepo.save(dv);

        // 4. Tạo ChiTietDatVe cho từng ghế
        for (Ghe ghe : gheList) {
            ChiTietDatVe ct = new ChiTietDatVe();
            ct.setDatVe(dv);
            ct.setLichChieu(lc);
            ct.setGhe(ghe);
            ct.setNhanGhe(ghe.getNhanGhe());
            ct.setGiaChot(base.multiply(ghe.getHeSoGia()));
            ct.setLoaiGhe(ghe.getLoaiGhe());
            chiTietDatVeRepo.save(ct);
        }

        // 5. Tạo record ThanhToan ở trạng thái PENDING
        ThanhToan tt = new ThanhToan();
        tt.setMaDatVe(dv.getId());
        tt.setSoTien(total);
        tt.setCongGiaoDich("PAYPAL");
        tt.setHinhThuc("ONLINE");
        tt.setTrangThai("PENDING");
        tt.setNgayTao(LocalDateTime.now());
        tt.setCreatedAt(LocalDateTime.now());
        tt.setIdempotencyKey(UUID.randomUUID().toString());
        tt = thanhToanRepo.save(tt);

        // 6. CHUYỂN SANG USD ĐỂ GỌI PAYPAL
        BigDecimal exchangeRate = new BigDecimal("26000"); // 1 USD = 26,000 VND (tạm)
        BigDecimal amountUsd = total.divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);

        // 6. Gọi PayPal tạo order
        PaypalClient.PaypalCreateOrderResult paypalResult =
                paypalClient.createOrder(amountUsd, "USD", tt.getMaGiaoDich().toString());

        // Lưu orderId (providerRef) để sau này capture
        tt.setProviderRef(paypalResult.orderId());
        thanhToanRepo.save(tt);

        return new CreatePaypalOrderResponse(
                tt.getMaGiaoDich(),
                paypalResult.orderId(),
                paypalResult.approveUrl()
        );
    }

    @Transactional
    public void captureOrder(Long userId, CapturePaypalRequest req) throws Exception {
        ThanhToan tt = thanhToanRepo.findById(req.thanhToanId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thanh toán"));

        if (!Objects.equals(tt.getProviderRef(), req.orderId())) {
            throw new IllegalArgumentException("orderId không khớp");
        }

        // 1. Gọi PayPal capture
        PaypalClient.PaypalCaptureResult result = paypalClient.captureOrder(req.orderId());

        if (result.success()) {
            tt.setTrangThai("SUCCEEDED");
            tt.setSucceededAt(LocalDateTime.now());
            tt.setLyDo(null);
            thanhToanRepo.save(tt);

            // 2. Cập nhật DatVe sang PAID
            DatVe dv = datVeRepo.findById(tt.getMaDatVe())
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy DatVe"));
            dv.setTrangThai("PAID");
            datVeRepo.save(dv);

            // TODO: tạo Ve cho từng ChiTietDatVe, bắn SSE cập nhật ghế BOOKED,...
        } else {
            tt.setTrangThai("FAILED");
            tt.setFailedAt(LocalDateTime.now());
            tt.setLyDo(result.errorMessage());
            thanhToanRepo.save(tt);
            throw new IllegalStateException("Thanh toán PayPal thất bại: " + result.errorMessage());
        }
    }
}
