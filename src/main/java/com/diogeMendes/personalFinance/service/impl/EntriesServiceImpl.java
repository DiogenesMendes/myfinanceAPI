package com.diogeMendes.personalFinance.service.impl;

import com.diogeMendes.personalFinance.exception.BusinessExeception;
import org.springframework.transaction.annotation.Transactional;
import com.diogeMendes.personalFinance.model.entity.Entries;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;
import com.diogeMendes.personalFinance.model.repository.EntriesRepository;
import com.diogeMendes.personalFinance.service.EntriesService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class EntriesServiceImpl implements EntriesService {

    private EntriesRepository repository;

    public EntriesServiceImpl(EntriesRepository repository){
        this.repository = repository;
    }

    @Override
    @Transactional
    public Entries save(Entries entries) {
        validateEntries(entries);
        entries.setEntriesStatus(EntriesStatus.PENDING);
        return  repository.save(entries);
    }

    @Override
    @Transactional
    public Entries update(Entries entries) {
        Objects.requireNonNull(entries.getId());
        return repository.save(entries);
    }

    @Override
    @Transactional
    public void delete(Entries entries) {
        Objects.requireNonNull(entries.getId());
        repository.delete(entries);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entries> find(Entries entries) {
        Example example = Example.of(entries, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example);
    }

    @Override
    public void updateStatus(Entries entries, EntriesStatus status) {
        entries.setEntriesStatus(status);
        update(entries);
    }

    @Override
    public void validateEntries(Entries entries) {
        if(entries.getDescription().isEmpty() || entries.getDescription().trim().equals("")){
            throw new BusinessExeception("enter a valid description");
        }
        if(entries.getMount().equals(null) || entries.getMount() < 1 || entries.getMount() > 12){
            throw new BusinessExeception("inform a valid month");
        }
        if(entries.getYear().equals(null) || entries.getYear().toString().length() != 4){
            throw new BusinessExeception("inform a valid year");
        }
        if (entries.getUser().equals(null) || entries.getUser().getId().equals(null)){
            throw new BusinessExeception("inform a valid user");
        }
        if (entries.getValue().equals(null) || entries.getValue().compareTo(BigDecimal.ZERO) < 1){
            throw new BusinessExeception("iform a valid value");
        }
        if (entries.getEntriesType().equals(null)){
            throw new BusinessExeception("inform a valid entries type");
        }
    }
}