package com.diogeMendes.personalFinance.service.impl;

import com.diogeMendes.personalFinance.exception.BusinessExeception;
import com.diogeMendes.personalFinance.model.enums.EntriesType;
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
import java.util.Optional;

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
        if(entries.getDescription() == null || entries.getDescription().trim().equals("")){
            throw new BusinessExeception("enter a valid description");
        }
        if(entries.getMount() == null || entries.getMount() < 1 || entries.getMount() > 12){
            throw new BusinessExeception("inform a valid month");
        }
        if(entries.getYear() == null || entries.getYear().toString().length() != 4){
            throw new BusinessExeception("inform a valid year");
        }
        if (entries.getUserId() == null  || entries.getUserId().getId() == null){
            throw new BusinessExeception("inform a valid user");
        }
        if (entries.getValue() == null  || entries.getValue().compareTo(BigDecimal.ZERO) < 1){
            throw new BusinessExeception("inform a valid value");
        }
        if (entries.getEntriesType() == null ){
            throw new BusinessExeception("inform a valid entries type");
        }
    }


    @Override
    public Optional<Entries> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public BigDecimal getBalanceByEntriesAndUser(Long id) {
        BigDecimal  revenue = repository.getBalance(id, EntriesType.REVENUE, EntriesStatus.PAID);
        BigDecimal  expense = repository.getBalance(id, EntriesType.EXPENSE, EntriesStatus.PAID);

        if(revenue == null){
            revenue = BigDecimal.ZERO;
        }
        if (expense == null){
            expense = BigDecimal.ZERO;
        }
        return  revenue.subtract(expense);
    }
}
