package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class SensorRole extends Model {

    public static enum RoleName {
        DISPLAY,
        TEMPERATURE,
        HUMIDITY,
        OTHER,
        UNKNOWN
    }


    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @ManyToMany(mappedBy = "roles")
    public List<Sensor> sensors;


    public static Finder<Long, SensorRole> find = new Finder<Long, SensorRole>(
            Long.class, SensorRole.class
    );

    public static SensorRole findByRoleName(RoleName roleName) {
        return find.where().eq("name", roleName.toString()).findUnique();
    }

    public static Map<String, String> selectOptions() {
        Map<String, String> options = new LinkedHashMap<>();

        for (SensorRole sensorRole : find.all()) {
            options.put(sensorRole.id.toString(), sensorRole.name);
        }

        return options;
    }
}
