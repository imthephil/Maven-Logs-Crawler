package dataStructure;

import gif_logs_filter.GitLogFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import svn_logs_filter.SVNLogFilter;



public class SCM {
	private String connection; 
	private String url;
	private POM pom;
	private final int TIMEOUT_S = 120;
	public SCM(String connection, String url,POM pom) {
		this.connection = connection;
		this.url = url;
		this.pom = pom;
	} 

	public SCM(String string,POM pom) {
		this.pom = pom;
		if(string.equals(" "))return; 
		else{
			try{
			string = string.replaceAll("SCM\\[", "").replaceAll("\\]", "");
			String[] be = string.split(";");
			this.connection = be[0];
			this.url = be[1];
			}
			catch(Exception e){
				System.out.println("Issue with scm creation from: "+string);
			}
		}
	}

	public String write(){
		String s ="";
		s+= "SCM[";
		s+= this.connection+ ";";
		s+= this.url;
		return s+"]";
	}

	public int DownloadLogs(){
		
		if(this.connection != null){
			int state =1;
			String type = this.getType();
			
			if(type.equals("svn")) state = NewDownloadSVN(); 
			//else if(type.equals("cvs"))state = DownloadCVS(); 
			//if(type.equals("git"))state =DownloadGIT(); 
			//else if(type.equals("hg"))state = DownloadHG();
			else state = 1;
			//System.out.println(state + " ON Type:"+type+" - "+this.url +" -> " +this.connection);
			return state; 	
		
		}
		else return 7; 
		
		
	}


	private int NewDownloadSVN() {
		String add = this.connection; 
		add = add.replace("scm:svn:", "");
		if(this.pom !=null) add  = add.replaceAll("$\\{artifactId\\}", this.pom.getArtifactID()); 
		//System.out.println(add);
		// MANAGEMENT OF SSH
		if(this.url.startsWith("svn+ssh")){
			return 1; 
		}
		SVNLogFilter slf = new SVNLogFilter(add,this.pom.hash().replace(".", "-"));
		return slf.getStatus();
	}

	
	


	public static boolean isAlive( Process p ) {
	    try
	    {
	        p.exitValue();
	        return false;
	    } catch (IllegalThreadStateException e) {
	        return true;
	    }
	}
	

	private int DownloadGIT() {
		String connection=this.url.replace("scm:git:", "");
		connection = connection.replace(":","/");
		connection = connection.replace("http///","http://");
		connection = connection.replace("https///","https://");
		connection = connection.replace("git///","git://");	
		connection = connection.replace("@","://");
		String cmd = "./get_git_log.sh "+connection +" "+ "data/Logs/GIT/"+this.pom.hash()+".log "+this.pom.hash();
		//System.out.println(cmd);

		try {
			Runtime runtime  = Runtime.getRuntime();
			Process process = runtime.exec(cmd);
			// TIMEOUT IMPLEMENTATION
			int exitVal = 8;
			long now = System.currentTimeMillis();
		    long timeoutInMillis = 1000L * TIMEOUT_S;
		    long finish = now + timeoutInMillis;
		    while ( isAlive( process ) && ( System.currentTimeMillis() < finish ) )
		    {
		        Thread.sleep( 100 );
		    }
		    if ( isAlive( process ) )
		    {
		    	exitVal = 8; 
		    	throw new InterruptedException( "Process timeout out after " + TIMEOUT_S + " seconds" );
		    
		    }
		    exitVal = process.exitValue();
		    
			if(exitVal==0){
				newformatGITLog(this.pom.hash()+".log");
				return 0;
			}
			else return exitVal;
			
		} catch (Exception e) {
			//System.out.println(e.getMessage());
			return 8;
			
		}
	
	
	}

	

	private int DownloadCVS() {
		String connection=this.connection.replace("scm:cvs", "");
		int ind = connection.lastIndexOf(":");
		String pack = connection.substring(ind); 
		String server = connection.substring(0, ind);
		
		
		String cmd = "./get_cvs_log.sh "+server +" "+ "data/Logs/CVS/"+this.pom.hash()+".log "+pack;
		System.out.println(cmd);

		try {
			Runtime runtime  = Runtime.getRuntime();
			Process process = runtime.exec(cmd);
			int exitVal = 8;
			long now = System.currentTimeMillis();
		    long timeoutInMillis = 1000L * TIMEOUT_S;
		    long finish = now + timeoutInMillis;
		    while ( isAlive( process ) && ( System.currentTimeMillis() < finish ) )
		    {
		        Thread.sleep( 100 );
		    }
		    if ( isAlive( process ) )
		    {
		    	exitVal = 8; 
		    	throw new InterruptedException( "Process timeout out after " + TIMEOUT_S + " seconds" );
		    
		    }
		    exitVal = process.exitValue();
		    
			if(exitVal==0){
				if(formatCVSLog(this.pom.hash()+".log")) return 0;
				else return 9;
			}
			else return exitVal;
			
		} catch (Exception e) {
			//System.out.println(e.getMessage());
			return 8;
			
		}
	
		
	}

	private int DownloadSVN() {
		// 0 OK 
		// 1 Not supported protocol 
		// 2 Error Connecting to repository
		// 3 Test Failed
		// 4 Issue get revision
		// 5 Issue Getting logs
		// 6 Issue Writing File
		//
		
		// REPLACE SOME {}
		String add = this.connection; 
		add = add.replace("scm:svn:", "");
		if(this.pom !=null) add  = add.replaceAll("$\\{artifactId\\}", this.pom.getArtifactID()); 
		//System.out.println(add);
		// MANAGEMENT OF SSH
		if(this.url.startsWith("svn+ssh")){
			return 1; 
		}
		//System.out.println("svn: " + add);
		// FIRST WE CONNECT TO REPOSITORY
		SVNRepositoryFactoryImpl.setup();
		DAVRepositoryFactory.setup();
		SVNRepository repository = null; 
		try { 
			repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(add));	
		} catch (SVNException e){
			return 2; 
		}

		
		// TEST IF CONNECTION OK 
		try {
			repository.testConnection();
		} catch (SVNException e) {
			return 3; 
		}
		
		// GET LATEST REVISION 
		long revision =0; 
		try {
			 revision = repository.getLatestRevision();
		} catch (SVNException e1) {
			return 4;
		}

		// GET LOGS
		ArrayList<SVNLogEntry> les = new ArrayList<SVNLogEntry>();
		try {
			repository.log(null,les, 0, revision, true,false);
		} catch (SVNException e) {
			return 5;
		} 
		
		// WRITE LOGS
		
		FileWriter out = null;
		try {
			out = new FileWriter("data/Logs/Formatted/"+this.pom.hash()+".log",true);
			for( int i =0;i<les.size();i++){
				Log log = new Log(les.get(i));
				out.write(log.write()+"\r\n");
			}
			out.close(); 
		
				
		} catch (IOException e1) {
			e1.printStackTrace();
			return 6;
		}
		return 0;
	}
	
	

	public String getType(){
		if(this.connection == null) return "No SCM For this Packacge"; 
		int start = this.connection.indexOf(":");
		if(start>0){
			int end = this.connection.indexOf(":",start+1);
			if(end>start)return this.connection.substring(start+1, end);
			else return ""; 
		}
		else return "";
	}

	public POM getPom() {
		return pom;
	}

	public String getUrl() {
		return this.url;
	}
	public String getConnection(){
		return this.connection; 
	}
	
	
	private boolean formatHGLog(String filename){
	
		SimpleDateFormat DATEFORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",Locale.ENGLISH);
		File aFile = new File("data/Logs/HG/"+filename);
		FileWriter out = null;
	
	
	
		try {
			out = new FileWriter("data/Logs/Formatted/"+filename,true);
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				Log log = null; 
				String message = ""; 
				while (( line = input.readLine()) != null){
					if (!line.startsWith("changeset")){
						if(!line.contentEquals(""))message += line.replace("summary: ","")+"|"; 
					}
					else{
						if(log!= null){
							log.setMessage(message);
							out.write(log.write()+"\r\n");
						}
	
						log = new Log();
	
						message ="";
						log.setType("HG");
						log.setRevision(line.split("   ")[1].split(":")[0]);
						line = input.readLine();
						while(!line.startsWith("user:") && !line.startsWith("changeset") &&line!=null){
							line = input.readLine();
						}
						
						if(line.startsWith("user:")){
							log.setAuthor(line.replace("user:        ", ""));
							line = input.readLine(); 
							if(line.startsWith("date: ")){ 
								Calendar c = Calendar.getInstance();
								c.setTime(DATEFORMAT.parse(line.replace("date:        ", "")));
								log.setDate(c);
							}
	
							else{
								System.out.println("error syntax");
								return false; 
							}
						}
						else{
							System.out.println("error syntax");
							return false;
						}
					}
				}
				return true; 
			}
			finally {
				input.close();
				out.close(); 
	
			}
		}
		catch (Exception ex){
			//ex.printStackTrace();
			return false;
		}
	
	
	}

	
	
	private void newformatGITLog(String filename) {
		new GitLogFilter("data/Logs/GIT/"+filename,this.pom.hash().replace(".", "-"));
	}
	private boolean formatGITLog(String filename){

		SimpleDateFormat DATEFORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",Locale.ENGLISH);
		File aFile = new File("data/Logs/GIT/"+filename);
		FileWriter out = null;



		try {
			out = new FileWriter("data/Logs/Formatted/"+filename,true);
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				Log log = null; 
				String message = ""; 
				while (( line = input.readLine()) != null){

					if (!line.startsWith("commit")){
						if(!line.contentEquals(""))
							message += line+"|"; 
					}
					else{
						if(log!= null){
							log.setMessage(message);
							out.write(log.write()+"\r\n");
						}
						log = new Log();
						message ="";
						log.setType("GIT");
						log.setRevision(line.split(" ")[1]);
						line = input.readLine();
						while(!line.startsWith("Author:") && !line.startsWith("commit") &&line!=null)
							line = input.readLine();
						if(line.startsWith("Author:")){
							log.setAuthor(line.replace("Author: ", ""));
							line = input.readLine(); 
							if(line.startsWith("Date: ")){ 
								Calendar c = Calendar.getInstance();
								c.setTime(DATEFORMAT.parse(line.replace("Date:   ", "")));
								log.setDate(c);

							}
							else{
								System.out.println("error syntax");
								return false; 

							}
						}
						else{
							System.out.println("error syntax");
							return false;

						}
					}
				}
				return true; 
			}
			finally {
				input.close();
				out.close(); 

			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			return true;
		}


	}
	
	

	private static boolean formatCVSLog(String filename){

		SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.ENGLISH);
		File aFile = new File("data/Logs/CVS/"+filename);
		FileWriter out = null;


		try {
			out = new FileWriter("data/Logs/Formatted/"+filename,true);
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				Log log = null; 
				while (( line = input.readLine()) != null){
					if(line.startsWith("----------")){
						line = input.readLine(); 
						if(line.startsWith("revision ")){
							log = new Log();
							log.setType("CVS");
							log.setRevision(line.split(" ")[1]);
							line = input.readLine(); 
							if(line.startsWith("date: ")){
								String[] vals = line.split(";");
								Calendar c = Calendar.getInstance();
								c.setTime(DATEFORMAT.parse(	vals[0].replace("date:", "")));
								log.setDate(c);
								log.setAuthor(vals[1].replace("  author: ", ""));
								line = input.readLine(); 
								log.setMessage(line);
								out.write(log.write());
								System.out.println(log.write());
							}
						}
					}
				}
				return true; 


			}
			finally {
				input.close();
				out.close(); 

			}
		}
		catch (Exception ex){
			//ex.printStackTrace();
			return false;
		}


	}

}
