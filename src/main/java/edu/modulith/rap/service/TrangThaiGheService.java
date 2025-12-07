package edu.modulith.rap.service;

import edu.modulith.rap.domain.GheRepo;
import edu.modulith.rap.domain.Phong;
import edu.modulith.rap.domain.PhongRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrangThaiGheService {
    private final GheRepo gheRepo;
    private final PhongRepo phongRepo;

    public Map<Long, Boolean> getRoomsConfigStatus() {
        // 1. Lấy danh sách tất cả mã phòng
        List<Long> maPhongList = phongRepo.findAll().stream()
                .map(Phong::getId)
                .collect(Collectors.toList());

        // 2. Tính toán trạng thái cấu hình cho từng phòng
        return maPhongList.stream()
                .collect(Collectors.toMap(
                        maPhong -> maPhong,
                        maPhong -> gheRepo.countByPhong_Id(maPhong) > 0
                ));
    }
}
