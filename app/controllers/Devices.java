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
        return index();
    }

    public static Result edit(Long id){
        Device device = Device.find.byId(id);
        Form<Device> myForm = Form.form(Device.class);
        myForm = myForm.fill(device);

        List<String> list = new ArrayList<String>();
        for(DeviceRole role : DeviceRole.values()) {
            list.add(role.toString());
        }

        return ok(views.html.Devices.edit.render(myForm, list));
    }

    public static Result save () {
        //DynamicForm requestData = Form.form().bindFromRequest();
        Device device = Form.form(Device.class).bindFromRequest().get();
        device.save();
        return index();
    }

    public static Result update() {
        Device device = Form.form(Device.class).bindFromRequest().get();
        device.update();
        return index();
    }
}
