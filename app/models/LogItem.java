package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Luuk on 26/01/15.
 */
@Entity
public class LogItem extends Model {

    @Id
    public Long id;

    public String message;

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
}
