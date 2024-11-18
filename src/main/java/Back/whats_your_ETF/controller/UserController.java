package Back.whats_your_ETF.controller;

import Back.whats_your_ETF.dto.ETFlistResponse;
import Back.whats_your_ETF.dto.TradeHistoryResponse;
import Back.whats_your_ETF.entity.User;
import Back.whats_your_ETF.dto.UserResponse;
import Back.whats_your_ETF.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userinfo")
public class UserController {

    private final UserService userService;

    // 사용자 정보 불러오기
    @GetMapping("/{user_id}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable("user_id") Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //1.2.1 : 나의 거래내역 가져오기
    @GetMapping("/tradehistory/{user_id}")
    public ResponseEntity<List<TradeHistoryResponse>> getTradeHistory(@PathVariable("user_id") Long userId) {
        return userService.getTradeHistoryById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //1.3.1 : 나의 ETF목록 가져오기
    @GetMapping("/etf/list/{user_id}")
    public ResponseEntity<ETFlistResponse> getUserETFlist(@PathVariable("user_id") Long userId) {
        return userService.getUserETFlistById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
