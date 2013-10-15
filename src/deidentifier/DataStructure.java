
package deidentifier;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author gerikson
 */
public class DataStructure {

    public static int datacount = 0;
    
    // Initialize the class constructor
    public DataStructure (String row) {

            /*
             * here we need to parse the file and transform it to the 
             */
                       datacount++; 
                       String[] inputLine = row.split("\t");
                       String ref = inputLine[3];
                       String var = inputLine[4];
                       String chrom = inputLine[0];
                       String[] g = inputLine[9].split(":");
                       String genotype = g[0];
                       //if multiple genotypes, extract all of them
                       for (int i = 10; i< inputLine.length; i++) {
                           String[] t = inputLine[9].split(":"); 
                           genotype = genotype + ":" + t[0];
                       }
                       
                       /*
                        * something to do with the offset position 0 to 1
                        */
                       int begin = Integer.parseInt(inputLine[1]) - 1;
                       if (var.contains(",")) {
                            String[] variations = var.split(",");
                            for (int i=0; i<variations.length; i++) {
                                // send each variations through again
                                //extract the begin again since it probably remembers the transformations
                                begin = Integer.parseInt(inputLine[1]) - 1;
                                vcfParse( chrom, begin, ref, variations[i], genotype);
                            }       
                        } else {
            vcfParse(chrom, begin, ref, var, genotype);
                       }
     
            if (datacount % 10000 == 0) {
            System.out.println(datacount);
            }

    }
    

       public void vcfParse(String chrom, int begin, String ref, String var, String genotype) {                 
                        boolean ignore = true;
                       int end = 0;

                       String varType = null;
                       
                    //is this a snp 
             /*       if (ref.length() == var.length() && ref.length() == 1) {
                        varType = "snp";
                      
                        end = begin + 1;
                        
                    } else if (ref.length() == var.length() && ref.length() > 1) {
                        if (ref.equals(var)) {
                            System.out.println("Reference equal to allele: " + begin);
                        } else {
                            varType = "snp";
                            int offset = 0;
                            while (ref.regionMatches(offset, var, offset, 1)) {
                                offset++;
                            }
                            ref = ref.substring(offset, offset+1);
                            var = var.substring(offset, offset+1);
                            begin = begin + offset;
                            end = begin + 1;
                            
                        }
                    }
                    
                    // is this insertion
                    else if (ref.length() < var.length() && ref.length() == 1) {
                        var = var.replaceFirst(ref, "");
                        ref = "-";
                        varType = "ins";
                        begin = begin + 1;
                        end = begin;
                       
                    }
                    else if (ref.length() < var.length() && ref.length() > 1) { 

                        var = var.replaceFirst(ref, "");
                        ref = "-";
                        varType = "ins";
                        begin = begin + 1;
                        end = begin;
                    }
                    
                    
                    //is this a deletion
                    else if (ref.length() > var.length()) {
                        int size  = ref.length() - var.length();
                        if (ref.length() > 10 && ref.regionMatches(5, "<", 0, 1)){
                            ref = ref.substring(0, 4).concat("AAA").concat(ref.substring(-5));
                            System.out.println("Crisp!!!");
                        }
                        
                        begin = begin + 1;
                        end = begin + size; 
                        ref = ref.replaceFirst(var, "");
                        var = "-";
                        varType = "del";
                    } */
               
                                    /*
                     * New parsing
                     */
                     int  start = 0;
                    for (int i=0; i < Math.min(var.length(),ref.length()); i++) {
                        if (var.charAt(i) == ref.charAt(i)) {
                            start = start+1 ;
                                    }            
                        else {
                            break;
                        }
                    }
                 //trim the head 
                    ref = ref.substring(start);
                    var = var.substring(start);
                    begin = begin + start;
                
                    
                //check the tail 
                  for (int i=1; i < Math.min(var.length(),ref.length()); i++) {
                      if (var.substring(var.length()-i-1, var.length()-i).equals(ref.substring(ref.length()-i-1,ref.length()-i))) {
                            end = end+1 ;
                                    }            
                   else {
                            break;
                        }
                  } 
                  
                    if (end != 0) {
                    ref = ref.substring(0, ref.length() - end);
                    var = var.substring(0, var.length() - end);
                    } 
                    
                    //is this a snp 
                   if (ref.length() == 1 && var.length() == 1) {
                       end = begin + 1;
                       varType = "snp";
                   } 
                   
                   else if (ref.length() == 0 && var.length() > 0) {
                       begin = begin + 1;
                       end = begin;
                       varType = "ins";
                       ref = "-";
                   }
                   
                   else if (ref.length() > 0 && var.length() == 0) {
                       varType = "del";
                       end = begin + ref.length();
                       var = "-";
                       
                   }
                   
                   else if (ref.length() > var.length() && (var.startsWith(".") || var.startsWith("-")) && !ref.contains("<")) {
                       int offset = var.length();
                       ref = ref.substring(offset);
                       begin = begin + offset;
                       end = begin + ref.length();
                       varType = "del";
                       var = "-";
                   }
                   else if (ref.length() > 0 && var.length() > 0 && !ref.equals(var)) {
                       ref = ref.replace("-", "");
                       varType = "delins";
                       end = begin + ref.length();
                   }
                   
           
                   
                         try{ 
                        BufferedWriter bw = new BufferedWriter(
                        new FileWriter("parsed.txt", true));  
                       /*
                        * depending of the vcf file version, check if chromosome starts with "chr" or not
                        */ 
                       String goodLine = null;
                           if (chrom.equalsIgnoreCase("chr1") || chrom.equalsIgnoreCase("chr2") || chrom.equalsIgnoreCase("chr3") || chrom.equalsIgnoreCase("chr4") || chrom.equalsIgnoreCase("chr5") || chrom.equalsIgnoreCase("chr6") ||
                                 chrom.equalsIgnoreCase("chr7") || chrom.equalsIgnoreCase("chr8") || chrom.equalsIgnoreCase("chr9") || chrom.equalsIgnoreCase("chr10") || chrom.equalsIgnoreCase("chr11") || chrom.equalsIgnoreCase("chr12") ||
                                   chrom.equalsIgnoreCase("chr13") || chrom.equalsIgnoreCase("chr14") || chrom.equalsIgnoreCase("chr15") || chrom.equalsIgnoreCase("chr16") || chrom.equalsIgnoreCase("chr17") || chrom.equalsIgnoreCase("chr18") ||
                                 chrom.equalsIgnoreCase("chr19") || chrom.equalsIgnoreCase("chr20") || chrom.equalsIgnoreCase("chr21") || chrom.equalsIgnoreCase("chr22") || chrom.equalsIgnoreCase("chrX") || chrom.equalsIgnoreCase("chrY")) 
                           { 

                            goodLine = chrom.concat("\t").concat(String.valueOf(begin)).concat("\t").
                            concat(String.valueOf(end)).concat("\t").concat(varType).concat("\t").concat(ref).concat("\t").concat(var).concat("\t").concat(genotype); 
                            bw.write(goodLine);
                            bw.newLine();
                            bw.close();
                       }
                    else if (chrom.equalsIgnoreCase("1") || chrom.equalsIgnoreCase("2") || chrom.equalsIgnoreCase("3") || chrom.equalsIgnoreCase("4") || chrom.equalsIgnoreCase("5") || chrom.equalsIgnoreCase("6") ||
                                 chrom.equalsIgnoreCase("7") || chrom.equalsIgnoreCase("8") || chrom.equalsIgnoreCase("9") || chrom.equalsIgnoreCase("10") || chrom.equalsIgnoreCase("11") || chrom.equalsIgnoreCase("12") ||
                                 chrom.equalsIgnoreCase("13") || chrom.equalsIgnoreCase("14") || chrom.equalsIgnoreCase("15") || chrom.equalsIgnoreCase("16") || chrom.equalsIgnoreCase("17") || chrom.equalsIgnoreCase("18") ||
                                 chrom.equalsIgnoreCase("19") || chrom.equalsIgnoreCase("20") || chrom.equalsIgnoreCase("21") || chrom.equalsIgnoreCase("22") || chrom.equalsIgnoreCase("X") || chrom.equalsIgnoreCase("Y")) 
                           { 
                            goodLine = "chr".concat(chrom).concat("\t").concat(String.valueOf(begin)).concat("\t").
                            concat(String.valueOf(end)).concat("\t").concat(varType).concat("\t").concat(ref).concat("\t").concat(var).concat("\t").concat(genotype);        
                            bw.write(goodLine);
                            bw.newLine();
                            bw.close();
                       }
                      }
                        catch(Exception e) {} 
                  
}

    

    
}
