package controllers;

import models.Sensor;
import models.SensorRole;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.sensorsViews.editView;
import views.html.sensorsViews.indexView;
import views.html.sensorsViews.rolesIndexView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luuk on 26/01/15.
 */
public class Sensors extends Controller {
    
    public static Result index() {
        List<Sensor> sensors = Sensor.find.all();
        return ok(indexView.render(sensors));
    }

    public static Result rolesIndex() {
        List<SensorRole> roles = SensorRole.find.all();
        return ok(rolesIndexView.render(roles));
    }


    public static Result add() {
        Form<Sensor> myForm = Form.form(Sensor.class);
        return ok(editView.render(myForm, null));
    }

    public static Result delete(Long id) {
        Sensor sensor = Sensor.find.byId(id);
        sensor.delete();
        return redirect(routes.Sensors.index());
    }

    public static Result edit(Long id) {
        Sensor sensor = Sensor.find.byId(id);
        Form<Sensor> myForm = Form.form(Sensor.class);

        // Rewrite IDs of roles to transient field, to all mark checkboxes as checked
        for (SensorRole role : sensor.roles) {
            sensor.rolesIds.add(role.id);
        }

        myForm = myForm.fill(sensor);

        return ok(editView.render(myForm, sensor));
    }

    public static Result update() {
        Form<Sensor> sensorForm = Form.form(Sensor.class).bindFromRequest();

        if (sensorForm.hasErrors()) {
            Sensor sensor = Sensor.find.byId(Long.valueOf(sensorForm.data().get("id")));
            return badRequest(editView.render(sensorForm, sensor));
        }

        // Form is OK, has no errors we can proceed
        Sensor sensor = sensorForm.get();

        // Get checked checkboxes and add sensorRoles by ID
        sensor.roles = new ArrayList<SensorRole>();
        for (Long roleId : sensor.rolesIds) {
            sensor.roles.add(SensorRole.find.ref(roleId));
        }

        // Save or update?
        if (sensor.id == null) {
            sensor.save();
        } else {
            sensor.update(sensor.id);
        }


//        return ok(editView.render(sensorForm));
        return redirect(routes.Sensors.index());
    }
    
    
}
