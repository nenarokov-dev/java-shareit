package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(" select b from Booking b" +
            " where b.booker.id=?1 " +
            " order by b.start desc ")
    List<Booking> findAllByBookerSortByStartDate(Long booker);

    @Query(" select b from Booking b" +
            " where b.item.owner.id=?1 " +
            " order by b.start desc ")
    List<Booking> findAllByOwnerSortByStartDate(Long owner);

    @Query(" select b from Booking b" +
            " where b.item.id=?1 " +
            " and b.status='APPROVED'" +
            " order by b.start")
    List<Booking> findInBookingNowByItemId(Long itemId);


    @Query(" select b from Booking b" +
            " where b.item.id=?1 " +
            " and b.booker.id=?2 " +
            " and b.status='APPROVED'" +
            " order by b.start")
    List<Booking> findAllBookingsByItemIdAndUserId(Long itemId, Long userId);

}
