package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Action;
import models.Device;
import models.Sensor;
import models.SensorRole;
import org.apache.commons.codec.binary.Base64;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Luuk on 25/01/15.
 */
public class Api extends Controller {

    private static final String encryptionKey = "TTksVT7gRCnXnpXvE4xstrK5JyNZwjLIQ9t9axlr";

    public static Result sensorData(String data) {
        ObjectNode result = Json.newObject();
        try {
            if(data == null) throw new Exception("No data given");

            byte[] dataBytes = Base64.decodeBase64(data);
            String dataString = new String(dataBytes, "UTF-8");
            String decodedData = decrypt(dataString);

            String remoteAddress = request().remoteAddress();
            //Logger.debug("Remote address for server stats: " + remoteAddress + " with data " + decodedData);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(decodedData, JsonNode.class); // src can be a File, URL, InputStream etc

            if(!rootNode.get("status").asText().equalsIgnoreCase("ok")) throw new Exception("Check this sensor, as it gives a error: " + rootNode.get("status").asText());

            String uniqueId = rootNode.get("serial").asText();
            Device device = Device.find.where().ilike("uniqueId", uniqueId).findUnique();

            if(device == null){
                // Create a device for this serial
                device = new Device();
                device.name = "Unknown";
                device.uniqueId = uniqueId;
                device.ipAddress = remoteAddress;
                device.save();
            }

            ArrayNode actions = mapper.createArrayNode();

            // Get the sensor readings
            if(rootNode.get("sensors").isArray()){
                for (JsonNode node : rootNode.get("sensors")) {
                    String sensorId = node.get("sensor").asText();
                    Sensor sensor = Sensor.find.where().ilike("sensorId", sensorId).findUnique();

                    if(sensor == null){
                        sensor = new Sensor();
                        sensor.device = device;
                        sensor.name = "Unknown";
                        sensor.sensorId = sensorId;
                        sensor.roles.add(SensorRole.findByRoleName(SensorRole.RoleName.UNKNOWN));
                        sensor.save();
                    } else {
                        if (!Objects.equals(sensor.device.id, device.id)) throw new Exception("Problem, the sensor found belongs to another device...");
                    }

                    sensor.value = (float) node.get("value").asDouble();
                    sensor.save();

                    // Perform the actions
                    for(Action action : sensor.actions){
                        String sAction = action.checkForAction();
                        if(sAction != null){
                            ObjectNode actionObject = Json.newObject();
                            actionObject.put("action", "setHigh");
                            actionObject.put("pin", action.pin);
                            actions.add(actionObject);
                        }
                    }

                }
            }


            /*
            ArrayNode actions = mapper.createArrayNode();
            if(tempTemp > 21){
                ObjectNode action = Json.newObject();
                action.put("action", "setHigh");
                action.put("pin", 4);

                actions.add(action);
            } else {
                ObjectNode action = Json.newObject();
                action.put("action", "setLow");
                action.put("pin", 4);

                actions.add(action);
            }
            */

            result.put("actions", actions);

            result.put("time", "SUCCESS");
            result.put("status", "SUCCESS");

            return ok(result);
        } catch (Exception exception) {
            Logger.warn("Problem processing stats: " + exception.getMessage());
            result.put("status", "ERROR");
            result.put("message", exception.getMessage());
            return badRequest(result);
        }
    }

    private static String decrypt(String encrypted) throws Exception {
        DESKeySpec keySpec = new DESKeySpec(encryptionKey.getBytes("UTF8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        Base64 base64decoder = new Base64();
        byte[] encrypedBytes = base64decoder.decode(encrypted);

        // Decrypt cipher
        Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = (cipher.doFinal(encrypedBytes));

        return new String(plainText);
    }

}
