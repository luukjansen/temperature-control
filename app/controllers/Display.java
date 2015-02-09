package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
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
        result.put("primaryTempName", nameString(primary));

        String formattedDate = new SimpleDateFormat("dd MMM yy - HH:mm").format(new Date());
        result.put("lastUpdate", formattedDate);

        if (secondarySensors.size() > 0) {
            if (secondarySensors.get(0).lastUpdate.after(cutOff)) {
                result.put("secondTemp1", secondarySensors.get(0).value);
            } else {
                result.put("secondTemp1", "--");
            }
            result.put("secondTemp1Name", nameString(secondarySensors.get(0)));
        }
        if (secondarySensors.size() > 1) {
            if (secondarySensors.get(0).lastUpdate.after(cutOff)) {
                result.put("secondTemp2", secondarySensors.get(1).value);
            } else {
                result.put("secondTemp2", "--");
            }
            result.put("secondTemp2Name", nameString(secondarySensors.get(1)));
        }
        if (secondarySensors.size() > 2) {
            if (secondarySensors.get(0).lastUpdate.after(cutOff)) {
                result.put("secondTemp3", secondarySensors.get(2).value);
            } else {
                result.put("secondTemp3", "--");
            }
            result.put("secondTemp3Name", nameString(secondarySensors.get(2)));
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

    private static String nameString(Sensor sensor){
        float upLimit = 0;
        float lowerLimit = 0;
        String returnString = sensor.name;

        for(Action action : sensor.actions){
            if(action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                upLimit = action.tempHigh;
                lowerLimit = action.tempLow;
            }
        }

        if(upLimit > 0 && lowerLimit > 0) {
            returnString += " (" + String.format("%.1f", upLimit) +"˚/" + String.format("%.1f", lowerLimit) +"˚)";
        }
        return returnString;
    }

}
