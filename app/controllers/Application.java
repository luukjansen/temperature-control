package controllers;

import play.Logger;
import play.mvc.*;

import views.html.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Application extends Controller {

    public static Result index() {
         String ipAddress;
     	  try {
              InetAddress ip = InetAddress.getLocalHost();
              ipAddress = ip.getHostAddress();
     	  } catch (UnknownHostException e) {
              Logger.warn("Problem getting IP address", e);
              ipAddress = "(unknown)";
     	  }

        return ok(index.render(models.Action.sleepMode, ipAddress));
    }

}
