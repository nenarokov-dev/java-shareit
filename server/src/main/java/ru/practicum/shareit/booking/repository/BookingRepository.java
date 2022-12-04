package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBooker_IdOrderByStartDesc(Long booker);

    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long owner);

    List<Booking> findBookingsByItem_IdAndStatusOrderByStart(Long itemId, BookingStatus status);

    List<Booking> findBookingsByItem_IdAndBooker_IdAndStatusOrderByStart(Long itemId, Long userId, BookingStatus status);

}
