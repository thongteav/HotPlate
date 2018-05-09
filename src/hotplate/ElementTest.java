package hotplate;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class is a test class for checking whether or not the element class is adjusting the temperature correctly.
 * @author Thong Teav
 */
public class ElementTest {
    public void startTest(){
        //create two elements
        Element e1 = new Element(100, 0.05, 0, 0, 50);
        Element e2 = new Element(0, 0.05, 0, 1, 50);
        
        //add them as each other's neighbour
        e1.addNeighbour(e2);
        e2.addNeighbour(e1);
        
        //start the element threads
        e1.start();
        e2.start();
    }
}
