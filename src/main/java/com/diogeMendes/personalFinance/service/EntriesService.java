package com.diogeMendes.personalFinance.service;

import com.diogeMendes.personalFinance.model.entity.Entries;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EntriesService {

    Entries save ( Entries entries);
    Entries update( Entries entries);
    void delete (Entries entries);
    List<Entries> find (Entries entries);
    void updateStatus (Entries entries, EntriesStatus status);
    void validateEntries(Entries entries);
    Optional<Entries> getById(Long id);
    BigDecimal getBalanceByEntriesAndUser (Long id);

}
