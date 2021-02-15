package com.diogeMendes.personalFinance.model.repository;

import com.diogeMendes.personalFinance.model.entity.Entries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntriesRepository extends JpaRepository<Entries, Long> {

}
