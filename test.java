package Game;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import calculate.AStar;
import entities.Entity;
import graphic.Engine3D;

public class test{
   public static void main(String[] fuck) {
	  JFrame f = new JFrame();
	  Container c = f.getContentPane();
	  f.setVisible(true);
	  f.setBounds(0,0,900,600);
	  f.setLayout(null);
	  Panel p = new Panel();
	  p.setBounds(0,0,900,600);
	  p.setVisible(true);
	  p.setBackground(Color.black);
	  f.add(p);
	  f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   }
   
   static class Panel extends JPanel implements Runnable{
	  Engine3D e = new Engine3D();
	  
		Thread gamethread;
		Object monitorthread;
		
		//this is the rate things update in, not the graphical fps
	    public int fps = 60;
	    
	  public Panel() {
		 e.addCube(0, 0, 0, 0.5f, 1);
		 startThread();
	  }
	  
      public void startThread() {
   	   gamethread= new Thread(this);
   	   gamethread.start();
      }
      
      public void run() {
   		double drawinterval= 1000000000/fps;
   		double delta=0;
   		long lasttime=System.nanoTime();
   		long currenttime;
   		
   	   while(gamethread!=null) {
   		   
             currenttime=System.nanoTime();
   		   delta+=(currenttime-lasttime)/drawinterval;
   		   lasttime=currenttime;
   		   
   		   
   		   if(delta>=1) {
   		   repaint();
   		   delta--;
   		   }
   	   }
      }
      
	  public void paintComponent(Graphics g) {
		  super.paintComponent(g);
		 Graphics2D g2 = (Graphics2D)g;
		 e.loadMeshes(g2);
	  }
   }
}