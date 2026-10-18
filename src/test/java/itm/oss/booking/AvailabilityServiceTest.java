package itm.oss.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AvailabilityServiceTest {
    private Room room(String id, int capacity) {
        return new Room(id, "Test room " + id, capacity, Set.of());
    }

    private Booking booking(String roomId, String start, String end, BookingStatus status) {
        return new Booking(1, "test", roomId, sToTime(start),
                sToTime(end), 2, "Study", status);
    }

    private LocalDateTime sToTime(String time) {
        return LocalDateTime.parse(time);
    }

    @Test
    @DisplayName("Check room availability by capacity and time")
    void returnAvailableRoom() {
        List<Room> rooms = List.of(room("r1", 4), room("r2", 2),
                room("r3", 6));

        List<Booking> bookings = List.of(booking("r3", "2026-10-18T09:00", "2026-10-18T10:30", BookingStatus.ACTIVE));

        List<Room> result = AvailabilityService.findAvailableRooms(rooms, bookings, sToTime("2026-10-18T10:00"),
                sToTime("2026-10-18T11:00"), 4);

        assertEquals(1, result.size());
        assertEquals("r1", result.get(0).getId());
    }

    @Test
    @DisplayName("Check overlapping and touching book handling")
    void checkOverlaplogic() {
        List<Room> rooms = List.of(room("r1", 4), room("r2", 6));

        List<Booking> bookings = List.of(booking("r1", "2026-10-18T09:00", "2026-10-18T10:00", BookingStatus.ACTIVE),
                booking("r2", "2026-10-18T11:00", "2026-10-18T12:00", BookingStatus.ACTIVE));
        List<Room> result = AvailabilityService.findAvailableRooms(rooms, bookings, sToTime("2026-10-18T10:00"),
                sToTime("2026-10-18T11:00"), 4);
        // Request touches r1's end and overlappes with r2

        assertEquals(2, result.size());
        assertEquals("r1", result.get(0).getId());
        assertEquals("r2", result.get(1).getId());
    }

    @Test
    @DisplayName("Check when result is empty")

    void checkIfNoResult() {
        List<Room> rooms = List.of(room("r1", 2), room("r2", 6));

        List<Booking> bookings = List.of(booking("r1", "2026-10-18T09:00", "2026-10-18T10:00", BookingStatus.ACTIVE),
                booking("r2", "2026-10-18T11:00", "2026-10-18T12:00", BookingStatus.ACTIVE));
        List<Room> result = AvailabilityService.findAvailableRooms(rooms, bookings, sToTime("2026-10-18T10:00"),
                sToTime("2026-10-18T11:30"), 4);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Check cancelled book availability")
    void checkCancelled() {
        List<Room> rooms = List.of(room("r1", 6));

        List<Booking> bookings = List.of(booking("r1", "2026-10-18T09:00",
                "2026-10-18T10:00", BookingStatus.CANCELLED));

        List<Room> result = AvailabilityService.findAvailableRooms(rooms, bookings, sToTime("2026-10-18T09:00"),
                sToTime("2026-10-18T11:00"), 4);

        assertEquals(1, result.size());
        assertEquals("r1", result.get(0).getId());
    }

    @Test
    @DisplayName("Check if output preserve input order")
    void checkOrder() {
        List<Room> rooms = List.of(room("r2", 6), room("r3", 4), room("r1", 4));

        List<Room> result = AvailabilityService.findAvailableRooms(rooms, List.of(), sToTime("2026-10-18T10:00"),
                sToTime("2026-10-18T11:00"), 4);

        assertEquals(3, result.size());
        assertEquals("r2", result.get(0).getId());
        assertEquals("r3", result.get(1).getId());
        assertEquals("r1", result.get(2).getId());
    }
}
