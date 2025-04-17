package kr.co.inntavern.dripking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class Trip extends Item {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date start_date;

    private Date end_date;
}