package com.diogeMendes.personalFinance.service;

import com.diogeMendes.personalFinance.model.entity.Entries;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;

import java.util.List;

public interface EntriesService {

    Entries save ( Entries entries);
    Entries update( Entries entries);
    void delete (Entries entries);
    List<Entries> find (Entries entries);
    void updateStatus (Entries entries, EntriesStatus status);
    void validateEntries(Entries entries);

}
