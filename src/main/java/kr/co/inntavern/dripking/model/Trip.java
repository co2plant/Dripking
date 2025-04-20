package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Trip extends Item {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date start_date;

    private Date end_date;
}