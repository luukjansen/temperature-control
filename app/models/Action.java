package models;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luuk on 25/01/15.
 */

@Entity
public class Action extends Model {

    /**
     *  For lack of a better place, sleepMode is controlled here. (Bascially, is inactive mode, everything off)
     */
    public static boolean sleepMode = false;

    @Id
    public Long id;

    public String name = "Unknown";

    @Constraints.Required
    public float tempLow = 0;

    @Constraints.Required
    public float tempHigh = 0;

    public int pin = -1;

    @ManyToOne
    public Sensor sensor;

    // If the temperature goes up as a result of the action (e.g. heater/CV)
    public boolean actionUp = true;

    // Locks the current value to manual (not automatic changes)
    public boolean fix = false;

    @ManyToMany
    public List<ActionRole> roles = new ArrayList<>();

    @Transient
    public List<Long> rolesIds = new ArrayList<>();

    public static Finder<Long, Action> find = new Finder<Long, Action>(
            Long.class, Action.class
    );

    /**
     * This procedure checks if any action is needed under the current circumstances
     *
     * @return Any action string
     */

    public String checkForAction(){
        if(sleepMode);

        if(roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))){
            if(actionUp) {
                if (tempLow > sensor.value) {
                    return "setHigh";
                } else if (tempHigh < sensor.value) {
                    return "setLow";
                }
            } else {
                if (tempLow > sensor.value) {
                    return "setLow";
                } else if (tempHigh < sensor.value) {
                    return "setHigh";
                }
            }

            // DO nothing for now, should improve.
            return null;
        }

        // NO action?
        return null;
    }

}
