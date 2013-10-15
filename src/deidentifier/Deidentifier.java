/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deidentifier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 *
 * @author gerikson
 */
public class Deidentifier extends JFrame implements Runnable  {
    public static javax.swing.JFileChooser fileChooser;
    public static File file;
        //ShowTable thread management
    public static ExecutorService threadExecutor;
    public static String fileName;
    public static String newNameGz;
    
    public static JFrame frame;
    public static Container content;
    public static JProgressBar progressBar;
    public static Border border;
    public static double percentage = 0;

    public Deidentifier(){
        fileChooser = new javax.swing.JFileChooser();
        
         frame = new JFrame("Deidentificator");
         content = frame.getContentPane();
         progressBar = new JProgressBar();
         frame.setSize(300, 100);
       
    }
    
    //desides what file is this: complete genomics or vcf file
    public void Validator() throws FileNotFoundException, IOException{
       
       int returnVal = fileChooser.showOpenDialog(fileChooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        fileName = file.getName();
        
        /*
         * take care of the tar.gz , bz2 or zip
         */
        if (fileName.contains("tar.gz") || fileName.contains("bz2") || fileName.contains("zip")) {
            
                JFrame frame =  new JFrame();
                frame.setContentPane(new JPanel());

               //Display the window.
               frame.setSize(300, 150);
               frame.setBackground(Color.white);
            //   frame.setLocationRelativeTo(null);
               frame.setVisible(true); 
               frame.setTitle("Read Me!");
               javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();
               jTextArea1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
               jTextArea1.setText("    \n    For a shorter wait time please\n    uncompres the file and \n    upload the uncompressed file!\n");
               jTextArea1.setEditable(false);
               frame.add(jTextArea1);
               frame.setVisible(true); 
        }
        
        else {
            border = BorderFactory.createTitledBorder("Validating data");
            progressBar.setBorder(border);
            content.add(progressBar, BorderLayout.NORTH);
            percentage = 2;
            progressBar.setValue(2);
            progressBar.setStringPainted(true);
            frame.setVisible(true);
      /*     Notification rf2 = new Notification();
           threadExecutor = Executors.newFixedThreadPool(1);
           threadExecutor.execute(rf2);
           threadExecutor.shutdown(); */
         boolean masterVar = false;
         boolean var = false;
         boolean vcf = false;
         BufferedReader bReader;
         bReader = new BufferedReader(
         new FileReader(file));
         String line = bReader.readLine();

         while((line = bReader.readLine()) != null) {
                             
                       if (line.startsWith("##", 0)) {     
                        vcf = true;   
                        continue;
             
                       } 
                            //get rid of the first lines              
                          else   if (line.startsWith("#", 0)) {   
                               System.out.println("first lines");
                               continue;
                            }
                           
                           else if (line.startsWith(">", 0)) {
                               String l = line.replace(">", "");
                               String[] headSplit = l.split("\t");
                               System.out.println("Header is: " + l);
                               //if chromosome is on the 3rd position this is a masterVar file
                               if (headSplit[2].contains("chromosome")) {
                                   System.out.println("This is a master var file.");
                                   masterVar = true;
                               }
                               continue;
                           } else if (line.isEmpty()) {
                              // System.out.println("Line is empty!" + "\t");
                               continue;
                           } 
                           //good lines, send it to the parsers
                           else {
                               if (vcf) {
                                   System.out.println("This is a vcf file!");
                                   openVCF(file);
                                   break;
                               } else {
                                   openCG(file);
                                   break;
                               }
                           }  
         }
        }
        }
    }
    
    //for complete genomics file
    public void openCG(File file) throws FileNotFoundException, IOException {
        boolean masterVar = false;
        boolean ploidy = true;
        String previousLine = "";
        int counter = 0;
        String fileName = file.getName();
        BufferedReader bReader;
        bReader = new BufferedReader(
        new FileReader(file));
        String line;
         while((line = bReader.readLine()) != null) {
                           
                            //get rid of the first lines
                           if (line.startsWith("#", 0)) {   
                               System.out.println("first lines");
                               continue;
                            }
                           
                           else if (line.startsWith(">", 0)) {
                               String l = line.replace(">", "");
                               String[] headSplit = l.split("\t");
                               System.out.println("Header is: " + l);
                               //if chromosome is on the 3rd position this is a masterVar file
                               if (headSplit[2].contains("chromosome")) {
                                   System.out.println("This is a master var file.");
                                   masterVar = true;
                               }
                               continue;
                           } else if (line.isEmpty()) {
                              // System.out.println("Line is empty!" + "\t");
                               continue;
                           }  
                String[] spLine = line.split("\t");
                

                //is this a master var file?
                if (masterVar) {
                  if (spLine[6].equals("no-call") || spLine[6].equals("no-ref") ||
                        spLine[6].equals("complex") || spLine[6].contains("PAR") ||
                        spLine[6].equals("ref")) {
                    continue;
                }
                 String[] parts = fixVariants(spLine);
                String genotype = "01";    
                    if (parts[5].contains("hom")) {
                        genotype = "11";
                    }
                        try{ 
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter("parsed.txt", true)); 
                        counter++;
                       String goodLine = parts[2].concat("\t").concat(parts[3]).concat("\t").
                            concat(parts[4]).concat("\t").concat(parts[6]).concat("\t").
                            concat(parts[7]).concat("\t").concat(parts[8]).concat("\t").concat(genotype);
                        bw.write(goodLine);
                        bw.newLine();
                        bw.close();
                        continue;
                         }
                        catch(Exception e) {
                        continue;
                        }     
                } 
                //this is regular var
                else {
                    if (spLine.length <6) {
                        ploidy = false;
                        continue;
                    } else if((spLine[6].equals("no-call") || spLine[6].equals("no-ref") ||
                        spLine[6].equals("complex") || spLine[6].contains("PAR") ||
                        spLine[6].equals("ref"))) {
                        
                        //check previous line 
                        if (previousLine.equals("")) {
                            continue;
                        } 
                        //if there is a previous line record it
                        else {
                            String[] t2 = previousLine.split("\t");
                            String[] temp = fixVariants(t2);
                            
                            try{ 
                                 BufferedWriter bw = new BufferedWriter(
                                 new FileWriter("parsed.txt", true)); 
                                 counter++;
                                 String goodLine = temp[2].concat("\t").concat(temp[3]).concat("\t").
                                    concat(temp[4]).concat("\t").concat(temp[6]).concat("\t").
                                    concat(temp[7]).concat("\t").concat(temp[8]).concat("\t").concat("01");
                                bw.write(goodLine);
                                bw.newLine();
                                bw.close();
                                previousLine = "";
                                continue;
                         }
                        catch(Exception e) {
                                continue;
                        }        
                        }
                     // this is a good line lets verify 
                    } else {
                        //if there is no previous line
                        if (previousLine.equals("")) {
                            previousLine = line;
                            continue;
                        } else {
                            String[] t2 = previousLine.split("\t");
                            String[] temp = fixVariants(t2);
                            String[] parts = fixVariants(spLine);
                            if (parts[3].equals(temp[3]) && parts[4].equals(temp[4]) && parts[5].equals(temp[5])
                                && parts[6].equals(temp[6]) && parts[7].equals(temp[7]) && parts[8].equals(temp[8])){
                                try{ 
                                 BufferedWriter bw = new BufferedWriter(
                                 new FileWriter("parsed.txt", true)); 
                                 counter++;
                                 String goodLine = temp[2].concat("\t").concat(temp[3]).concat("\t").
                                    concat(temp[4]).concat("\t").concat(temp[6]).concat("\t").
                                    concat(temp[7]).concat("\t").concat(temp[8]).concat("\t").concat("11");
                                bw.write(goodLine);
                                bw.newLine();
                                bw.close();
                                previousLine = "";
                                continue;
                         }
                        catch(Exception e) {
                                continue;
                        }    
                        } 
                            // record the previousLine and keep this line in memory
                            else {
                                   try{ 
                                 String[] t3 = previousLine.split("\t");
                                 String[] temp1 = fixVariants(t3);      
                                 BufferedWriter bw = new BufferedWriter(
                                 new FileWriter("parsed.txt", true)); 
                                 counter++;
                                 String goodLine = temp1[2].concat("\t").concat(temp1[3]).concat("\t").
                                    concat(temp1[4]).concat("\t").concat(temp1[6]).concat("\t").
                                    concat(temp1[7]).concat("\t").concat(temp1[8]).concat("\t").concat("01");
                                bw.write(goodLine);
                                bw.newLine();
                                bw.close();
                                previousLine = line;
                                continue;
                         }
                        catch(Exception e) {
                                continue;
                        }    
                            }
                    }
                    }
                   
                    
                }
                
                
         }        
          System.out.println("Number of variants in is: " + counter);
         //add the snp132Flagged.vars to the end of the file
         concat();
         // sorting the PipelineInput.txt by chromosome, begin, end
         JavaRunCommand();   
         // delete variants if they are already found in the input file
         deleteDoubles();
         //delete the unused files, keep the parsed file
         cleanUp();
                  //sort the parsed file for future reference
         sortFile("parsed.txt");
         //change file name for the user to be easier to identify
         String filename = file.getName();
         String f = filename.concat(".").concat("Identified");
         Runtime.getRuntime().exec("mv parsed_sorted.txt " + f);
         Runtime.getRuntime().exec("rm parsed.txt");
                         JFrame frame =  new JFrame();
                frame.setContentPane(new JPanel());
               //Display the window.
               frame.setSize(750, 160);
               frame.setBackground(Color.white);
               frame.setVisible(true); 
               frame.setTitle("Read Me!");
               javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();
               jTextArea1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
               String ident = fileName.concat(".Identified"); 
               jTextArea1.setText("    \n                Deidentification successful! \n\n    Please proceed to genomics.scripps.edu and upload the file: \n    " + newNameGz + 
               "\n    Note: don't delete the file " + ident + ", you will need this file for \n    Identification once you have SG ADVISER annotation results.");
               jTextArea1.setEditable(false);
               frame.add(jTextArea1);
               frame.setVisible(true); 
    
   
    }    
    
    public String[] fixVariants(String[] parts) {
        if (parts[6].equals("snp")) {
            return parts;
        }
        else if (parts[6].equals("sub")) {
            parts[6] = "delins";
        } else if (parts[6].equals("del")) {
            parts[8] = "-";
        } else if (parts[6].equals("ins")) {
            parts[7] = "-";
        } else {
            System.out.println("Inacceptable variant format " + parts[2] + "\t" + parts[3] + "\t" + parts[6]);
        }
        return parts;
    }
     
    
    public void openVCF(File file) throws FileNotFoundException, IOException{
        border = BorderFactory.createTitledBorder("Parsing data");
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        frame.setVisible(true);
        int counter = 0;    
        String fileName = file.getName();
        BufferedReader bReader;
        bReader = new BufferedReader(
        new FileReader(file));
        String line = bReader.readLine();
         while((line = bReader.readLine()) != null) {
                       if (line.startsWith("##", 0)) {     
                  
                        continue;
             
                     }
          else if (line.startsWith("#", 0)) {
                        String l = line.replace("#", "");
       
                        
                        continue; 
                    } else {
                        counter ++;
                         
                        if ((counter%10000) == 0){     
                            // Loading.frame.dispose();
                           //  System.out.print("Parsing VCF file" + counter + "\t");
                             DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                             Calendar cal = Calendar.getInstance();
                             System.out.println(dateFormat.format(cal.getTime()));
                             percentage = percentage + 1.0;
                             int b = (int) percentage;  
                             progressBar.setValue(b);
                             progressBar.setStringPainted(true);
             }
                        DataStructure ob1 = new DataStructure(line);
                       
        }
    }
         System.out.println("Number of variants in first chromosome is: " + DataStructure.datacount);
         //sort the parsed file for future reference
         border = BorderFactory.createTitledBorder("Sorting file");
         progressBar.setBorder(border);
         content.add(progressBar, BorderLayout.NORTH);
         progressBar.setValue(80);
         progressBar.setStringPainted(true);
         sortFile("parsed.txt");
         //add the snp132Flagged.vars to the end of the file
         border = BorderFactory.createTitledBorder("Adding clinically associated variants");
         progressBar.setBorder(border);
         content.add(progressBar, BorderLayout.NORTH);
         progressBar.setValue(85);
         progressBar.setStringPainted(true);
         concat();
         // sorting the PipelineInput.txt by chromosome, begin, end
         JavaRunCommand();   
         // delete variants if they are already found in the input file
         deleteDoubles();
         border = BorderFactory.createTitledBorder("Remove duplicated variants");
         progressBar.setBorder(border);
         content.add(progressBar, BorderLayout.NORTH);
         progressBar.setValue(90);
         progressBar.setStringPainted(true);
         //delete the unused files, keep the parsed file
         cleanUp();
         border = BorderFactory.createTitledBorder("Clean up");
         progressBar.setBorder(border);
         content.add(progressBar, BorderLayout.NORTH);
         progressBar.setValue(90);
         progressBar.setStringPainted(true);
         //sort the parsed file for future reference
         sortFile("parsed.txt");
         //change file name for the user to be easier to identify
         String filename = file.getName();
         String f = filename.concat(".").concat("Identified");
         Runtime.getRuntime().exec("mv parsed_sorted.txt " + f);
         Runtime.getRuntime().exec("rm parsed.txt");
         frame.removeAll();
                JFrame frame =  new JFrame();
                frame.setContentPane(new JPanel());
               //Display the window.
               frame.setSize(750, 160);
               frame.setBackground(Color.white);
               frame.setVisible(true); 
               frame.setTitle("Read Me!");
               javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();
               jTextArea1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
               String ident = fileName.concat(".Identified"); 
               jTextArea1.setText("    \n                Deidentification successful! \n\n    Please proceed to genomics.scripps.edu and upload the file: \n    " + newNameGz + 
               "\n    Note: don't delete the file " + ident + ", you will need this file for \n    Identification once you have SG ADVISER annotation results.");
               jTextArea1.setEditable(false);
               frame.add(jTextArea1);
               frame.setVisible(true); 
         
         
  
    }


//concatenate    
    
public void concat() throws IOException{
        InputStream in = null; 
        try {
            File f1 = new File("parsed.txt"); 
            File f2 = new File("snp132Flagged.vars");
            File f3 = new File("Combined.txt");
            in = new FileInputStream(f1);
            //For Append the file.  
            OutputStream out = new FileOutputStream(f3,true);  
  
            byte[] buf = new byte[1024];  
            int len;  
            while ((len = in.read(buf)) > 0){  
                out.write(buf, 0, len);  
            }  
            in.close();  
            out.close();  
            in = new FileInputStream(f2);
            //For Append the file.  
            OutputStream out2 = new FileOutputStream(f3,true);  
  
            while ((len = in.read(buf)) > 0){  
                out2.write(buf, 0, len);  
            }  
            in.close();  
            out2.close();  
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Deidentifier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Deidentifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    

public static void sortFile (String file) {
            String s = null;

        try {
            
            File sorted = new File("parsed_sorted.txt");
            //old version to sort by chromosome, begin, end when we had haplotype in the first position
            //Process process = Runtime.getRuntime().exec("sort -k2,2 -k3n,3 -k4n,4 PipelineInput.txt");
            Process process = Runtime.getRuntime().exec("sort -k1,1 -k2n,2 -k3n,3 "+ file);
            InputStream in = process.getInputStream();
            OutputStream out = new FileOutputStream(sorted);
            byte[] buf = new byte[1024];  
            int len;  
            while ((len = in.read(buf)) > 0){  
                out.write(buf, 0, len);  
            }  
            in.close();  
            out.close();  
            System.out.println("Parsed file sorted succesfully");
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
}

public static void JavaRunCommand() {

        String s = null;

        try {
            
            File sorted = new File("combined_sorted.txt");
            //old version to sort by chromosome, begin, end when we had haplotype in the first position
            //Process process = Runtime.getRuntime().exec("sort -k2,2 -k3n,3 -k4n,4 PipelineInput.txt");
            Process process = Runtime.getRuntime().exec("sort -k1,1 -k2n,2 -k3n,3 Combined.txt");
            InputStream in = process.getInputStream();
            OutputStream out = new FileOutputStream(sorted);
            byte[] buf = new byte[1024];  
            int len;  
            while ((len = in.read(buf)) > 0){  
                out.write(buf, 0, len);  
            }  
            in.close();  
            out.close();  
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
 

public static void deleteDoubles() throws FileNotFoundException, IOException {
         String previousLine = "";
        int duplicatedVariants = 0;
        File sorted  = new File("combined_sorted.txt");
        BufferedReader bReader;
        bReader = new BufferedReader(
        new FileReader(sorted));
        String line;
        String newName = fileName.concat(".Deidentified");
        BufferedWriter bw = new BufferedWriter(new FileWriter(newName));  
        while((line = bReader.readLine()) != null) {
       
            String newLine = "";

                /*
                 * Need to get rid of the haplotype but we don't know how long it is if multiple 
                 * genomes in file, also we need to get rid of the last tab
                 */
                String[] lin = line.split("\t");
                newLine = lin[0];
                
                for (int i=1; i<6; i++) {
                //    System.out.println(lin[i]);
                    newLine = newLine.concat("\t").concat(lin[i]);
                    
                }

            //check to see if this is the first line
            if (previousLine.equals("")) {
                previousLine = newLine;
                /* this is for now so it can go through the pipeline, I need to 
                **add first and last line, I will have to delete this step once validator is fixed
                */
                String tempLine = "-".concat("\t").concat(newLine).concat("\t").concat("-");
                bw.write(tempLine);
                bw.newLine();
                continue;
            } 
            
            
            //is this line a douple, don't copy it and skip to the next line
            else if (previousLine.equals(newLine)) {
                 duplicatedVariants++;
                continue;
                
            } else {
               /* 
                * this is for now so it can go through the pipeline, I need to 
                **add first and last column, I will have to delete this step once validator is fixed
                */
                String tempLine = "-".concat("\t").concat(newLine).concat("\t").concat("-");
                bw.write(tempLine);
                bw.newLine();
                previousLine = newLine;
            }
                       
        }
        
        bw.close();
        //newNameGz = newName;
        //compress file
       newNameGz = newName.concat(".tar.gz");

       String command = "tar -zcvf ".concat(newNameGz).concat(" ").concat(newName);
       Runtime.getRuntime().exec(command);
        
        
        System.out.println("Duplicated variants removed succesfully!");
        System.out.println("Number of duplicated variants: " + duplicatedVariants);
        System.out.println("Deidentification successful!  Please go to http://genomics.scripps.edu and upload the file Deidentified.tar.gz for annotation! Attn: do not delete the IdentifiedFile folder, you will need it once you have the annotated genome and you have to identify it!");
}

public static void cleanUp() throws IOException {
    
       Runtime.getRuntime().exec("rm combined_sorted.txt");
       Runtime.getRuntime().exec("rm Deidentified.txt");
       Runtime.getRuntime().exec("rm Combined.txt");
        
  //      Runtime.getRuntime().exec("mkdir IdentifiedFile");
   //     Runtime.getRuntime().exec("cp parse.txt IdentifiedFile/");
    //    Runtime.getRuntime().exec("rm parse.txt");
        
}

    @Override
    public void run() {
     
            final String orgName = Thread.currentThread().getName();
            Thread.currentThread().setName(orgName + "firstThread");
            try {
                   percentage=0;
            try {
                Validator();
            //   inputData(arrayOfLines, dataLines, selection, headers);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Deidentifier.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Deidentifier.class.getName()).log(Level.SEVERE, null, ex);
            }
            } finally {
                Thread.currentThread().setName(orgName);
            }
        }
}
 