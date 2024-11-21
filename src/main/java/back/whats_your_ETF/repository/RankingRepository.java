package back.whats_your_ETF.repository;

import back.whats_your_ETF.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Optional<Ranking> findByStockCode(String stockCode);
    Optional<Ranking> findByStockName(String stockName);



}