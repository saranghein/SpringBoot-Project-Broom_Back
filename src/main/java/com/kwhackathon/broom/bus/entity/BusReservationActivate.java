package com.kwhackathon.broom.bus.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bus_reservation_activate")
public class BusReservationActivate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_reservation_activate_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "is_activated", nullable = false)
    @ColumnDefault("false")
    private boolean isActivated;

    public void updateActivatedStatus() {
        this.isActivated = !this.isActivated;
    }
}
