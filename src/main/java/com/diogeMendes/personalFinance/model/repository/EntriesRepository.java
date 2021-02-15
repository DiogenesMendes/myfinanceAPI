package com.diogeMendes.personalFinance.model.repository;

import com.diogeMendes.personalFinance.model.entity.Entries;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;
import com.diogeMendes.personalFinance.model.enums.EntriesType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface EntriesRepository extends JpaRepository<Entries, Long> {
    @Query( value =
            " select sum(l.value) from Entries l join l.userId u "
                    + " where u.id = :userId and l.entriesType =:type and l.entriesStatus = :status group by u " )
    BigDecimal getBalance(@Param("userId") Long userId, @Param("type") EntriesType type, @Param("status") EntriesStatus status);
}
