/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deidentifier;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author gerikson
 */
public class Notification implements Runnable{

    public void inProgress() {
                JFrame frame =  new JFrame();
                frame.setContentPane(new JPanel());
               //Display the window.
               frame.setSize(500, 150);
               frame.setBackground(Color.white);
               frame.setVisible(true); 
               frame.setTitle("Read Me!");
               javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();
               jTextArea1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
               jTextArea1.setText("    \n    Please be patient, the process might take a few minutes \n    we will notify you when the deidentification is finished!\n");
               jTextArea1.setEditable(false);
               frame.add(jTextArea1);
               frame.setVisible(true); 
}
    @Override
    public void run() {
        inProgress();
    }
    
}
