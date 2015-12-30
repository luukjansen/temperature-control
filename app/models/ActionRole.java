package models;

import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Luuk on 30/01/15.
 */
@Entity
public class ActionRole extends Model {

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
        TEMPERATURE,
        CV,
        DISPLAY,
        TEMP_UP,
        TEMP_DOWN,
        SLEEP,
        UNKNOWN
    }


    @Id
    public Long id;

    @Constraints.Required
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<Sensor> sensors;


    public static Model.Finder<Long, ActionRole> find = new Model.Finder<Long, ActionRole>(
            Long.class, ActionRole.class
    );

    public static ActionRole findByRoleName(RoleName roleName) {
        return find.where().eq("name", roleName.toString()).findUnique();
    }

    public static Map<String, String> selectOptions() {
        Map<String, String> options = new LinkedHashMap<>();

        for (ActionRole actionRole : find.all()) {
            options.put(actionRole.id.toString(), actionRole.getName());
        }

        return options;
    }
}
