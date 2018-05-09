/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotplate;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

/**
 * This class represents a single node that can be part of multiple elements on a hotplate and runs in its own thread.
 * 
 * @author Thong Teav
 */
public class Element implements Runnable{
    //attributes----------------------------------------------------------------
    private List<Element> neighbours;
    private double currentTemp;
    private double heatConstant;
    private boolean stopRequested;
    private int x, y, size; //x and y represent the element's index in the 2D array
    
    //constructors--------------------------------------------------------------
    /**
     * Construct an element with defined temperature and heat constant.
     * 
     * @param currentTemp a double represents the temperature when an element is constructed
     * @param heatConstant a double represents the heat constant
     */
    public Element(double currentTemp, double heatConstant){
        this.currentTemp = currentTemp;
        if(heatConstant > 1.0 || heatConstant <= 0.0){ //check whether or not the heat constant is zero or not between 0.0 and 1.0
            throw new IllegalArgumentException("Heat constant should be between 0.0 (exclusive) and 1.0");
        }
        else {
            this.heatConstant = heatConstant;
        }  
        neighbours = new ArrayList<>();
    }
    
    public Element(double currrentTemp, double heatConstant, int x, int y, int size){
        this(currrentTemp, heatConstant);
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    //methods-------------------------------------------------------------------
    /**
     * This method creates a new thread and starts the thread.
     */
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }
    
    /**
     * Returns the current temperature of the element.
     * @return currentTemp a double which is the current temperature of the element
     */
    public synchronized double getTemperature(){
        return this.currentTemp;
    }
    
    /**
     * Request stop to the thread by setting the stopRequested variable to true.
     */
    public void requestStop(){
        this.stopRequested = true;
    }
    
    /**
     * Constantly updates the temperature of the element with its neighboring elements.
     */
    @Override
    public void run(){
        stopRequested = false;
        while(!stopRequested) {
            adjustTemp();
            try {                               
                Thread.sleep(200); //put the thread to sleep to allow other threads to execute                              
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Returns a list of neighboring elements of the element
     * 
     * @return a list of Elements representing the neighboring elements
     */
    public List<Element> getNeighbours(){
        return this.neighbours;
    }
    
    /**
     * Draws a rectangle of a color representing the temperature of the element.
     * 
     * @param g a Graphics object to draw the rectangle on
     */
    public synchronized void drawRect(Graphics g){
        int red = (int) this.getTemperature() * 255 / 1000;//check the temperature to determine how red will the rectangle be, ie. 0 degree means 0 red and 1000 degrees means 255 red
        int blue = 255 - red;//subtract the red value from the blue
        g.setColor(new Color(red, 0, blue));
        g.fillRect(y * size, x * size, size, size);//draw the rectangle
        g.setColor(Color.red);
        g.drawRect(y * size, x * size, size, size);//draw the rectangle outline
    }
    
    /**
     * Adjusts the temperature of the element using by comparing the average temperatures of its neighbors with its own temperature
     */
    public void adjustTemp() {
        double averageTemps = 0;
        double totalTemp = 0;
        //add each neighbor's temperature to the toal temp
        for (Element e : this.neighbours) {
            totalTemp += e.getTemperature();
        }
        averageTemps = totalTemp / this.neighbours.size();//calculate the average temperature
        currentTemp += (averageTemps - currentTemp) * heatConstant;//adjust the current temperature
    }
    
    /**
     * Adds a specified element as a neighbor to the element
     * 
     * @param element a specified element which represents a neighboring element
     */
    public void addNeighbour(Element element){
        this.neighbours.add(element);
    }
    
    /**
     * Apply the specified temperature to the element
     * 
     * @param appliedTemp a specified temperature to apply to the element
     */
    public synchronized void applyTempToElement(double appliedTemp){
        this.currentTemp += (appliedTemp - this.currentTemp) * this.heatConstant;
    }
    
    //getter and setter---------------------------------------------------------
    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

    public double getHeatConstant() {
        return heatConstant;
    }

    public synchronized void setHeatConstant(double heatConstant) {
        this.heatConstant = heatConstant;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }    
}
