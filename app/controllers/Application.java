package controllers;

import play.Logger;
import play.mvc.*;

import views.html.*;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Application extends Controller {

    public static Result index() {
         String ipAddress = "";
     	  try {
              //InetAddress ip = InetAddress.getLocalHost();
              //ipAddress = ip.getHostAddress();
              Enumeration e = NetworkInterface.getNetworkInterfaces();
              while(e.hasMoreElements())
              {
                  NetworkInterface n = (NetworkInterface) e.nextElement();
                  Enumeration ee = n.getInetAddresses();
                  while (ee.hasMoreElements())
                  {
                      InetAddress i = (InetAddress) ee.nextElement();
                      if(i.getHostAddress().startsWith("192")){
                          if(ipAddress.length() > 0) ipAddress += ", ";
                          ipAddress += i.getHostAddress();
                      }
                  }
              }
     	  } catch (Exception e) {
              Logger.warn("Problem getting IP address", e);
              ipAddress = "(unknown)";
     	  }

        return ok(index.render(models.Action.isSleepMode(), ipAddress));
    }

}
