package com.diogeMendes.personalFinance.model.entity;


import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.diogeMendes.personalFinance.model.enums.EntriesStatus;
import com.diogeMendes.personalFinance.model.enums.EntriesType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@Entity
@Table(name = "expensePost")
@Builder
@Data
public class Entries {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "id" )
	private Long id;

	@Column(name = "Description")
	private String description;
	
	@Column (name = "mouth" ) 
	private Integer mount;
	
	@Column (name = "year" )
	private Integer year;
	
	@ManyToOne
	@JoinColumn(name ="id_user")
	private User user;
	
	@Column(name = "value")
	private BigDecimal value; 
	
	
	@Column(name = "local_date")
	@Convert( converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate localDate;
	
	@Column(name = "type")
	@Enumerated(value = EnumType.STRING)
	private EntriesType EntriesType;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private EntriesStatus EntriesStatus;

}
