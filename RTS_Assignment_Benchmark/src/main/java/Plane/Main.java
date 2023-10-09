/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plane;

import Sensors.*;
import Actuators.*;
import com.rabbitmq.client.ConnectionFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;

/**
 *
 * @author JUN WEI
 */

public class Main {
    
    public static void main(String[] args) throws Exception{
        
        ConnectionFactory cf = new ConnectionFactory();
        FlightSimulation simulate = new FlightSimulation();
        ScheduledExecutorService es = Executors.newScheduledThreadPool(10);
        
        es.scheduleAtFixedRate(new AltitudeSensor(cf,simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new GPSSensor(cf,simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new WeatherSensor(), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new SpeedSensor(cf,simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new CabinPressureSensor(cf,simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new TailActuator(cf,simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new WingFlapsActuator(cf,simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new OxygenMasksActuator(cf,simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new LandingGearActuator(cf, simulate), 0, 1, TimeUnit.SECONDS);
        es.scheduleAtFixedRate(new FlightControlSystem(cf,simulate), 0, 1, TimeUnit.SECONDS);
        
        
    }
}

