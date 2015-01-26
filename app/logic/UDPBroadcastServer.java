package logic;

import akka.actor.*;
import play.Logger;
import scala.Option;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Luuk on 24/01/15.
 */
public class UDPBroadcastServer extends UntypedActor {

    DatagramSocket socket;
    DatagramPacket packet;
    boolean stop;


    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        Logger.debug("Trying to restart...");

        super.preRestart(reason, message);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        stop = true;
        Logger.info("Master Killed");
    }

    @Override
    public void onReceive(Object command) throws Exception {
        Logger.info("UDP server: " + command);
        stop = false;
        /*
        try {
            while(true){
                Thread.sleep(1000);
                Logger.debug("Test");
            }
        } catch (Exception e) {
            Logger.error("Crash in UDP server: " + e.getLocalizedMessage());
        }
        */

        String action = (String) command;
        if (action.equals("Start")) {
            new Thread(new Runnable() {
                public void run(){
                    try {
                        //Keep a socket open to listen to all the UDP trafic that is destined for this port
                        socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));

                        socket.setBroadcast(true);

                        while (!stop) {
                            Logger.debug("Ready to receive broadcast packets!");

                            //Receive a packet
                            byte[] recvBuf = new byte[15000];
                            packet = new DatagramPacket(recvBuf, recvBuf.length);
                            socket.receive(packet);

                            //Packet received
                            Logger.debug("Discovery packet received from: " + packet.getAddress().getHostAddress());
                            Logger.debug("Packet received; data: " + new String(packet.getData()));

                            //See if the packet holds the right command (message)
                            String message = new String(packet.getData()).trim();
                            if (message.equals("DISCOVER_TEMPSERVER_REQUEST")) {
                                byte[] sendData = "DISCOVER_TEMPSERVER_RESPONSE".getBytes();

                                //Send a response
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                                socket.send(sendPacket);

                                Logger.debug("Sent packet to: " + sendPacket.getAddress().getHostAddress());
                            }
                        }
                    } catch (IOException ex) {
                        Logger.error("Problem with UDP server:" + ex.getLocalizedMessage());
                    }
                }
            }).start();
        } else if (action.equals("Stop")) {
            Logger.debug("Stopping UDP server!");
            stop = true;
            if(socket != null) socket.close();
        }
    }
}
