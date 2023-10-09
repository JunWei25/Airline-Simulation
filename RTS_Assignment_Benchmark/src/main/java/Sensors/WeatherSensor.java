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
import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JUN WEI
 */
public class WeatherSensor implements Runnable{
    private ConnectionFactory cf;
    private FlightSimulation simulate;
    
    @Override
    public void run(){
        Random rand = new Random();
        boolean isBadWeather = rand.nextDouble() < 0.2;
       
        try {
            ConnectionFactory cf = new ConnectionFactory();
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();
            chan.queueDeclare("weather", false, false, false, null);
  
            simulate.setWeatherCondition(isBadWeather);
          
            chan.basicPublish("", "weather", null, String.valueOf(isBadWeather).getBytes());
            chan.close();
            con.close();
          try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(WeatherSensor.class.getName()).log(Level.SEVERE, null, ex);}
    } catch (TimeoutException | IOException e) {
      System.out.println(e.getMessage());
    }
    
    }
}
