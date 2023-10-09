/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plane;

import Actuators.OxygenMasksActuator;
import Sensors.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 *
 * @author JUN WEI
 */

public class FlightControlSystem implements Runnable{  
    FlightSimulation simulate;
    ConnectionFactory cf;
    int alt;
    int angleAdjust; 
    int checkAltitude;
    int offAngle =0;
    boolean weatherStatus;
    int speed;
    double distance;
    boolean pressureAlert;
    boolean landingGlear;
    String pressureMessage = "";
  
    public FlightControlSystem(ConnectionFactory cf,FlightSimulation simulate){
        this.cf = cf;
        this.simulate = simulate;
    }
        
    @Override
    public void run() {
        receiveSensorValues();
        
        System.out.println("");
        distance = simulate.getDestination();
        System.out.println("\n[FCS Distance]: Plane current distance to destination is " + distance);
        if(distance>50){
            double newDistance = distance - 50;
            simulate.setDestination(newDistance);
  
            if(pressureAlert == false){
                normalState();
            }else{
                System.out.println("[FCS Cabin Pressure]: !!!!!!!!!!PressureAlert!!!!!!!!!!");
                System.out.println(" ------------------Pressure "+simulate.getCabinPressure()+" is not in normal range-------------");
                System.out.println("                  !!!!!!!!!!PressureAlert!!!!!!!!!!");
                
                adjustAltitude(alt);
                System.out.println("[FCS Altitude]: Plane current altitude is " + alt);

                processWeather(weatherStatus);

                System.out.println("[FCS Speed]: Current plane speed is "+ speed+ "km/h");

                adjustDirection(offAngle);
                System.out.println("[FCS GPS}: Plane current direction is "+offAngle + " degree away from track.");
                
                simulate.setConfirmDeployMasks(true);
                sendMasksCommand(pressureAlert);
            }
            
        }
        else if(distance==50){
            double newDistance = distance - 50;
            simulate.setDestination(newDistance);
            System.out.println("[FCS Plane]: Slowing Down Engine!");
            landingState();
            if(pressureAlert == false){
                System.out.println("[FCS Cabin Pressure]: Pressure "+simulate.getCabinPressure()+" is in normal range");
            }else{
                System.out.println("[FCS Cabin Pressure]: !!!!!!!!!!PressureAlert!!!!!!!!!!");
                System.out.println(" ------------------Pressure "+simulate.getCabinPressure()+" is not in normal range-------------");
                System.out.println("                  !!!!!!!!!!PressureAlert!!!!!!!!!!");
                simulate.setConfirmDeployMasks(true);
                sendMasksCommand(pressureAlert);          
            }
        }
        else{
                if(simulate.getPlaneStatus()!=true){
                    simulate.setPlaneLanded(true);
                    System.out.println("[FCS Plane]: Plane has successfully landed!");
                }else{
                    System.out.println("[FCS Plane]: Shutting Down FCS!");
                    System.exit(0);
                }
        }
        try {
                   Thread.sleep(1000);
               } catch (InterruptedException ex) {
                   Logger.getLogger(FlightControlSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    public void receiveSensorValues(){
        try {
            String altitudeQueue = "altitude";
            String gpsQueue = "gps";
            String weatherQueue = "weather";
            String speedQueue = "speed";
            String cabinPressureQueue = "cabinPressure";
            
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();
            
            chan.queueDeclare(altitudeQueue,false,false,false,null);
            chan.queueDeclare(gpsQueue,false,false,false,null);
            chan.queueDeclare(weatherQueue,false,false,false,null);
            chan.queueDeclare(speedQueue, false, false, false, null);
            chan.queueDeclare(cabinPressureQueue , false, false, false, null);
            
            //altitude
            chan.basicConsume(altitudeQueue,(x,msg)->{
                String m = new String(msg.getBody(),"UTF-8");
                alt = Integer.parseInt(m);
            }, x->{});
            
            //gps
            chan.basicConsume(gpsQueue,(x,msg)->{
                String m = new String(msg.getBody(),"UTF-8");
                offAngle = Integer.parseInt(m);
            }, x->{});
            
            //weather
            chan.basicConsume(weatherQueue,(x,msg)->{
                String m = new String(msg.getBody(),"UTF-8");
                weatherStatus = Boolean.parseBoolean(m);
            }, x->{});
            
            //speed
            chan.basicConsume(speedQueue,(x,msg)->{
                String m = new String(msg.getBody(),"UTF-8");
                speed = Integer.parseInt(m);
            }, x->{});
            
            //cabin pressure
            chan.basicConsume(cabinPressureQueue,(x,msg)->{
                String m = new String(msg.getBody(),"UTF-8");
                pressureAlert = Boolean.parseBoolean(m);
            }, x->{});
           
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(FlightControlSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public boolean checkDistance(){
        
        if(distance>0){
            return true;
        }else{
            return false;
        }
    }
    
    public void normalState(){
        System.out.println("[FCS Cabin Pressure]: Pressure "+simulate.getCabinPressure()+" is in normal range");

        adjustAltitude(alt);
        System.out.println("[FCS Altitude]: Plane current altitude is " + alt);

        processWeather(weatherStatus);

        System.out.println("[FCS Speed]: Current plane speed is "+ speed+ "km/h");

        adjustDirection(offAngle);
        System.out.println("[FCS GPS}: Plane current direction is "+offAngle + " degree away from track.");    
    }
    
    public void landingState(){
        sendWingsCommand(-45);
        System.out.println("[FCS Altitude]: Plane current altitude is " + alt);
        processWeather(weatherStatus);
        System.out.println("[FCS Speed]: Current plane speed is "+ speed+ "km/h");
        adjustDirection(offAngle);
        System.out.println("[FCS GPS}: Plane current direction is "+offAngle + " degree away from track."); 
        sendLandingGearCommand(true);
        simulate.setLandingPhase(true);
    }
    
    public void lowCabinPessureEvent(){
       sendWingsCommand(45);
       adjustDirection(offAngle);
    }
    
    
    public void adjustAltitude(int current_alt){
        checkAltitude = current_alt - FlightSimulation.idealAltitude;
        int idealAltitude = (int) (FlightSimulation.idealAltitude * 0.1);
        
        if (checkAltitude > idealAltitude || checkAltitude < -idealAltitude) {
            if(Math.abs(checkAltitude) > idealAltitude*2){
                angleAdjust = 45;
            } else angleAdjust= 25; 
            if(checkAltitude > 0) angleAdjust = -angleAdjust; 

        } else {
            angleAdjust = 0;
        }
   
        sendWingsCommand(angleAdjust);
}
    
    public void adjustDirection(int offAngle){
        if (offAngle<0){
        angleAdjust = 20; 
    }else angleAdjust =-20;
        
        sendTailCommand(angleAdjust);
    }
    
    public void processWeather(boolean weather){
        if(weather==false){
            System.out.println("[FCS Weather condition]: Current weather condition is good");
        }else{
            System.out.println("[FCS Weather condition]: Current weather condition is bad");
        }
    }
    
    public void sendWingsCommand(int angle){
      String queueName = "wingsAngle";
       try {
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();

            chan.queueDeclare(queueName,false,false,false,null);
                        
            chan.basicPublish("", queueName, null, Integer.toString(angle).getBytes());
            chan.close();
            con.close();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(AltitudeSensor.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    public void sendTailCommand(int angle){
      String queueName = "tailAngle";
       try {
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();

            chan.queueDeclare(queueName,false,false,false,null);
                        
            chan.basicPublish("", queueName, null, Integer.toString(angle).getBytes());
            chan.close();
            con.close();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(AltitudeSensor.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    public void sendMasksCommand(boolean PressureAlert){
      String queueName = "oxygenMasks";
       try {
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();
                     
            chan.queueDeclare(queueName,false,false,false,null);
            
            chan.basicPublish("", queueName, null, Boolean.toString(PressureAlert).getBytes());
            chan.close();
            con.close();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(AltitudeSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendLandingGearCommand(boolean landingGear){
      String queueName = "landinggear";
       try {
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();
                     
            chan.queueDeclare(queueName,false,false,false,null);
            
            chan.basicPublish("", queueName, null, Boolean.toString(landingGear).getBytes());
            chan.close();
            con.close();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(AltitudeSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
