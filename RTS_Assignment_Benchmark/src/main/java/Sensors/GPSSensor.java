/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Sensors;

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
public class GPSSensor implements Runnable{
    FlightSimulation simulate;
    ConnectionFactory cf;
    
    int offAngle=0;

    public GPSSensor(ConnectionFactory factory, FlightSimulation simulate){
        this.cf = factory;
        this.simulate = simulate;
    }
    
     public void changeInOA() {
        offAngle += simulate.generateRandomOA();    
    }

    @Override
    public void run() {
        changeInOA();
        sendGPSValue(offAngle);
    }

    public void sendGPSValue(int offAngle){
       String queueName = "gps";
        
       try { 
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();
            
            //convert message
            String msg = Integer.toString(offAngle);
                       
            chan.queueDeclare(queueName,false,false,false,null);
            
            //publish the message to the exchange using the routing key
            chan.basicPublish("", queueName, null, msg.getBytes());
            chan.close();
            con.close();
            try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(GPSSensor.class.getName()).log(Level.SEVERE, null, ex);}
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(GPSSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
      
}
