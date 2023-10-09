/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Actuators;

import Plane.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JUN WEI
 */
public class WingFlapsActuator implements Runnable{
    FlightSimulation simulate;
    ConnectionFactory cf;
    int angle = 0;
    String message = "";

    public WingFlapsActuator(ConnectionFactory cf,FlightSimulation sp) {
        this.cf = cf;
        this.simulate = sp;
    }

    @Override
    public void run() {
        if(simulate.getPlaneStatus()!=true){
        receiveWingsCommand();
        adjustWingsAngle();
        try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(WingFlapsActuator.class.getName()).log(Level.SEVERE, null, ex);}
        }
    }

    public void receiveWingsCommand() {
        try {
            String queueName = "wingsAngle";

            ConnectionFactory cf = new ConnectionFactory();
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();

            chan.queueDeclare(queueName, false, false, false, null);

            //use the channel to consume/receive the message
            chan.basicConsume(queueName, (x, msg) -> {
                String m = new String(msg.getBody(), "UTF-8");
                message = m;
                angle = Integer.parseInt(m);
            }, x -> {
            });
            
            
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(WingFlapsActuator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void adjustWingsAngle() {
        String direction = "";
            if (angle < 0) {
                direction = "(Downwards)";
            }
            else if(angle>0) {
                direction="(Upwards)";
            }

            if(message!=""){
                System.out.println("[Wings]: Flap angle has been adjusted to: " + angle+direction);
            }
    }
}
