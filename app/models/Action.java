package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Luuk on 25/01/15.
 */

@Entity
public class Action extends Model {

    /**
     * For lack of a better place, sleepMode is controlled here. (Bascially, is inactive mode, everything off)
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

    @Version
    public Date lastAction;

    @ManyToOne
    public Sensor sensor;

    @ManyToOne
    public Device device;

    // If the temperature goes up as a result of the action (e.g. heater/CV)
    public boolean actionUp;

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

    public static List<ObjectNode> checkForDeviceActions(Device device) {
        List<ObjectNode> result = new ArrayList<>();

        for (Action action : device.actions) {
                // For temperature controllers
            if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                ObjectNode actionObject = Json.newObject();
                if (action.actionUp) {
                    if (sleepMode) {
                        // Turn off
                        actionObject.put("action", "setLow");
                        actionObject.put("pin", action.pin);
                        result.add(actionObject);
                    } else if (action.tempLow > action.sensor.value) {
                        actionObject.put("action", "setHigh");
                        actionObject.put("pin", action.pin);
                        result.add(actionObject);
                    } else if (action.tempHigh < action.sensor.value) {
                        actionObject.put("action", "setLow");
                        actionObject.put("pin", action.pin);
                        result.add(actionObject);
                    }
                } else {
                    if (sleepMode) {
                        // Turn off
                        actionObject.put("action", "setLow");
                        actionObject.put("pin", action.pin);
                        result.add(actionObject);
                    } else if (action.tempLow > action.sensor.value) {
                        actionObject.put("action", "setLow");
                        actionObject.put("pin", action.pin);
                        result.add(actionObject);
                    } else if (action.tempHigh < action.sensor.value) {
                        actionObject.put("action", "setHigh");
                        actionObject.put("pin", action.pin);
                        result.add(actionObject);
                    }
                }
            }
        }

        return result;
    }

    public static List<ObjectNode> checkForSensorActions(Sensor sensor) {
        List<ObjectNode> result = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -500);
        Date touchDelay = calendar.getTime();

        for (Action action : sensor.actions) {
            if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.DISPLAY))) {
                if(action.lastAction.before(touchDelay)) {
                    ObjectNode actionObject = Json.newObject();
                    actionObject.put("action", "switchDisplay");
                    result.add(actionObject);
                }
            }

            if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.SLEEP))) {
                if(action.lastAction.before(touchDelay)) {
                    if(sleepMode){
                        sleepMode = false;
                        ObjectNode actionObject = Json.newObject();
                        actionObject.put("action", "turnOnDisplay");
                        result.add(actionObject);
                    } else {
                        // Turn everything related to temperature off
                        for(Action anAction : action.sensor.actions) {
                            if(anAction.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                                ObjectNode actionObject = Json.newObject();
                                actionObject.put("pin", anAction.pin);
                                actionObject.put("action", "setLow");
                                result.add(actionObject);
                            }
                        }
                    }
                }
            }

            if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMP_UP))) {
                if (action.lastAction.before(touchDelay)) {
                    for(Action anAction : action.sensor.actions) {
                        if (anAction.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                            anAction.tempHigh += 1;
                            anAction.tempLow += 1;
                            anAction.save();
                        }
                    }
                }
            }

            if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMP_DOWN))) {
                if (action.lastAction.before(touchDelay)) {
                    for(Action anAction : action.sensor.actions) {
                        if (anAction.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                            anAction.tempHigh -= 1;
                            anAction.tempLow -= 1;
                            anAction.save();
                        }
                    }
                }
            }
        }

         return result;
     }

}
