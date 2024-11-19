package kr.co.inntavern.dripking.model;

import com.fasterxml.jackson.annotation.JsonTypeId;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trip_id;
    @Column
    private String name;
    @Column(length = 1000)
    private String description;
    @Column
    private Date start_date;
    @Column
    private Date end_date;
}

/*
*
* Trip{
	trip_id Long PK
	name varchar(255)
	description text
	start_date datetime
	end_date datetime
}
* */
