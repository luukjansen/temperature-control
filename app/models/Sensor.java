package models;

import play.data.validation.Constraints;
import com.avaje.ebean.Model;

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

    private String name;

    @Constraints.Required
    private String sensorId;

    @Version
    private Date lastUpdate;

    private float value;

    @OneToOne
    private LogItem latestError;

    @ManyToOne
    @Constraints.Required
    private Device device;

    @OneToMany
    private List<Action> actions = new ArrayList<>();

    @ManyToMany
    private List<SensorRole> roles = new ArrayList<>();

    @Transient
    private List<Long> rolesIds = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public LogItem getLatestError() {
        return latestError;
    }

    public void setLatestError(LogItem latestError) {
        this.latestError = latestError;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<SensorRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SensorRole> roles) {
        this.roles = roles;
    }

    public List<Long> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(List<Long> rolesIds) {
        this.rolesIds = rolesIds;
    }

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
