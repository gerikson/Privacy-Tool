/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deidentifier;

import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 *
 * @author gerikson
 */
class Identifier extends JFrame implements Runnable{
    
    public static javax.swing.JFileChooser fileChooser;
    public static File file;
    public static ArrayList<String> AnnotatedData = new ArrayList<String>();
    public static String head;
    public static String fileName;
    
    public static JFrame frame;
    public static Container content;
    public static JProgressBar progressBar;
    public static Border border;
    public static double percentage = 0;
    
    public Identifier(){
        fileChooser = new javax.swing.JFileChooser();
        
         frame = new JFrame("Deidentificator");
         content = frame.getContentPane();
         progressBar = new JProgressBar();
         frame.setSize(300, 100);
    }
    
    
    public static void openAnnotatedFile() throws FileNotFoundException, IOException {    
        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        fileName = file.getName();
        BufferedReader bReader;
        bReader = new BufferedReader(
        new FileReader(file));
        String line;
        int datacount = 0;
         while((line = bReader.readLine()) != null) {
     /*
      * Uncoment this when ready for release, no header line in the test file
      */
            
             //skip the header line
    /*        if (datacount == 0) {
    
                 head = line;
                 System.out.println("this is the header line");
                           try{ 
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter("Final.txt", true));  
                        String headerLine = "Genotype".concat("\t").concat(line); 
                        System.out.println("New header line" + headerLine);
                        bw.write(headerLine);
                        bw.newLine();
                        bw.close();
                         }
                        catch(Exception e) {} 
                 datacount++;
                 continue;
             } */
            
             AnnotatedData.add(line);
    }
         openIdentified();

    }
        
}
    public static void openIdentified() {
               final JFrame frame =  new JFrame();
               frame.setContentPane(new JPanel());

               //Display the window.
               frame.setSize(300, 150);
               frame.setBackground(Color.white);
               frame.setLocationRelativeTo(null);
               frame.setVisible(true); 
               frame.setTitle("Read Me!");
            /*   javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();
               jTextArea1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
               jTextArea1.setText("    \n    Please select the identified file!");
               jTextArea1.setEditable(false);
               frame.add(jTextArea1); */
               JLabel label = new JLabel("Please select the identified file!       \n");
               JButton select = new JButton("Select");
               frame.add(label);
               frame.add(select);
               frame.setVisible(true); 
               select.addActionListener(new java.awt.event.ActionListener() {
                  public void actionPerformed(java.awt.event.ActionEvent evt) {
                       try {
                           LoadIdentified();
                           frame.dispose();
                       } catch (FileNotFoundException ex) {
                           Logger.getLogger(Identifier.class.getName()).log(Level.SEVERE, null, ex);
                       } catch (IOException ex) {
                           Logger.getLogger(Identifier.class.getName()).log(Level.SEVERE, null, ex);
                       }
                  }
               });
    }
    
    public static void LoadIdentified() throws FileNotFoundException, IOException {
        int datacount = 0;
        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        String fileName = file.getName();
        BufferedReader bReader;
        bReader = new BufferedReader(
        new FileReader(file));
       String line;
         while((line = bReader.readLine()) != null) {
             
             
             
             String[] temp = line.split("\t");
             if (datacount > AnnotatedData.size()) {
                 System.out.println("Something terible has happened. "
                         + "The identificated file might not be correct file. In this case please restart the program and load the correct file. "
                         + "If the problem persists please contact Galina Erikson at gerikson@scripps.edu");
                 break;
             }
             String anotated = AnnotatedData.get(datacount);
             String[] anotatedTemp = anotated.split("\t");
             if (temp[0].equals(anotatedTemp[1]) && temp[1].equals(anotatedTemp[2]) && temp[2].equals(anotatedTemp[3]) 
                     && temp[3].equals(anotatedTemp[4]) && temp[4].equals(anotatedTemp[5]) && temp[5].equals(anotatedTemp[6])) {
                           try{ 
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter("Final.txt", true));  
                        String newLine = temp[6].concat("\t").concat(anotated);
                        bw.write(newLine);
                        bw.newLine();
                        bw.close();
                  //      System.out.println("Line found! ");
                        datacount++;
                         }
                        catch(Exception e) {} 
             } else {
                 //go till you find it
                 for (int i=datacount; i<AnnotatedData.size(); i++) {
                   //  System.out.println("i is: " + i);
                     String anotatedNew = AnnotatedData.get(i);
                     String[] anotatedTempNew = anotatedNew.split("\t");
                     if (temp[0].equals(anotatedTempNew[1]) && temp[1].equals(anotatedTempNew[2]) && temp[2].equals(anotatedTempNew[3]) 
                     && temp[3].equals(anotatedTempNew[4]) && temp[4].equals(anotatedTempNew[5]) && temp[5].equals(anotatedTempNew[6])) {
                           try{ 
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter("Final.txt", true));  
                        String newLine = temp[6].concat("\t").concat(anotated);
                        bw.write(newLine);
                        bw.newLine();
                        bw.close();
                        datacount = i+1;
                        break;
                         }
                        catch(Exception e) {} 
                 }
             }
                 continue;
         }
        }
               final JFrame frame =  new JFrame();
               frame.setContentPane(new JPanel());

               //Display the window.
               frame.setSize(300, 150);
               frame.setBackground(Color.white);
               frame.setLocationRelativeTo(null);
               frame.setVisible(true); 
               frame.setTitle("Read Me!");  
               JLabel label = new JLabel("Identification succesful!       \n");
               frame.add(label);
               JLabel label2 = new JLabel("The identified file is Final.txt located in the application folder!\n");
               frame.setVisible(true); 
         
    }
} 
    
        @Override
    public void run() {
     
            final String orgName = Thread.currentThread().getName();
            Thread.currentThread().setName(orgName + "firstThread");
            try {
                   percentage=0;
            } finally {
                Thread.currentThread().setName(orgName);
            }
        }
}
