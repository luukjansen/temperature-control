package controllers;

import models.Sensor;
import models.SensorRole;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by Luuk on 26/01/15.
 */
public class Display extends Controller {

    public static Result index() {
        List<Sensor> tempSensors = Sensor.find.where().in("roles.name", "DISPLAY").findList();
        return ok(views.html.displayViews.indexView.render(tempSensors));
    }

}
