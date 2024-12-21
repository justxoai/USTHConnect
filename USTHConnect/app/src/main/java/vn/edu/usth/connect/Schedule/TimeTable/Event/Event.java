package vn.edu.usth.connect.Schedule.TimeTable.Event;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Event {

    public static ArrayList<Event> eventsList = new ArrayList<>();

    // Use for Week Event
    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        Log.d("Event", "Filtering events for date: " + date);
        for(Event event : eventsList)
        {
            // Check date in Event == date
            if(event.getEventStartDateTime().toLocalDate().equals(date))
                // Add Event to Array
                events.add(event);
        }

        Log.d("Event", "Found events: " + events.size());
        return events;
    }

    // Use for Daily Event
    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            int eventHour = event.getEventStartDateTime().toLocalTime().getHour();
            int cellHour = time.getHour();
            // Check date in Event == date and check hour
            if(event.getEventStartDateTime().toLocalDate().equals(date) && eventHour == cellHour)
                // Add Event to Array
                events.add(event);
        }

        return events;
    }

    private Integer eventId;
    private String eventName;
    private String eventDescription;
    private String eventStart;
    private String eventEnd;
    private String locationValue;
    private Integer organizerId;

    // Constructor
    public Event(Integer eventId, String eventName, String eventDescription,
                 String eventStart, String eventEnd,
                 String location, Integer organizerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.locationValue = location;
        this.organizerId = organizerId;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
    }

    public String getLocation() {
        return locationValue;
    }

    public void setLocation(String location) {
        this.locationValue = location;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    // Converts the eventStart string to LocalDateTime
    public LocalDateTime getEventStartDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.parse(this.eventStart, formatter);
    }

    // Converts the eventEnd string to LocalDateTime
    public LocalDateTime getEventEndDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.parse(this.eventEnd, formatter);
    }
    // EventItem
//    private String name;
//    private LocalDate date;
//    private LocalTime time;
//
//    public Event(String name, LocalDate date, LocalTime time)
//    {
//        this.name = name;
//        this.date = date;
//        this.time = time;
//    }
//
//    public String getName()
//    {
//        return name;
//    }
//
//    public void setName(String name)
//    {
//        this.name = name;
//    }
//
//    public LocalDate getDate()
//    {
//        return date;
//    }
//
//    public void setDate(LocalDate date)
//    {
//        this.date = date;
//    }
//
//    public LocalTime getTime()
//    {
//        return time;
//    }
//
//    public void setTime(LocalTime time)
//    {
//        this.time = time;
//    }

}
