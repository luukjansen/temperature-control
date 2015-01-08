package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.ebean.Model;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Luuk on 07/01/15.
 */

@Entity
public class Device extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String ipAddress;

    @Constraints.Required
    @ElementCollection(fetch = FetchType.EAGER)
    public Set<DeviceRole> roles = new HashSet<DeviceRole>(Arrays.asList(DeviceRole.OTHER));

    @Version
    public Timestamp lastUpdate;

    public static Finder<Long,Device> find = new Finder<Long,Device>(
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
