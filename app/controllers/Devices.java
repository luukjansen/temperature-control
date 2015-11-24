package controllers;

import models.Device;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import views.html.devicesViews.editView;
import views.html.devicesViews.indexView;

/**
 * Created by Luuk on 07/01/15.
 */
public class Devices extends Controller {

    public static Result index() {
        List<Device> devices = Device.find.all();
        return ok(indexView.render(devices));
    }

    public static Result add() {
        Form<Device> deviceForm = Form.form(Device.class);
        return ok(editView.render(deviceForm, null));
    }

    public static Result delete(Long id) {
        Device device = Device.find.byId(id);
        device.delete();
        return redirect(routes.Devices.index());
    }

    public static Result edit(Long id) {
        Device device = Device.find.byId(id);
        Form<Device> deviceForm = Form.form(Device.class);

        deviceForm = deviceForm.fill(device);

        return ok(editView.render(deviceForm, device));
    }

    public static Result update() {
        Form<Device> deviceForm = Form.form(Device.class).bindFromRequest();
        Device device = deviceForm.get();

        if (deviceForm.hasErrors()) {
            return badRequest(editView.render(deviceForm, device));
        }
        // Form is OK, has no errors we can proceed

        // Save or update?
        if (device.id == null) {
            device.save();
        } else {
            device.update();
        }


//        return ok(editView.render(deviceForm));
        return redirect(routes.Devices.index());
    }

}
