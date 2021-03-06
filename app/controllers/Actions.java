package controllers;

import models.Action;
import models.ActionRole;
import models.Device;
import models.Sensor;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.actionsViews.editView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Luuk on 01/02/15.
 */
public class Actions extends Controller {

    public static Result add(Long sensorId) {
        Form<Action> myForm = Form.form(Action.class);

        Sensor sensor = Sensor.find.byId(sensorId);
        Action action = new Action();
        action.setActionUp(true);
        action.setActionIsHigh(true);
        action.setSensor(sensor);
        myForm.fill(action);

        return ok(editView.render(myForm, action, allDevices()));
    }

    public static Result delete(Long id) {
        Action action = Action.find.byId(id);
        Sensor sensor = action.getSensor();
        action.delete();
        return redirect(routes.Sensors.edit(sensor.id));
    }

    public static Result edit(Long id) {
        Action action = Action.find.byId(id);
        Form<Action> myForm = Form.form(Action.class);

        // Rewrite IDs of roles to transient field, to all mark checkboxes as checked
        for (ActionRole role : action.getRoles()) {
            action.getRolesIds().add(role.id);
        }

        myForm = myForm.fill(action);

        return ok(editView.render(myForm, action, allDevices()));
    }

    public static Result update() {
        Form<Action> actionForm = Form.form(Action.class).bindFromRequest();

        if (actionForm.hasErrors()) {
            Action action = Action.find.byId(Long.valueOf(actionForm.data().get("id")));
            return badRequest(editView.render(actionForm, action, allDevices()));
        }

        // Form is OK, has no errors we can proceed
        Action action = actionForm.get();

        // Get checked checkboxes and add ActionRoles by ID
        action.setRoles(new ArrayList<ActionRole>());
        for (Long roleId : action.getRolesIds()) {
            Logger.info("Adding role id " + roleId);
            action.getRoles().add(ActionRole.find.ref(roleId));
        }

        // Get around bug
        if(action.isActionUp()){
            action.setActionUp(true);
        } else {
            action.setActionUp(false);
        }

        if(action.isActionIsHigh()){
            action.setActionIsHigh(true);
        } else {
            action.setActionIsHigh(false);
        }

        // END bug hack

        // Save or update?
        if (action.getId() == null) {
            action.save();
        } else {
            action.update();
        }

//        return ok(editView.render(ActionForm));
        return redirect(routes.Sensors.edit(action.getSensor().id));
    }

    private static HashMap<String, String> allDevices(){
        HashMap<String, String> devices = new HashMap<>();
                for(Device device : Device.find.all()){
                    devices.put(device.id.toString(), device.getName());
                }
        return devices;
    }
}
