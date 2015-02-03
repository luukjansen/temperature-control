package controllers;

import models.Action;
import models.ActionRole;
import models.Device;
import models.Sensor;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.actionsViews.editView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Luuk on 01/02/15.
 */
public class Actions extends Controller {

    public static Result add(Long sensorId) {
        Form<Action> myForm = Form.form(Action.class);

        Sensor sensor = Sensor.find.byId(sensorId);
        Action action = new Action();
        action.actionUp = true;
        action.sensor = sensor;
        myForm.fill(action);

        return ok(editView.render(myForm, action, allDevices()));
    }

    public static Result delete(Long id) {
        Action action = Action.find.byId(id);
        Sensor sensor = action.sensor;
        action.delete();
        return redirect(routes.Sensors.edit(sensor.id));
    }

    public static Result edit(Long id) {
        Action action = Action.find.byId(id);
        Form<Action> myForm = Form.form(Action.class);

        // Rewrite IDs of roles to transient field, to all mark checkboxes as checked
        for (ActionRole role : action.roles) {
            action.rolesIds.add(role.id);
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
        action.roles = new ArrayList<ActionRole>();
        for (Long roleId : action.rolesIds) {
            action.roles.add(ActionRole.find.ref(roleId));
        }

        // Save or update?
        if (action.id == null) {
            action.save();
        } else {
            action.update(action.id);
        }

//        return ok(editView.render(ActionForm));
        return redirect(routes.Sensors.edit(action.sensor.id));
    }

    private static HashMap<String, String> allDevices(){
        HashMap<String, String> devices = new HashMap<>();
                for(Device device : Device.find.all()){
                    devices.put(device.id.toString(), device.name);
                }
        return devices;
    }
}
