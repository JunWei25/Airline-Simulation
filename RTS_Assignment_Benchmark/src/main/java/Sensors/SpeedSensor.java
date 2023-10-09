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
public class SpeedSensor implements Runnable{
    FlightSimulation simulate;
    ConnectionFactory cf;
    int planeSpeed;
    
    public SpeedSensor(ConnectionFactory cf, FlightSimulation simulate){
        this.cf = cf;
        this.simulate = simulate;
    }
    
    
    @Override
    public void run() {
        planeSpeed = simulate.getSpeed()+simulate.generateRandomSpeed();
        String queueName = "speed";
        try {
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();
            chan.queueDeclare(queueName, false, false, false, null);
          
            chan.basicPublish("", queueName, null, String.valueOf(planeSpeed).getBytes());
            chan.close();
            con.close();
          try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(SpeedSensor.class.getName()).log(Level.SEVERE, null, ex);}
        } catch (TimeoutException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
