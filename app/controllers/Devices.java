package controllers;

import models.Device;
import models.DeviceRole;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import views.html.devicesViews.editView;
import views.html.devicesViews.indexView;
import views.html.devicesViews.rolesIndexView;

/**
 * Created by Luuk on 07/01/15.
 */
public class Devices extends Controller {

    public static Result index() {
        List<Device> devices = Device.find.all();
        return ok(indexView.render(devices));
    }

    public static Result rolesIndex() {
        List<DeviceRole> roles = DeviceRole.find.all();
        return ok(rolesIndexView.render(roles));
    }


    public static Result add() {
        Form<Device> myForm = Form.form(Device.class);
        return ok(editView.render(myForm));
    }

    public static Result delete(Long id) {
        Device device = Device.find.byId(id);
        device.delete();
        return redirect(routes.Devices.index());
    }

    public static Result edit(Long id) {
        Device device = Device.find.byId(id);
        Form<Device> myForm = Form.form(Device.class);

        // Rewrite IDs of roles to transient field, to all mark checkboxes as checked
        for (DeviceRole role : device.roles) {
            device.rolesIds.add(role.id);
        }

        myForm = myForm.fill(device);

        return ok(editView.render(myForm));
    }

    /**
     * @return Result
     * @deprecated Use update() for saving and updating...
     */
    @Deprecated
    public static Result save() {
        Device device = Form.form(Device.class).bindFromRequest().get();
        device.save();
        return index();
    }

    public static Result update() {

        Form<Device> deviceForm = Form.form(Device.class).bindFromRequest();
        if (deviceForm.hasErrors()) {
            return badRequest(editView.render(deviceForm));
        }

        // Form is OK, has no errors we can proceed

        Device device = deviceForm.get();

        // Get checked checkboxes and add DeviceRoles by ID
        device.roles = new ArrayList<DeviceRole>();
        for (Long roleId : device.rolesIds) {
            device.roles.add(DeviceRole.find.ref(roleId));
        }

        // Save or update?
        if (device.id == null) {
            device.save();
        } else {
            device.update(device.id);
        }


//        return ok(editView.render(deviceForm));
        return redirect(routes.Devices.index());
    }

}
