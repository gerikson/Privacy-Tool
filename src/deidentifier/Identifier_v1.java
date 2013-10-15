/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deidentifier;

import java.awt.Color;
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

/**
 *
 * @author gerikson
 */
class Identifier_v1 {
    
    public static javax.swing.JFileChooser fileChooser;
    public static File file;
    public static ArrayList<String> AnnotatedData = new ArrayList<String>();
    public static String head;
    public static String fileName;
    public static JFrame tempFrame;
    
    public Identifier_v1(){
        fileChooser = new javax.swing.JFileChooser();
    }
    
    
    public static void openAnnotatedFile() throws FileNotFoundException, IOException {    
        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        fileName = file.getName();
       // System.out.println("New file name is: " + fileName);
        BufferedReader bReader;
        bReader = new BufferedReader(
        new FileReader(file));
        String line;
        int datacount = 0;
         while((line = bReader.readLine()) != null) {
            
             //skip the header line
            if (datacount == 0) {
    
                 head = line;
               //  System.out.println("this is the header line");
                           try{ 
                        String newFilename = fileName + "_Final.txt"; 
                        System.out.println("Final identified file is: " + newFilename);
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter(newFilename, true));  
                        String headerLine = "Genotype".concat("\t").concat(line); 
                       // System.out.println("New header line" + headerLine);
                        bw.write(headerLine);
                        bw.newLine();
                        bw.close();
                         }
                        catch(Exception e) {} 
                 datacount++;
                 continue;
             } 
            
             AnnotatedData.add(line);
    }
         openIdentified();
       //  System.out.println("Identification succesful!");
    }
        
}
    public static void openIdentified() {
             //  final JFrame frame =  new JFrame();
               tempFrame = new JFrame();
                tempFrame.setContentPane(new JPanel());

               //Display the window.
               tempFrame.setSize(300, 150);
               tempFrame.setBackground(Color.white);
               tempFrame.setLocationRelativeTo(null);
               tempFrame.setVisible(true); 
               tempFrame.setTitle("Please read!");
               JLabel label = new JLabel("Please select the identified file!       \n");
               JButton select = new JButton("Select");
               select.addActionListener(new java.awt.event.ActionListener() {
                  public void actionPerformed(java.awt.event.ActionEvent evt) {
                       try {
                           LoadIdentified();
                          // frame.dispose();
                       } catch (FileNotFoundException ex) {
                           Logger.getLogger(Identifier_v1.class.getName()).log(Level.SEVERE, null, ex);
                       } catch (IOException ex) {
                           Logger.getLogger(Identifier_v1.class.getName()).log(Level.SEVERE, null, ex);
                       }
                  }
               });
               tempFrame.add(label);
               tempFrame.add(select);
               tempFrame.setVisible(true); 
    }
    
    public static void LoadIdentified() throws FileNotFoundException, IOException {
        int datacount = 0;
        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            tempFrame.dispose();
        file = fileChooser.getSelectedFile();
       // String fileName = file.getName();
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
             /*
              * Progress
              */
             if (datacount % 10000 == 0) {
                 System.out.print("Progress: ");
                 System.out.print(datacount);
                 System.out.print(" lines identified");
                 System.out.println("\n");
             } 
             String[] anotatedTemp = anotated.split("\t");
             if (temp[0].equals(anotatedTemp[1]) && temp[1].equals(anotatedTemp[2]) && temp[2].equals(anotatedTemp[3]) 
                     && temp[3].equals(anotatedTemp[4]) && temp[4].equals(anotatedTemp[5]) && temp[5].equals(anotatedTemp[6])) {
                           try{ 
                        String newFilename = fileName + "_Final.txt";         
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter(newFilename, true));  
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
                        String newFilename = fileName + "_Final.txt";       
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter(newFilename, true));  
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
         
        System.out.println("Identification succesful!");
    }
} 
}
