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
    private List<ActionRole> roles = new ArrayList<>();

    @Transient
    private List<Long> rolesIds = new ArrayList<>();

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

    public List<ActionRole> getRoles() {
        return roles;
    }

    public void setRoles(List<ActionRole> roles) {
        this.roles = roles;
    }

    public List<Long> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(List<Long> rolesIds) {
        this.rolesIds = rolesIds;
    }

    public String getCommand(boolean action){
        if(isActionIsHigh()){
            if(action)return "setHigh";
        } else {
            if(!action) return "setHigh";
        }

        return "setLow";
    }

    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Action)) {
            return false;
        }
        return ((Action) o).getId().equals(getId());
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
        // First compile a list with actions, then write them to the nodeList.
        HashMap<Action, Boolean> actionMap = new HashMap<>();
        for (Action action : device.getActions()) {
            // For temperature controllers. Fix ignores the procedure, and leave the sensor as is.
            if (!action.isFix() && action.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                Boolean performAction = null;
                if (action.isActionUp()) {
                    if (isSleepMode()) {
                        // Turn off
                        performAction = false;
                    } else if (action.getTempLow() > action.getSensor().getValue()) {
                        performAction = true;
                    } else if (action.getTempHigh() < action.getSensor().getValue()) {
                        performAction = false;
                    }
                } else {
                    if (isSleepMode()) {
                        // Turn off
                        performAction = false;
                    } else if (action.getTempLow() > action.getSensor().getValue()) {
                        performAction = false;
                    } else if (action.getTempHigh() < action.getSensor().getValue()) {
                        performAction = true;
                    }
                }

                if(performAction != null){
                    // Check if pin is already present, and update or add.
                    for(Map.Entry<Action, Boolean> pair : actionMap.entrySet()){
                        if(pair.getKey().getPin() == action.getPin()){
                            if(pair.getValue() && !performAction){
                                performAction = true;
                            }
                        }
                    }

                    if(actionMap.containsKey(action)){
                        actionMap.replace(action, performAction);
                    } else {
                        actionMap.put(action, performAction);
                    }
                }
            }
        }

        List<ObjectNode> result = new ArrayList<>();
        //Iterator it = actionMap.entrySet().iterator();
        for(Map.Entry<Action, Boolean> pair : actionMap.entrySet()){
            ObjectNode actionObject = Json.newObject();
            actionObject.put("action", pair.getKey().getCommand(pair.getValue()));
            actionObject.put("pin", pair.getKey().getPin());
            result.add(actionObject);
            //it.remove(); // avoids a ConcurrentModificationException
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
                if (action.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.DISPLAY))) {
                    ObjectNode actionObject = Json.newObject();
                    actionObject.put("action", "switchDisplay");
                    result.add(actionObject);
                }

                if (action.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.SLEEP))) {
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
                            if (anAction.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                                ObjectNode actionObject = Json.newObject();
                                actionObject.put("pin", anAction.getPin());
                                actionObject.put("action", "setLow");
                                result.add(actionObject);
                            }
                        }
                    }
                }

                if (action.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMP_UP))) {

                    for (Action anAction : Action.find.where().in("roles.name", "CV").findList()) {
                        if (anAction.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                            anAction.setTempHigh(anAction.getTempHigh() + 1);
                            anAction.setTempLow(anAction.getTempLow() + 1);
                            anAction.save();
                        }
                    }
                }

                if (action.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMP_DOWN))) {
                    for (Action anAction : Action.find.where().in("roles.name", "CV").findList()) {
                        if (anAction.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
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
