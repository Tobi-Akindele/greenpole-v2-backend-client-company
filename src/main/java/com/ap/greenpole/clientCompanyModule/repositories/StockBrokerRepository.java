package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.StockBroker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StockBrokerRepository extends CrudRepository<StockBroker, Long> {

  @Query(value = "SELECT * FROM stock_broker WHERE stock_broker_name = ?1", nativeQuery = true)
  StockBroker findByStockBrokerName(String stockBrokerName);
}
