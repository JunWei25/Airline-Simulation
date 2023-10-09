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
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JUN WEI
 */
public class CabinPressureSensor implements Runnable{
    ConnectionFactory cf;
    FlightSimulation simulate;
    boolean pressureAlert;
    
    public CabinPressureSensor(ConnectionFactory factory, FlightSimulation simulate){
        this.cf = factory;
        this.simulate = simulate;
    }
    
    @Override
    public void run() {
       generateCabinPressure();
       sendCabinPressureValue();
       try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(CabinPressureSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean generateCabinPressure(){
        pressureAlert = false;
        Random rand = new Random();
        int randomGeneratedPressure =rand.nextInt(2);
//        randomGeneratedPressure *= -1;
        if(rand.nextBoolean()){
            
        }
        int newCabinPressure = simulate.getNormalCabinPressure()-randomGeneratedPressure;
        simulate.setCabinPressure(newCabinPressure);
        if(newCabinPressure<8){
            pressureAlert = true;
        }
        return pressureAlert;
    }
    
    
    public void sendCabinPressureValue(){
       String cabinPressure = "cabinPressure";
       try {          
            Connection con = cf.newConnection();
            Channel chan = con.createChannel();                   
            chan.queueDeclare(cabinPressure,false,false,false,null);
         
            chan.basicPublish("", cabinPressure, null, Boolean.toString(pressureAlert).getBytes());
            chan.close();
            con.close();
            
           try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {
               Logger.getLogger(CabinPressureSensor.class.getName()).log(Level.SEVERE, null, ex);
           }
           
           pressureAlert = false;
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(CabinPressureSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
