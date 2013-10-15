/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deidentifier;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 *
 * @author gerikson
 */
public class ProgressBar extends JFrame implements Runnable {
    
    public JFrame frame;
    public Container content;
    public JProgressBar progressBar;
    public Border border;
    
    public File file;
    public static String fileName;
    public static int fileLines;
    public static boolean status = false;
    public double perc2 = 0;
    
            
    public ProgressBar(File f){
        
        file = f;
       
         frame = new JFrame("File Loading");
         content = frame.getContentPane();
         progressBar = new JProgressBar();
         border = BorderFactory.createTitledBorder("Entire file loading in memory...");
         progressBar.setBorder(border);
         content.add(progressBar, BorderLayout.NORTH);
         frame.setSize(300, 100);
         frame.setVisible(true);
          
         
    }
  
        @Override
     public void run() {
        try {   
            int datacount = 0;
            
            Interface newfile = new Interface();    
            fileName =  file.getName();
            BufferedReader bReader;
           
              bReader = new BufferedReader(new FileReader(file)); //new ProgressBar(file));
                    
               long len = file.length();
                    double y = (double) len;
                    double ing  = 60000000.0;
                    double x = (double) ing/len;
                    double perc = (double) (x*100);
                   String line = null;
                   
  
                try {
                    line = bReader.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(ProgressBar.class.getName()).log(Level.SEVERE, null, ex);
                }

                   for (int i = 0; i < 1; i++) {
                      
                   }
                try {
                    while((line = bReader.readLine()) != null) {
                          
                          datacount++;
                 
             //              
             //            /**
             //             * Test function to see how the program works
             //             */   
                           if (datacount%100000 == 0){
                               
                              // Loading.frame.dispose();
                               System.out.print(datacount + "\t");
                               DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                               Calendar cal = Calendar.getInstance();
                               System.out.println(dateFormat.format(cal.getTime()));
                               perc2 = perc2 + perc;
                               int b = (int) perc2;  
                                progressBar.setValue(b);
                                progressBar.setStringPainted(true);
                           }
                         
                      }
                } catch (IOException ex) {
                    Logger.getLogger(ProgressBar.class.getName()).log(Level.SEVERE, null, ex);
                }


               progressBar.setValue(100);
               progressBar.setStringPainted(true);             
               fileLines = datacount;
               this.frame.dispose();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProgressBar.class.getName()).log(Level.SEVERE, null, ex);
        }
               

                    }


}


