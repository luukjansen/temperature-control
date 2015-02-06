package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Action;
import models.LogItem;
import models.Sensor;
import models.SensorRole;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Luuk on 26/01/15.
 */
public class Display extends Controller {

    public static Result index() {
        Sensor primarySensor = Sensor.getPrimarySensor();
        List<Sensor> secondarySensors = Sensor.getSecondarySensors();

        LogItem errorItem = LogItem.findLastActiveError();

        return ok(views.html.displayViews.indexView.render(primarySensor, secondarySensors, errorItem));
    }

    public static Result displayData() {
        ObjectNode result = Json.newObject();
        List<Sensor> secondarySensors = Sensor.getSecondarySensors();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        Date cutOff = calendar.getTime();

        Sensor primary = Sensor.getPrimarySensor();
        if (primary.lastUpdate.after(cutOff)) {
            result.put("primaryTemp", primary.value);
        } else {
            result.put("primaryTemp", "--");
        }

        String formattedDate = new SimpleDateFormat("dd MMM yy - HH:mm").format(primary.lastUpdate);
        result.put("lastUpdate", formattedDate);

        if (secondarySensors.size() > 0) {
            if (secondarySensors.get(0).lastUpdate.after(cutOff)) {
                result.put("secondTemp1", secondarySensors.get(0).value);
            } else {
                result.put("secondTemp1", "--");
            }
        }
        if (secondarySensors.size() > 1) {
            if (secondarySensors.get(0).lastUpdate.after(cutOff)) {
                result.put("secondTemp2", secondarySensors.get(1).value);
            } else {
                result.put("secondTemp2", "--");
            }
        }
        if (secondarySensors.size() > 2) {
            if (secondarySensors.get(0).lastUpdate.after(cutOff)) {
                result.put("secondTemp3", secondarySensors.get(2).value);
            } else {
                result.put("secondTemp3", "--");
            }
        }

        LogItem errorItem = LogItem.findLastActiveError();
        if (errorItem == null) {
            result.put("status", "OK");
        } else if(Action.sleepMode) {
            result.put("status", "In sleep mode...");
        } else {
            result.put("status", errorItem.message);
        }

        return ok(result);
    }

}
