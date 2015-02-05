package models;

import play.db.ebean.Model;

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

    public String message;

    /*
        0 - No error/unknown
        1 - Timeout on device
     */
    public int code = 0;

    public Date time;

    public Date lastUpdate;

    public boolean accepted;

    @OneToOne
    public Device device;

    @OneToOne
    public Sensor sensor;

    public static Finder<Long, LogItem> find = new Finder<Long, LogItem>(
            Long.class, LogItem.class
    );

    public static LogItem findLastActiveError() {
        List<LogItem> list = find.where().eq("accepted", true).order("lastUpdate DESC").findList();
        if(list.size() <= 0) return null;
        return list.get(0);
    }
}
