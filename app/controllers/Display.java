package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.LogItem;
import models.Sensor;
import models.SensorRole;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luuk on 26/01/15.
 */
public class Display extends Controller {

    public static Result index() {
        Sensor primarySensor = Sensor.getPrimarySensor();
        List<Sensor> secondarySensors = Sensor.getSecondarySensors();

        List<LogItem> errors = new ArrayList<>();

        return ok(views.html.displayViews.indexView.render(primarySensor, secondarySensors, errors));
    }

    public static Result displayData(){
        ObjectNode result = Json.newObject();
        List<Sensor> secondarySensors = Sensor.getSecondarySensors();

        result.put("primaryTemp", Sensor.getPrimarySensor().temp);
        result.put("secondTemp1", "");
        result.put("secondTemp2", "");
        result.put("secondTemp3", "");
        if(secondarySensors.size() > 0) result.put("secondTemp1", secondarySensors.get(0).temp);
        if(secondarySensors.size() > 0) result.put("secondTemp2", secondarySensors.get(1).temp);
        if(secondarySensors.size() > 0) result.put("secondTemp3", secondarySensors.get(2).temp);

        return ok(result);
    }

}
