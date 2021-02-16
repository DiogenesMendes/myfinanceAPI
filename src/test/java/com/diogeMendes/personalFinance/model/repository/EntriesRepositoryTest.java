package com.diogeMendes.personalFinance.model.repository;

import com.diogeMendes.personalFinance.model.entity.Entries;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;
import com.diogeMendes.personalFinance.model.enums.EntriesType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static  org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class EntriesRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    EntriesRepository repository;

    @Test
    public void mustSaveEntries(){
        //given
        Entries entries = createEntries();

        //when
        entries = entityManager.persist(entries);

        //then
        assertThat(entries.getId()).isNotNull();

    }
    @Test
    public void deleteEntriesTest(){
        //given
        Entries entries = createAndPersistEntries();
        Entries foundEntries =  entityManager.find(Entries.class, entries.getId());

        //when
        repository.delete(foundEntries);

        //then
        Entries deletedProduct = entityManager.find(Entries.class, entries.getId());
        assertThat(deletedProduct).isNull();
    }

    @Test
    public void MustUpdateEntries(){
        //given
        Entries entries = createAndPersistEntries();

        //when
        entries.setYear(2022);
        entries.setMount(2);
        entries.setEntriesStatus(EntriesStatus.CANCELED);
        repository.save(entries);

        //then
        Entries updateEntries = entityManager.find(Entries.class, entries.getId());
        assertThat(updateEntries.getYear()).isEqualTo(2022);
        assertThat(updateEntries.getMount()).isEqualTo(2);
        assertThat(updateEntries.getEntriesStatus()).isEqualTo(EntriesStatus.CANCELED);

    }
    @Test
    public void MustFindEntrieById(){
        //given
        Entries entries = createAndPersistEntries();

        //when
        Optional<Entries> findEntries = repository.findById(entries.getId());

        //then
        assertThat(findEntries.isPresent()).isTrue();
    }

    private Entries createAndPersistEntries(){
        return entityManager.persist(createEntries());
    }

    private Entries createEntries(){
        return Entries.builder()
                .year(2021)
                .mount(7)
                .description("teste de descrição")
                .value(BigDecimal.valueOf(100))
                .entriesType(EntriesType.EXPENSE)
                .entriesStatus(EntriesStatus.PAID)
                .localDate(LocalDate.now())
                .build();
    }

}
