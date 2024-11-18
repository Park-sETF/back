package Back.whats_your_ETF.controller;

import Back.whats_your_ETF.dto.EtfRequest;
import Back.whats_your_ETF.service.EtfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/etf")
public class EtfController {

    private final EtfService etfService;

    // ETF 투자하기
    @PostMapping("/buy/{user_id}")
    public ResponseEntity<Void> buyETF(@PathVariable("user_id") Long userId,
                                       @RequestBody EtfRequest.etfInvestList etfInvestList) {
        etfService.buyETF(userId, etfInvestList);
        return ResponseEntity.ok().build();  // Response는 없음
    }
}