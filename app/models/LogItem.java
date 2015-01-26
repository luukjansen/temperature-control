package models;

import play.db.ebean.Model;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;

/**
 * Created by Luuk on 26/01/15.
 */
public class LogItem extends Model {

    @Id
    public Long id;

    public String message;

    public Date time;

    public Date lastUpdate;

    @OneToMany
    public Device device;

    @OneToMany
    public Sensor sensor;

    public static Finder<Long, LogItem> find = new Finder<Long, LogItem>(
            Long.class, LogItem.class
    );
}
