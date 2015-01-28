package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;


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
    public String name;

    @Constraints.Required
    public String ipAddress;

    @Constraints.Required
    public String uniqueId;

    @ManyToOne
    public List<Sensor> sensors = new ArrayList<>();

    @ManyToMany
    public List<Action> actions = new ArrayList<>();

    @OneToOne
    public LogItem latestError;

    @Version
    public Timestamp lastUpdate;

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
