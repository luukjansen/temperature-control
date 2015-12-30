package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Luuk on 26/01/15.
 */
@Entity
public class LogItem extends Model {

    @Id
    public Long id;

    private String message;

    /*
        0 - No error/unknown
        1 - Timeout on device
     */
    private int code = 0;

    private Date time;

    private Date lastUpdate;

    private boolean accepted;

    @OneToOne
    private Device device;

    @OneToOne
    private Sensor sensor;

    public static Finder<Long, LogItem> find = new Finder<Long, LogItem>(
            Long.class, LogItem.class
    );

    public static LogItem findLastActiveError() {
        List<LogItem> list = find.where().eq("accepted", true).order("lastUpdate DESC").findList();
        if(list.size() <= 0) return null;
        return list.get(0);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
