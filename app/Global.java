import akka.actor.ActorRef;
import akka.actor.Props;
import logic.UDPBroadcastServer;
import models.ActionRole;
import models.SensorRole;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;


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

}
