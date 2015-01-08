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
public class DeviceRole extends Model {

    public static enum RoleName {
        SERVER,
        TEMPERATURE,
        HUMIDITY,
        SWITCH,
        OTHER
    }


    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @ManyToMany(mappedBy = "roles")
    public List<Device> devices;


    public static Finder<Long, DeviceRole> find = new Finder<Long, DeviceRole>(
            Long.class, DeviceRole.class
    );

    public static DeviceRole findByRoleName(RoleName roleName) {
        return find.where().eq("name", roleName.toString()).findUnique();
    }

    public static Map<String, String> selectOptions() {
        Map<String, String> options = new LinkedHashMap<>();

        for (DeviceRole deviceRole : find.all()) {
            options.put(deviceRole.id.toString(), deviceRole.name);
        }

        return options;
    }
}
