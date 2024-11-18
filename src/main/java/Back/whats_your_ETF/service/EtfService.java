package Back.whats_your_ETF.service;

import Back.whats_your_ETF.apiPayload.GeneralException;
import Back.whats_your_ETF.apiPayload.code.status.ErrorStatus;
import Back.whats_your_ETF.dto.EtfRequest;
import Back.whats_your_ETF.entity.ETFStock;
import Back.whats_your_ETF.entity.Portfolio;
import Back.whats_your_ETF.entity.Stock;
import Back.whats_your_ETF.entity.User;
import Back.whats_your_ETF.repository.ETFStockRepository;
import Back.whats_your_ETF.repository.PortfolioRepository;
import Back.whats_your_ETF.repository.StockRepository;
import Back.whats_your_ETF.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EtfService {

    private final ETFStockRepository etfStockRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;


    @Transactional
    public void buyETF(Long userId, EtfRequest.etfInvestList etfInvestList) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 2. 새로운 포트폴리오 생성
        Portfolio portfolio = portfolioRepository.save(Portfolio.builder()
                .user(user)
                .title(etfInvestList.getTitle())
                .isEtf(true)
                .build());

        // 3. 총 투자 금액 계산
        long totalInvestment = etfInvestList.getEtfList().stream()
                .mapToLong(EtfRequest.etfInvest::getPrice)
                .sum();

        // 4. 투자 금액만큼 사용자 자산 감소
        if (user.getAsset() < totalInvestment) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_FUNDS); // 자산 부족 예외 처리
        }
        user.setAsset(user.getAsset() - totalInvestment);
        userRepository.save(user); // 사용자 자산 업데이트

        // 5. 포트폴리오의 투자 금액 업데이트
        portfolio.setInvestAmount(totalInvestment);
        portfolioRepository.save(portfolio);

        // 6. 각 ETF 투자 항목 저장
        etfInvestList.getEtfList().forEach(etfInvest -> {
            Stock stock = stockRepository.findByStockCode(etfInvest.getStockCode())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_NOT_FOUND));

            ETFStock etfStock = ETFStock.builder()
                    .portfolio(portfolio)
                    .stock(stock)
                    .percentage(etfInvest.getPercentage())
                    .purchasePrice(etfInvest.getPrice())
                    .build();

            etfStockRepository.save(etfStock);
        });
    }

    @Transactional
    public void sellETF(Long portfolioId) {
        // 1. 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PORTFOLIO_NOT_FOUND));

        User user = portfolio.getUser();

        // 2. 수익금 계산: 투자금 * (수익률 / 100)
        long investAmount = portfolio.getInvestAmount();
        double revenuePercentage = portfolio.getRevenue() != null ? portfolio.getRevenue() : 0;
        long profit = (long) (investAmount * (revenuePercentage / 100.0));

        // 3. 사용자 자산 업데이트 (투자금 + 수익금 반환)
        long totalRefund = investAmount + profit;
        user.setAsset(user.getAsset() + totalRefund);
        userRepository.save(user);

        // 4. 포트폴리오에 속한 모든 ETFStock 삭제
        etfStockRepository.deleteAll(portfolio.getEtfStocks());

        // 5. 포트폴리오 삭제
        portfolioRepository.delete(portfolio);
    }

}
