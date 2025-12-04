package edu.modulith.rap.service;
import edu.modulith.rap.domain.Rap;
import edu.modulith.rap.domain.RapRepo;
import edu.modulith.rap.dto.RapDto;
import edu.modulith.rap.dto.TaoRapReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RapService {
  // TODO: implement business logic

    private final RapRepo rapRepo;

    private RapDto mapToDto(Rap r) {
        return new RapDto(
                r.getId(),
                r.getTenRap(),
                r.getDiaChi(),
                r.getDienThoai(),
                r.getEmail(),
                r.getTrangThai(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }

    public List<RapDto> danhSach() {
        return rapRepo.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public RapDto chiTiet(Long maRap) {
        Rap r = rapRepo.findById(maRap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp với mã " + maRap));
        return mapToDto(r);
    }

    public RapDto taoRap(TaoRapReq req) {
        Rap entity = new Rap();
        entity.setTenRap(req.tenRap().trim());
        entity.setDiaChi(req.diaChi().trim());
//        entity.setDienThoai(req.dienThoai());
//        entity.setEmail(req.email());
        entity.setTrangThai(true); // mặc định đang hoạt động

        Rap saved = rapRepo.save(entity);
        return mapToDto(saved);
    }

    public RapDto capNhat(Long id, TaoRapReq req) {
        Rap r = rapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp với mã " + id));

        r.setTenRap(req.tenRap().trim());
        r.setDiaChi(req.diaChi().trim());
//        r.setDienThoai(req.dienThoai());
//        r.setEmail(req.email());

        Rap saved = rapRepo.save(r);
        return mapToDto(saved);
    }

    public void xoaRap(Long id) {
        Rap r = rapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp với mã " + id));

        // TODO (nếu cần): kiểm tra còn phòng / suất chiếu liên kết hay không trước khi xóa
        rapRepo.delete(r);
    }
}
