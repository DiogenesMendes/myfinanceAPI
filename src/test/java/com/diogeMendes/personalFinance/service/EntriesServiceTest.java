package com.diogeMendes.personalFinance.service;

import com.diogeMendes.personalFinance.exception.BusinessExeception;
import com.diogeMendes.personalFinance.model.entity.Entries;
import com.diogeMendes.personalFinance.model.entity.User;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;
import com.diogeMendes.personalFinance.model.enums.EntriesType;
import com.diogeMendes.personalFinance.model.repository.EntriesRepository;
import com.diogeMendes.personalFinance.model.repository.UserRepository;
import com.diogeMendes.personalFinance.service.impl.EntriesServiceImpl;
import com.diogeMendes.personalFinance.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class EntriesServiceTest {
    @SpyBean
    EntriesServiceImpl service;

    @MockBean
    EntriesRepository repository;

    @Test
    public void MustSaveAEntries(){
        //given
        Entries entries = createEntries();

        //when
        Mockito.doNothing().when(service).validateEntries(entries);
        Mockito.when(repository.save(entries)).thenReturn(
                Entries.builder()
                .id(1L)
                .year(2021)
                .mount(7)
                .description("teste de descrição")
                .value(BigDecimal.valueOf(100))
                .entriesType(EntriesType.EXPENSE)
                .entriesStatus(EntriesStatus.PAID)
                .localDate(LocalDate.now())
                .build()
        );
       Entries savedEntries = service.save(entries);

        //then
        assertThat(savedEntries.getId()).isEqualTo(1L);
        Mockito.verify(repository,Mockito.times(1)).save(entries);
    }

    @Test
    public void mustDoesNotSaveEntriesWhenThrowValidateErrorTest() {
        //given
        Entries entries = createEntries();
        Mockito.doThrow(BusinessExeception.class).when(service).validateEntries(entries);

        //when
        Throwable execption = catchThrowableOfType( () -> service.save(entries), BusinessExeception.class );

        //then
        Mockito.verify(repository, Mockito.never()).save(entries);
        assertThat(execption).isNotNull();
    }
    @Test
    public void mustUpdateEntries() {
        //given
        Entries entries = createEntries();
        entries.setId(1l);
        entries.setEntriesStatus(EntriesStatus.PENDING);

        Mockito.doNothing().when(service).validateEntries(entries);

        Mockito.when(repository.save(entries)).thenReturn(entries);

        //when
        service.update(entries);

        //then
        Mockito.verify(repository, Mockito.times(1)).save(entries);

    }
    @Test
    public void mustThrowErrorWhenTryUpdateEntries() {
        //given
        Entries entries = createEntries();

        //when
        Throwable exception = catchThrowableOfType( () -> service.update(entries), NullPointerException.class );

        //then
        Mockito.verify(repository, Mockito.never()).save(entries);
        assertThat(exception).isNotNull();
    }
    @Test
    public void MustDeleteEntries() {
        //given
        Entries entries = createEntries();
        entries.setId(1l);

        //when
        service.delete(entries);

        //then
        Mockito.verify( repository, times(1) ).delete(entries);
    }

    @Test
    public void mustThrowErrorWhenTryDelteEntries() {

        //given
        Entries entries = createEntries();

        //when
        Throwable exception =  catchThrowableOfType( () -> service.delete(entries), NullPointerException.class );

        //then
        verify( repository, never() ).delete(entries);
        assertThat(exception).isNotNull();
    }
    @Test
    public void MustFilterEntries() {
        //given
        Entries entries = createEntries();
        entries.setId(1l);

        List<Entries> list = Arrays.asList(entries);
        when( repository.findAll(Mockito.any(Example.class)) ).thenReturn(list);

        //when
        List<Entries> result = service.find(entries);

        //then
        assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .contains(entries);

    }
    @Test
    public void mustUpdateStatus() {
        //given
        Entries entries = createEntries();
        entries.setId(1l);
        entries.setEntriesStatus(EntriesStatus.PENDING);

        EntriesStatus newStatus = EntriesStatus.PAID;
        doReturn(entries).when(service).update(entries);

        //when
        service.updateStatus(entries, newStatus);

        //given
        assertThat(entries.getEntriesStatus()).isEqualTo(newStatus);
        verify(service).update(entries);

    }
    @Test
    public void MustGetEntriesByID() {
        //given
        Entries entries = createEntries();
        entries.setId(1l);
        when(repository.findById(entries.getId())).thenReturn(Optional.of(entries));

        //when
        Optional<Entries> result =  service.getById(entries.getId());

        //then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void mustReturnNullWhenWhenEntriesDoesExist() {
        //given
        Entries entries = createEntries();
        entries.setId(1l);

        when( repository.findById(entries.getId()) ).thenReturn( Optional.empty() );

        //when
        Optional<Entries> resultado =  service.getById(entries.getId());

        //then
        assertThat(resultado.isPresent()).isFalse();
    }
    @Test
    public void mustThrowErrorWhenTryValidateEntries() {
        Entries entries = new Entries();

        Throwable exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("enter a valid description");

        entries.setDescription("");

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("enter a valid description");

        entries.setDescription("test");

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid month");

        entries.setMount(0);

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid month");

        entries.setMount(3);

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid year");

        entries.setYear(1);

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid year");

        entries.setYear(202);

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid year");

        entries.setYear(2020);

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid user");

        entries.setUserId(new User());

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid user");

        entries.getUserId().setId(1l);

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid value");

        entries.setValue(BigDecimal.ZERO);

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid value");

        entries.setValue(BigDecimal.valueOf(1));

        exception = catchThrowable( () -> service.validateEntries(entries) );
        assertThat(exception).
                isInstanceOf(BusinessExeception.class).
                hasMessage("inform a valid entries type");;

    }

    @Test
    public void deveObterSaldoPorUsuario() {
        //given
        Long userId = 1l;
        

        when( repository
                .getBalance(userId, EntriesType.REVENUE, EntriesStatus.PAID))
                .thenReturn(BigDecimal.valueOf(100));

        when( repository
                .getBalance(userId, EntriesType.EXPENSE, EntriesStatus.PAID))
                .thenReturn(BigDecimal.valueOf(50));

        //when
        BigDecimal saldo = service.getBalanceByEntriesAndUser(userId);

        //then
        assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));

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
