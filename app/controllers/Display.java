package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.LogItem;
import models.Sensor;
import models.SensorRole;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.SimpleDateFormat;
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

        Sensor primary = Sensor.getPrimarySensor();
        result.put("primaryTemp", primary.value);

        String formattedDate = new SimpleDateFormat("dd MMM yy - HH:mm").format(primary.lastUpdate);
        result.put("lastUpdate", formattedDate);

        if(secondarySensors.size() > 0) result.put("secondTemp1", secondarySensors.get(0).value);
        if(secondarySensors.size() > 1) result.put("secondTemp2", secondarySensors.get(1).value);
        if(secondarySensors.size() > 2) result.put("secondTemp3", secondarySensors.get(2).value);

        return ok(result);
    }

}
