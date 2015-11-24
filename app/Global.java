import akka.actor.ActorRef;
import akka.actor.Props;
import com.avaje.ebean.Ebean;
import logic.UDPBroadcastServer;
import models.*;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import play.libs.Yaml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        if (SensorRole.find.findRowCount() != SensorRole.RoleName.values().length) {
            for (SensorRole.RoleName roleName : SensorRole.RoleName.values()) {
                SensorRole newRole = new SensorRole();
                newRole.name = roleName.toString();
                newRole.save();
            }
        }

        if (ActionRole.find.findRowCount() != ActionRole.RoleName.values().length) {
            for (ActionRole.RoleName roleName : ActionRole.RoleName.values()) {
                ActionRole newRole = new ActionRole();
                newRole.name = roleName.toString();
                newRole.save();
            }
        }

        ActorRef instance = Akka.system().actorOf(Props.create(UDPBroadcastServer.class), "UDPServer");
        instance.tell("Start", ActorRef.noSender());

        Logger.debug("Akka path: " + instance.path());

        if(app.isDev()) InitialData.insert(app);

        super.onStart(app);
    }

    @Override
    public void onStop(Application app){
        Logger.debug("Stopping system...");
        ActorRef test = Akka.system().actorFor("//application/user/UDPServer");
        test.tell("Stop", ActorRef.noSender());

        Akka.system().shutdown();

        super.onStop(app);
    }

    static class InitialData {

        public static void insert(Application app) {
            if(Ebean.find(Device.class).findRowCount() == 0) {

                Device device = new Device();
                device.name = "Test";
                device.ipAddress = "192.168.1.1";
                device.uniqueId = "Test";
                device.save();

                Sensor sensor1 = new Sensor();
                sensor1.device = device;
                sensor1.name = "Sensor 1";
                sensor1.lastUpdate = new Date();
                sensor1.value = 20.0f;

                List<SensorRole> roles = new ArrayList<>();
                roles.add(SensorRole.find.ref(1l));

                sensor1.roles = roles;

                sensor1.save();

                Action action1 = new Action();
                action1.name = "Temp1";
                action1.sensor = sensor1;
                action1.tempLow = 18f;
                action1.tempHigh = 20f;

                List<ActionRole> actionRoles = new ArrayList<>();
                actionRoles.add(ActionRole.findByRoleName(ActionRole.RoleName.TEMPERATURE));

                action1.roles = actionRoles;
                action1.save();


            }
        }

    }

}
