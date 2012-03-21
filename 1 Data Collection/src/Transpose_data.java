import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dataStructure.Log;


public class Transpose_data {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File("dump/");
		String[] children = dir.list();
		for(int i =0; i<children.length;i++){
			String c = children[i];
			if(c.contains("_dump")){
				System.out.println(children[i]+" GO ");
				try{
					transpose(children[i]);
				}
				catch(Exception e ){
					System.out.println("--->"+children[i]+" KO");
				}
					System.out.println(children[i]+" OK ");
			}		
		}
	
	}	
		
		public static void transpose (String filename){
			
		
		
		String l1="";
		String l2=""; 
		String l3=""; 
		
		    try {

		      BufferedReader input =  new BufferedReader(new FileReader("dump/"+filename));
		      try {
		    	  l1 = input.readLine(); 
		    	  l2 = input.readLine(); 
		    	  l3 = input.readLine(); 		      }
		      finally {
		        input.close();
		      }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }

		    Writer output =null;
			try {
				output = new BufferedWriter(new FileWriter("dump_transposed/"+filename+"_tra"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    String[] dvs = l1.split(" ");
		    String[] files = l2.split(" ");
		    String[] time = l3.split(" ");
		    
		    for(int i=0;i<dvs.length;i++){
		    	try{
		    	output.write(dvs[i] +" "+ files[i]+" "+time[i] +"\r\n");
		    	}
		    	catch(Exception e){
		    		e.printStackTrace(); 
		    	}
		    }
	
		      try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   	    
		  }
	}




