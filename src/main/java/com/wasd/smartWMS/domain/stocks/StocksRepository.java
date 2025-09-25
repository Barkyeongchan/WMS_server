// 실제 DB에서 CRUD(create, read, update, delete)를 수행함
package com.wasd.smartWMS.domain.stocks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StocksRepository extends JpaRepository<Stocks, Long> {

    @Query("SELECT r FROM Stocks r ORDER BY r.id DESC")
    List<Stocks> findAllDesc();
}
