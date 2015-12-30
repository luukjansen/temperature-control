package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import com.avaje.ebean.Model;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Luuk on 07/01/15.
 */

@Entity
public class Device extends Model {

    @Id
    public Long id;

    @Constraints.Required
    private String name;

    @Constraints.Required
    private String ipAddress;

    @Constraints.Required
    private String uniqueId;

    @OneToMany(mappedBy="device")
    private List<Sensor> sensors = new ArrayList<>();

    @OneToMany(mappedBy="device")
    private List<Action> actions = new ArrayList<>();

    @OneToOne
    private LogItem latestError;

    @Version
    private Timestamp lastUpdate;

    private boolean debugMode;

    private boolean statusLed;

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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public LogItem getLatestError() {
        return latestError;
    }

    public void setLatestError(LogItem latestError) {
        this.latestError = latestError;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isStatusLed() {
        return statusLed;
    }

    public void setStatusLed(boolean statusLed) {
        this.statusLed = statusLed;
    }

    public static Finder<Long, Device> find = new Finder<Long, Device>(
            Long.class, Device.class
    );

    public List<ValidationError> validate() {
        /*
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (User.byEmail(email) != null) {
            errors.add(new ValidationError("email", "This e-mail is already registered."));
        }
        return errors.isEmpty() ? null : errors;
        */
        return null;
    }



}
