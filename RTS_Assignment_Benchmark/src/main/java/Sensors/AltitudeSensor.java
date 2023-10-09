/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package Sensors;

import Plane.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JUN WEI
 */
public class AltitudeSensor implements Runnable{
    FlightSimulation simulate;
    ConnectionFactory cf;
    int altitude = FlightSimulation.idealAltitude;

    public AltitudeSensor(ConnectionFactory factory, FlightSimulation simulate){
        this.cf = factory;
        this.simulate = simulate;
    }
   
    @Override
    public void run() {
        changeInAltitude();
        sendAltitudeValue(altitude);
        try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(AltitudeSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void changeInAltitude() {
        altitude += simulate.generateRandomAlt();    
    }

    public void sendAltitudeValue(int alt){
       String queueName = "altitude";
       
       try {          
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();                   
            chan.queueDeclare(queueName,false,false,false,null);
            
            chan.basicPublish("", queueName, null, Integer.toString(alt).getBytes());
            chan.close();
            con.close();
           try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(AltitudeSensor.class.getName()).log(Level.SEVERE, null, ex);
           }
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(AltitudeSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
}

