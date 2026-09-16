package com.fabhotels.entity;

import com.fabhotels.enums.AvailabilityStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "room_availability",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_room_availability_date",
                        columnNames = {"room_id", "availability_date"}
                )
        }
)
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "room_id",
            nullable = false
    )
    private Room room;

    @Column(
            name = "availability_date",
            nullable = false
    )
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    private AvailabilityStatus status;

    public RoomAvailability() {
    }

    public Long getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getDate() {
        return date;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }
}