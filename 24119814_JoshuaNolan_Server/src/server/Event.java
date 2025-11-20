
import java.util.Objects;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joshu
 */
public class Event {
    private String date;
    private String time;
    private String description;

    //Constructor
    public Event(String date, String time, String description) {
        this.date = date;
        this.time = time;
        this.description = description;
    }

    //Getters
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }
    
    //toString for display
    @Override
    public String toString() {
        return time + ", " + description;
    }
    
    //hashCode for storing events in collectionss
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.date);
        hash = 29 * hash + Objects.hashCode(this.time);
        hash = 29 * hash + Objects.hashCode(this.description);
        return hash;
    }

    //equals method to compare events
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.time, other.time)) {
            return false;
        }
        return Objects.equals(this.description, other.description);
    }
    
    
}
