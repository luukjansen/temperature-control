package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Luuk on 25/01/15.
 */

@Entity
public class Action extends Model {

    private static boolean sleepMode = false;

    @Id
    public Long id;

    private String name = "Unknown";

    @Constraints.Required
    private float tempLow = 0;

    @Constraints.Required
    private float tempHigh = 0;

    private int pin = -1;

    @Version
    private Date lastAction;

    @ManyToOne
    private Sensor sensor;

    @ManyToOne
    private Device device;

    // If the temperature goes up as a result of the action (e.g. heater/CV)
    private boolean actionUp;

    private boolean actionIsHigh;

    // Locks the current value to manual (not automatic changes)
    private boolean fix = false;

    @ManyToMany
    public List<ActionRole> roles = new ArrayList<>();

    @Transient
    public List<Long> rolesIds = new ArrayList<>();

    /**
     * For lack of a better place, sleepMode is controlled here. (Bascially, is inactive mode, everything off)
     */
    public static boolean isSleepMode() {
        return sleepMode;
    }

    public static void setSleepMode(boolean sleepMode) {
        Action.sleepMode = sleepMode;
    }

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

    public float getTempLow() {
        return tempLow;
    }

    public void setTempLow(float tempLow) {
        this.tempLow = tempLow;
    }

    public float getTempHigh() {
        return tempHigh;
    }

    public void setTempHigh(float tempHigh) {
        this.tempHigh = tempHigh;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean isActionUp() {
        return actionUp;
    }

    public void setActionUp(boolean actionUp) {
        this.actionUp = actionUp;
    }

    public boolean isActionIsHigh() {
        return actionIsHigh;
    }

    public void setActionIsHigh(boolean actionIsHigh) {
        this.actionIsHigh = actionIsHigh;
    }

    public boolean isFix() {
        return fix;
    }

    public void setFix(boolean fix) {
        this.fix = fix;
    }

    public String getCommand(boolean action){
        if(isActionIsHigh()){
            if(action)return "setHigh";
        } else {
            if(!action) return "setHigh";
        }

        return "setLow";
    }

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

        for (Action action : device.getActions()) {
            // For temperature controllers. Fix ignores the procedure, and leave the sensor as is.
            if (!action.isFix() && action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                ObjectNode actionObject = Json.newObject();
                if (action.isActionUp()) {
                    if (isSleepMode()) {
                        // Turn off
                        actionObject.put("action", action.getCommand(false));
                    } else if (action.getTempLow() > action.getSensor().getValue()) {
                        actionObject.put("action", action.getCommand(true));
                    } else if (action.getTempHigh() < action.getSensor().getValue()) {
                        actionObject.put("action", action.getCommand(false));
                    }
                } else {
                    if (isSleepMode()) {
                        // Turn off
                        actionObject.put("action", action.getCommand(false));
                    } else if (action.getTempLow() > action.getSensor().getValue()) {
                        actionObject.put("action", action.getCommand(false));
                    } else if (action.getTempHigh() < action.getSensor().getValue()) {
                        actionObject.put("action", action.getCommand(true));
                    }
                }
                actionObject.put("pin", action.getPin());
                result.add(actionObject);
            }
        }
        return result;
    }

    public static List<ObjectNode> checkForSensorActions(Sensor sensor) {
        List<ObjectNode> result = new ArrayList<>();

        //Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MILLISECOND, -500);
        //Date touchDelay = calendar.getTime();

        for (Action action : sensor.getActions()) {
            if (action.getSensor().getValue() > 0) {
                if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.DISPLAY))) {
                    ObjectNode actionObject = Json.newObject();
                    actionObject.put("action", "switchDisplay");
                    result.add(actionObject);
                }

                if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.SLEEP))) {
                    if (isSleepMode()) {
                        setSleepMode(false);
                        ObjectNode actionObject = Json.newObject();
                        actionObject.put("action", "turnOnDisplay");
                        result.add(actionObject);
                    } else {
                        setSleepMode(true);
                        ObjectNode actionDisplayObject = Json.newObject();
                        actionDisplayObject.put("action", "turnOffDisplay");
                        result.add(actionDisplayObject);

                        // Turn everything related to temperature off
                        for (Action anAction : action.getSensor().getActions()) {
                            if (anAction.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                                ObjectNode actionObject = Json.newObject();
                                actionObject.put("pin", anAction.getPin());
                                actionObject.put("action", "setLow");
                                result.add(actionObject);
                            }
                        }
                    }
                }

                if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMP_UP))) {

                    for (Action anAction : Action.find.where().in("roles.name", "CV").findList()) {
                        if (anAction.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                            anAction.setTempHigh(anAction.getTempHigh() + 1);
                            anAction.setTempLow(anAction.getTempLow() + 1);
                            anAction.save();
                        }
                    }
                }

                if (action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMP_DOWN))) {
                    for (Action anAction : Action.find.where().in("roles.name", "CV").findList()) {
                        if (anAction.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                            anAction.setTempHigh(anAction.getTempHigh() - 1);
                            anAction.setTempLow(anAction.getTempLow() - 1);
                            anAction.save();
                        }
                    }

                }
                //action.lastAction = new Date();
                //action.save();
            }
        }

        return result;
     }


}
