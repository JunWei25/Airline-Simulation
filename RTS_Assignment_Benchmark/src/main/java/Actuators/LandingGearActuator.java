/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Actuators;

import Plane.FlightControlSystem;
import Plane.FlightSimulation;
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
public class LandingGearActuator implements Runnable{
    FlightSimulation simulate;
    ConnectionFactory cf;
    String message;
    boolean deployLandingGear=false;
    
    public LandingGearActuator(ConnectionFactory cf,FlightSimulation simulate) {
        this.cf = cf;
        this.simulate = simulate;
    }
    
    @Override
    public void run() {
        if(simulate.getPlaneStatus()!=true){
            receiveTailCommand();
            deployLandingGear();
            try {
                   Thread.sleep(1000);
               } catch (InterruptedException ex) {
                   Logger.getLogger(OxygenMasksActuator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        else{
//            System.out.println("[LandingGear]: Actuator shutting down!");
//        }
    }
    
    public void receiveTailCommand() {
        try {
            String queueName = "landinggear";

            ConnectionFactory cf = new ConnectionFactory();
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();

            chan.queueDeclare(queueName, false, false, false, null);

            //use the channel to consume/receive the message
            chan.basicConsume(queueName, (x, msg) -> {
                String m = new String(msg.getBody(), "UTF-8");
                deployLandingGear = Boolean.parseBoolean(m);
            }, x -> {
            });
            

           
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(FlightControlSystem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void deployLandingGear(){ 
        if(simulate.getLandingPhase()==true&&deployLandingGear==true){
            System.out.println("[LandingGear]: Deploying Landing Gear!!!");
            System.out.println("[LandingGear]: Landing Gear Deployed!");
        }
    }
}
