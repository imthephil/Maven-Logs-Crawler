import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import dataStructure.POM;


public class GetLogs implements Runnable{
	private String letters; 
	private final  int MAXERRORCODE = 9;
	FileWriter[] writers= new FileWriter[MAXERRORCODE+1];
	
	public GetLogs(String letters) {
		this.letters = letters;
		
		
		
	}

	public void run() {
		openWriters();
		DataInputStream in = null; 
		BufferedReader br = null; 
		HashSet<String> doneScms = new HashSet<String>();
		try{
		FileInputStream fstream = new FileInputStream("data/POMS/"+this.letters+"_POM.csv");
		  // Get the object of DataInputStream
		  in = new DataInputStream(fstream);
		  br = new BufferedReader(new InputStreamReader(in));
		  String strLine;
		  while ((strLine = br.readLine()) != null)   {
			  POM p = new POM(strLine); 
			  if(!doneScms.contains(p.getScmConnection())){
				  int state = p.downloadSCM();
				  writers[state].write(p.getArtifactID()+","+p.getScmType()+","+ p.getScmUrl()+","+p.getScmConnection()+"\r\n"); 
				  doneScms.add(p.getScmConnection());
			  }
		  }
		  //Close the input stream
		
		
		    }catch (FileNotFoundException fne){//Catch exception if any
		    }catch (Exception e){
		    	e.printStackTrace(); 
		    }finally{
		    	try{
		    	if(br != null)  br.close(); 
				if(in != null)  in.close();
		    	}catch (Exception e){
		    		e.printStackTrace(); 
		    	}
		    }
		    closeWriters();
		    
	}
	public void closeWriters(){
		for(int i = 0;i<=MAXERRORCODE;i++){
			try {
				writers[i].close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void openWriters(){
		for(int i =0; i<=MAXERRORCODE;i++){
			writers[i] = null;
			try {
				writers[i] = new FileWriter("data/Logs/Errors/"+i+".log",true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
