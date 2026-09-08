package vn.edu.epu.quanlyhoso.common.test;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    // Ai có Token (bất kể Role gì) cũng vào được đây
    @GetMapping("/user")
    public ResponseEntity<String> sayHelloUser() {
        return ResponseEntity.ok("Xin chào! Bạn đã trình thẻ VIP (Token) thành công!");
    }

    // Cái này tý nữa cấu hình chỉ ADMIN mới được vào
    @GetMapping("/admin")
    public ResponseEntity<String> sayHelloAdmin() {
        return ResponseEntity.ok("Chào ADMIN!");
    }
}
