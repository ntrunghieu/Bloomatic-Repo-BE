package edu.modulith.thanhtoan.controller;
import edu.modulith.auth.context.AuthenticationFacade;
import edu.modulith.thanhtoan.domain.ThanhToanRepo;
import edu.modulith.thanhtoan.dto.CapturePaypalRequest;
import edu.modulith.thanhtoan.dto.CreatePaypalOrderRequest;
import edu.modulith.thanhtoan.dto.CreatePaypalOrderResponse;
import edu.modulith.thanhtoan.service.ThanhToanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/thanh-toan/paypal")
@CrossOrigin(origins = "http://localhost:4200")
public class ThanhToanController {
  private final ThanhToanRepo repo;

  private final ThanhToanService thanhToanService;
  private final AuthenticationFacade auth;
  @GetMapping
  public List<?> list() {
    return repo.findAll();
  }

  @PostMapping("/create-order")
  public CreatePaypalOrderResponse createOrder(@RequestBody CreatePaypalOrderRequest req) throws Exception {
    Long userId = auth.getCurrentUserId();
    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User chưa đăng nhập");
    }
    return thanhToanService.createOrder(userId, req);
  }

  @PostMapping("/capture")
  public ResponseEntity<Void> capture(@RequestBody CapturePaypalRequest req) throws Exception {
    Long userId = auth.getCurrentUserId();
    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User chưa đăng nhập");
    }
    thanhToanService.captureOrder(userId, req);
    return ResponseEntity.ok().build();
  }
}
