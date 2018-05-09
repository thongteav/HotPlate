/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotplate;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Provides the main UI of simulating a hot plate.
 * 
 * @author Thong Teav
 */
public class HotplateGUI extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
    //attributes----------------------------------------------------------------
    private Element[][] elements;
    private DrawingCanvas canvas;
    private Timer timer;
    private final int ROW = 15;
    private final int COL = 15;
    private JSlider heatConstantSlider, tempSlider;
    private JLabel heatConstantLbl, tempLbl;
    
    //constructor---------------------------------------------------------------
    public HotplateGUI(){
        super();
        setLayout(new BorderLayout());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
        canvas = new DrawingCanvas();
        createElements();
        addNeighbours();
        startElementThreads();
        
        createHeatSlider();
        heatConstantLbl = new JLabel("Heat Constant");
        JPanel heatConstantPanel = new JPanel(new BorderLayout());
        heatConstantPanel.add(heatConstantLbl, BorderLayout.WEST);
        heatConstantPanel.add(heatConstantSlider, BorderLayout.CENTER);
        heatConstantPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        createTempSlider();
        tempLbl = new JLabel("Temperature");
        JPanel tempPanel = new JPanel(new BorderLayout());
        tempPanel.add(tempLbl, BorderLayout.WEST);
        tempPanel.add(tempSlider, BorderLayout.CENTER);
        tempPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        add(canvas, BorderLayout.NORTH);
        add(heatConstantPanel, BorderLayout.CENTER);
        add(tempPanel, BorderLayout.SOUTH);
        
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);        
        timer = new Timer(20, this);
        timer.start();
    }
    
    private void createTempSlider(){
        tempSlider = new JSlider(0, 1000, 500);
        tempSlider.setMajorTickSpacing(100);
        tempSlider.setMinorTickSpacing(10);
        tempSlider.setPaintTicks(true);
        tempSlider.setPaintLabels(true);        
    }
    
    private void createHeatSlider(){
        heatConstantSlider = new JSlider(1, 10, 5);
        heatConstantSlider.setMajorTickSpacing(1);
        heatConstantSlider.setPaintLabels(true);
        heatConstantSlider.setPaintTicks(true);
        heatConstantSlider.addChangeListener(new ChangeListener() {//an annonymous class to change the heat constant of all elements when the slider changes its value
            @Override
            public void stateChanged(ChangeEvent e) {
                for(int x = 0; x < elements.length; ++x){
                    for(int y = 0; y < elements[x].length; ++y){
                        elements[x][y].setHeatConstant(heatConstantSlider.getValue() / 10.0);//convert the slider int value to the double value
                    }
                }
            }
        });
    }
    
    private void createElements(){
        elements = new Element[ROW][COL];
        for(int x = 0; x < elements.length; ++x){
            for(int y = 0; y < elements[x].length; ++y){
                elements[x][y] = new Element(0, 1.0, x, y, canvas.width / ROW);//size of the element will vary depending on the screen resolution
            }
        }
    }
    
    /**
     * This method checks the position of the element and add the neighbor elements accordingly.
     */
    private void addNeighbours(){
        for(int x = 0; x < elements.length; ++x){
            for(int y = 0; y < elements[x].length; ++y){
                if(x != elements.length - 1){//as long as it's not the last row, add the element below it
                    elements[x][y].addNeighbour(elements[x + 1][y]);
                }
                if(y != elements[x].length - 1){//as long as it's not the last column, add the element to the right of it
                    elements[x][y].addNeighbour(elements[x][y + 1]);
                }
                if(x != 0){//as long as it's not the first row, add the element above it
                    elements[x][y].addNeighbour(elements[x - 1][y]);
                }
                if(y != 0){//as long as it's not the first column, add the element to the left of it
                    elements[x][y].addNeighbour(elements[x][y - 1]);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getY() * ROW / canvas.width;
        int y = e.getX() * ROW  / canvas.width;
        
        if(x >= 0 && x < ROW && y >= 0 && y < ROW){ 
            elements[x][y].applyTempToElement(tempSlider.getValue());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        //reverse the x and y coordinates because it's different from the 2D array index
        int x = e.getY() * ROW / canvas.width;
        int y = e.getX() * ROW  / canvas.width;
        if(x >= 0 && x < ROW && y >= 0 && y < ROW){  
            elements[x][y].applyTempToElement(tempSlider.getValue());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer){
            canvas.repaint();
        }
    }

    private void startElementThreads() {
        for(int x = 0; x < elements.length; ++x){
            for(int y = 0; y < elements[x].length; ++y){
                elements[x][y].start();
            }
        }
    }
    
    private class DrawingCanvas extends JPanel 
    {
        private int width, height;
        
        public DrawingCanvas()
        {
            //using the windows screen size to adjust the canvas size so that it will work on different screen resolutions
            Toolkit kit = Toolkit.getDefaultToolkit();
            Dimension screenSize = kit.getScreenSize();
            width = screenSize.width / 2;
            height = width;
            setPreferredSize(new Dimension(screenSize.width / 2, screenSize.width / 2));
            setBackground(Color.WHITE);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            //Draw the rectangle using graphics object
            for(int x = 0; x < elements.length; ++x){
                for(int y = 0; y < elements[x].length; ++y){
                    elements[x][y].drawRect(g);
                }
            }
        }
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame("Hot Plate GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new HotplateGUI());
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
