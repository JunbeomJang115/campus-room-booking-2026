package itm.oss.booking;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Set;
import java.util.List;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class AvailabilityServiceTest {
        @Test 
        void returnsRoomWhenNoBookings(){
            Room room = new Room("R101","Room A", 40, Set.of("JJB"));
            List<Room> rooms = List.of(room);
            List<Booking> bookings = List.of();
            LocalDateTime start = LocalDateTime.parse("2026-10-20T10:00:00");
            LocalDateTime end = LocalDateTime.parse("2026-10-20T12:00:00");
            int minCapacity = 30;
            List<Room> result = AvailabilityService.findAvailableRooms(rooms, bookings, start, end, minCapacity);
            assertEquals(1,result.size(),"Expected exactly one available room");
            assertEquals(room,result.get(0),"Expected the returned room to match the requested available room");
        }
        @Test 
        void excludesRoomWithInsufficientCapacity(){
            Room room = new Room("R102","Room B",20,Set.of());
            List<Room> rooms = List.of(room);
            List<Booking> bookings = List.of();
            LocalDateTime start = LocalDateTime.parse("2026-10-20T10:00:00");
            LocalDateTime end = LocalDateTime.parse("2026-10-20T12:00:00");
            int minCapacity = 30;
            List<Room> result = AvailabilityService.findAvailableRooms(rooms, bookings, start, end, minCapacity);
            assertEquals(0, result.size(),"Rooms with insufficient capacity must be excluded");
        }
        @Test 
        void throwsExceptionForInvalidCapacity(){
            Room room = new Room("R103","Room C",40,Set.of());
            List<Room> rooms = List.of(room);
            List<Booking> bookings = List.of();
            LocalDateTime start = LocalDateTime.parse("2026-10-20T10:00:00");
            LocalDateTime end = LocalDateTime.parse("2026-10-20T12:00:00");
            int minCapacity = 0;
            assertThrows(IllegalArgumentException.class,()-> AvailabilityService.findAvailableRooms(rooms, bookings, start, end, minCapacity),"Invalid minimum capacity must throw IllegalArgumentException");
        }
        @Test 
        void filterRoomsByCapacityAndTime(){
            Room roomA = new Room("R101","Room A",40,Set.of());
            Room roomB = new Room("R102","Room B", 20, Set.of());
            Room roomC = new Room("R103","Room C", 50, Set.of());
            List<Room> rooms = List.of(roomA, roomB, roomC);
            Booking bookingA = new Booking(1, "s1", "R101", LocalDateTime.parse("2026-10-20T10:00"),LocalDateTime.parse("2026-10-20T11:00"),2,"Study",BookingStatus.ACTIVE);
            Booking bookingC = new Booking(3, "s3", "R103", LocalDateTime.parse("2026-10-20T13:00"),LocalDateTime.parse("2026-10-20T15:00"), 2, "Study", BookingStatus.ACTIVE);
            List<Booking> bookings = List.of(bookingA,bookingC);
            LocalDateTime start = LocalDateTime.parse("2026-10-20T10:00");
            LocalDateTime end = LocalDateTime.parse("2026-10-20T12:00");
            int minCapacity = 30;
            List<Room> result = AvailabilityService.findAvailableRooms(rooms, bookings, start, end, minCapacity);
            assertEquals(1,result.size(),"Only one room should satisfy both capacity and time conditions");
            assertEquals(roomC, result.get(0), "Expected the available room to be R103");
        }
}
