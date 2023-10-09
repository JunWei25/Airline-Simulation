/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Plane;

import java.util.Random;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author JUN WEI
 */
public class FlightSimulation {
    double distanceToDestination = 500;
    public static int idealAltitude = 1000; 
    public boolean weatherCondition = false;
    private int speed = 250; //plane initial speed
    int normalCabinPressure = 8; //normal pressure
    int cabinPressure;
    boolean cabinPressureStatus;
    boolean confirmDeployMasks;
    boolean planeLanded = false;
    boolean landingPhase = false;
    
    Random rand = new Random();
    int offAngleMax = 15;
    int offAngleMin = -15;
    
    public int getSpeed() {
        return speed;
    }
    
    public int getNormalCabinPressure(){
        return normalCabinPressure;
    }
    
    public boolean getCabinPressureStatus(){
        return cabinPressureStatus;
    }
    
    public int getCabinPressure(){
        return cabinPressure;
    }
    
    public boolean getConfirmDeployMasks(){
        return confirmDeployMasks;
    }
    
    public boolean getWeatherCondition() {
        return weatherCondition;
    }
    
    public double getDestination(){
        return distanceToDestination;
    }
    
    public boolean getPlaneStatus(){
        return planeLanded;
    }
    
    public boolean getLandingPhase(){
        return landingPhase;
    }
    
    public void setWeatherCondition(boolean weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
    
    public void setSpeed(int newSpeed){
       this.speed = newSpeed;
    }
    
    public void setCabinPressureStatus(boolean cabinPressure){
        this.cabinPressureStatus = cabinPressure;
    }
    
    public void setCabinPressure(int cabinPressure){
        this.cabinPressure = cabinPressure;
    }
    
    public void setConfirmDeployMasks(boolean deployMasks){
        this.confirmDeployMasks = deployMasks;
    }
    
    public void setDestination(double distanceTravelled){

        this.distanceToDestination = distanceTravelled;
    }
    
    
    public void setPlaneLanded(boolean planeLanded){
        this.planeLanded = planeLanded;
    }
    
    public void setLandingPhase(boolean landingPhase){
        this.landingPhase = landingPhase;
    }
    
    public int generateRandomAlt(){
        return rand.nextInt(200) * (rand.nextBoolean() ? 1 : -1);
    }
    
    public int generateRandomSpeed(){
        return rand.nextInt(100) * (rand.nextBoolean() ? 1 : -1);
    }
    
    public int generateRandomOA(){
   return rand.nextInt(offAngleMax - offAngleMin + 1) + offAngleMin;
    }
        
    public void resetOffAngleRange(){
        offAngleMax = 10;
        offAngleMin = -10;
    }
    
   
    
    public void changeOfDirection(int angle){
        if(angle==0){
            this.resetOffAngleRange();
        }
        else if(angle==20){
            offAngleMax = 3;
            offAngleMin = 0;
        }
        else if(angle==-20){
            offAngleMin = -3;
            offAngleMax = 0;
        }
        else{
            this.resetOffAngleRange();
        }
    }
    
     
}
