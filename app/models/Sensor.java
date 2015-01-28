package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Luuk on 25/01/15.
 */
@Entity
public class Sensor extends Model {

    @Id
    public Long id;

    public String name;

    @Constraints.Required
    public String sensorId;

    @Version
    public Date lastUpdate;

    public float temp;

    @OneToOne
    public LogItem latestError;

    @ManyToOne
    @Constraints.Required
    public Device device;

    @ManyToMany
    public List<SensorRole> roles = new ArrayList<>();

    @Transient
    public List<Long> rolesIds = new ArrayList<>();

    public static Finder<Long, Sensor> find = new Finder<Long, Sensor>(
            Long.class, Sensor.class
    );

    public static Sensor getPrimarySensor() {
        return Sensor.find.where().in("roles.name", "PRIMARY").findUnique();
    }

    public static List<Sensor> getSecondarySensors(){
        return Sensor.find.where().in("roles.name", "SECONDARY").findList();
    }
}
