package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.text.SimpleDateFormat;
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
        if (primary != null) {
            if (primary.getLastUpdate() != null && primary.getLastUpdate().after(cutOff)) {
                result.put("primaryTemp", primary.getValue());
            } else {
                result.put("primaryTemp", "--");
            }
            result.put("primaryTempName", nameString(primary));
            //if(primary.actions.size() > 0) result.put("primaryActions", addSensorData(primary));
        } else {
            result.put("primaryTempName", "(none)");
        }

        String formattedDate = new SimpleDateFormat("dd MMM yy - HH:mm").format(new Date());
        result.put("lastUpdate", formattedDate);

        if (secondarySensors.size() > 0) {
            if (secondarySensors.get(0).getLastUpdate() != null && secondarySensors.get(0).getLastUpdate().after(cutOff)) {
                result.put("secondTemp1", secondarySensors.get(0).getValue());
            } else {
                result.put("secondTemp1", "--");
            }
            result.put("secondTemp1Name", nameString(secondarySensors.get(0)));

        }

        if (secondarySensors.size() > 1) {
            if (secondarySensors.get(1).getLastUpdate() != null && secondarySensors.get(1).getLastUpdate().after(cutOff)) {
                result.put("secondTemp2", secondarySensors.get(1).getValue());
            } else {
                result.put("secondTemp2", "--");
            }
            result.put("secondTemp2Name", nameString(secondarySensors.get(1)));
        }

        if (secondarySensors.size() > 2) {
            if (secondarySensors.get(2).getLastUpdate() != null && secondarySensors.get(2).getLastUpdate().after(cutOff)) {
                result.put("secondTemp3", secondarySensors.get(2).getValue());
            } else {
                result.put("secondTemp3", "--");
            }
            result.put("secondTemp3Name", nameString(secondarySensors.get(2)));
        }

        LogItem errorItem = LogItem.findLastActiveError();
        if (errorItem == null && Action.isSleepMode()) {
            result.put("status", "In sleep mode...");
        } else if(errorItem == null) {
            result.put("status", "OK");
        } else {
            if(errorItem.getMessage() != null && errorItem.getMessage().length() > 0) {
                result.put("status", errorItem.getMessage());
            } else {
                result.put("status", "Unkown error");
            }
        }
        response().setHeader("Cache-Control", "no-cache");
        return ok(result);
    }

    public static Result decreaseLowTemp(){
        ObjectNode result = Json.newObject();

        try {
            long actionId = Long.valueOf(request().getQueryString("actionId"));
            Action action = Action.find.byId(actionId);
            action.setTempLow(action.getTempLow() - 1);
            action.save();
            result.put("result", action.getTempLow());
        } catch (Exception e){
            result.put("result", "error");
            Logger.warn("Problem with changing the temp", e);
        }
        return ok(result);
    }

    //@Transactional
    public static Result decreaseHighTemp(){
        ObjectNode result = Json.newObject();

        try {
            long actionId = Long.valueOf(request().getQueryString("actionId"));
            Action action = Action.find.byId(actionId);
            action.setTempHigh(action.getTempHigh() - 1);
            action.save();
            result.put("result", action.getTempHigh());
        } catch (Exception e){
            result.put("result", "error");
            Logger.warn("Problem with changing the temp", e);
        }
        return ok(result);
    }

    public static Result increaseLowTemp(){
        ObjectNode result = Json.newObject();

        try {
            long actionId = Long.valueOf(request().getQueryString("actionId"));
            Action action = Action.find.byId(actionId);
            action.setTempLow(action.getTempLow() + 1);
            action.save();
            result.put("result", action.getTempLow());
        } catch (Exception e){
            result.put("result", "error");
            Logger.warn("Problem with changing the temp", e);
        }
        return ok(result);
    }

    public static Result increaseHighTemp(){
        ObjectNode result = Json.newObject();

        try {
            long actionId = Long.valueOf(request().getQueryString("actionId"));
            Action action = Action.find.byId(actionId);
            action.setTempHigh(action.getTempHigh() + 1);
            action.save();
            result.put("result", action.getTempHigh());
        } catch (Exception e){
            result.put("result", "error");
            Logger.warn("Problem with changing the temp", e);
        }
        return ok(result);
    }


       /*
    private static JsonNode addSensorData(Sensor sensor){
        ArrayList<JsonNode> list = new ArrayList<>();

        for(Action action : sensor.actions) {
            if(!action.roles.contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) continue;
            ObjectNode actionNode = Json.newObject();
            actionNode.put("name", action.name);
            actionNode.put("tempHigh", action.tempHigh);
            actionNode.put("tempLow", action.tempLow);
            list.add(actionNode);
        }

        return Json.toJson(list);
    }
         */
    private static String nameString(Sensor sensor){
        float upLimit = 0;
        float lowerLimit = 0;
        String returnString = sensor.getName();

        for(Action action : sensor.getActions()){
            if(action.getRoles().contains(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE))) {
                upLimit = action.getTempHigh();
                lowerLimit = action.getTempLow();
            }
        }

        if(upLimit > 0 && lowerLimit > 0) {
            returnString += " (" + String.format("%.1f", lowerLimit) +"˚/" + String.format("%.1f", upLimit) +"˚)";
        }
        return returnString;
    }

}
