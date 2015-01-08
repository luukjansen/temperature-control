package controllers;

import models.Device;
import models.DeviceRole;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import scala.collection.Seq;
import scala.collection.convert.Wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Luuk on 07/01/15.
 */
public class Devices extends Controller {

    public static Result index() {
        List<Device> devices = Device.find.all();
        return ok(views.html.Devices.index.render(devices));
    }

    public static Result add () {
        Form<Device> myForm = Form.form(Device.class);
        return ok(views.html.Devices.add.render(myForm));
    }

    public static Result delete(Long id) {
        Device device = Device.find.byId(id);
        device.delete();
        return redirect(routes.Devices.index());
    }

    public static Result edit(Long id){
        Device device = Device.find.byId(id);
        Form<Device> myForm = Form.form(Device.class);
        myForm = myForm.fill(device);

        return ok(views.html.Devices.edit.render(myForm, listOfRoles()));
    }

    public static Result save () {
        //DynamicForm requestData = Form.form().bindFromRequest();
        Device device = Form.form(Device.class).bindFromRequest().get();
        device.save();
        return redirect(routes.Devices.index());
    }

    public static Result update() {
        Form<Device> deviceForm = Form.form(Device.class).bindFromRequest();

        if (deviceForm.hasErrors()) {
            return badRequest(views.html.Devices.edit.render(deviceForm, listOfRoles()));
        }

        // Form is OK, has no errors, we can proceed
        Device device = deviceForm.get();
        device.update(device.id);
        return redirect(routes.Devices.index());
    }

    private static List<String> listOfRoles(){
        List<String> list = new ArrayList<String>();
        for(DeviceRole role : DeviceRole.values()) {
            list.add(role.toString());
        }
        return list;
    }
}
