package models;

import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class SensorRole extends Model {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public static enum RoleName {
        PRIMARY,
        SECONDARY,
        TEMPERATURE,
        HUMIDITY,
        LIGHT,
        PORT,
        BUTTON,
        UNKNOWN
    }


    @Id
    public Long id;

    @Constraints.Required
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<Sensor> sensors;


    public static Finder<Long, SensorRole> find = new Finder<Long, SensorRole>(
            Long.class, SensorRole.class
    );

    public static SensorRole findByRoleName(RoleName roleName) {
        return find.where().eq("name", roleName.toString()).findUnique();
    }

    public static Map<String, String> selectOptions() {
        Map<String, String> options = new LinkedHashMap<>();

        for (SensorRole sensorRole : find.all()) {
            options.put(sensorRole.id.toString(), sensorRole.getName());
        }

        return options;
    }
}
