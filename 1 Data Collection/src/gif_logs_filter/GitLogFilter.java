package gif_logs_filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GitLogFilter {
	private GitCommit current=null; 
	private String id;
	private String author;
	private final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",Locale.ENGLISH);
	private Date date;
	private ArrayList<GitCommit> commits = new ArrayList<GitCommit>() ; 
	private int minus = 0;
	private int plus = 0; 
	private String file = null;
	private String fileType ="";
	private String name; 


	public GitLogFilter(String filename,String name){
		this.name = name; 
		System.out.println("Formating "+filename);
		File f = new File(filename);
		try{
			FileReader fr = new FileReader(f);	
			BufferedReader input =  new BufferedReader(fr);

			String line = null;

			while (( line = input.readLine()) != null){
				read(line); 
			}
			read("commit bli");//To save the last instance ! 
			input.close();
			
		}
		catch(Exception e){
			e.printStackTrace(); 
		}
		writeCommits(); 

	}

	private void read(String line) {
		//System.out.println(line);	
		if(line.startsWith("commit")){


			if(this.current != null){
				//System.out.println(this.current);
				if(this.file != null)
					this.current.addMyFile(this.file,this.fileType, this.plus, this.minus);
					this.file = null;
				this.commits.add(this.current); 
			}

			this.current = null; 
			this.author = null; 
			this.id = line.split(" ")[1];
			this.date = null;


		}
		else if(line.startsWith("Author"))this.author = line.replaceAll("Author: ", "");
		else if(line.startsWith("Date: ")){
			try {
				this.date = sdf.parse(line.replace("Date:   ",""));
				this.current = new GitCommit(this.author, this.date, this.id);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		else if(line.startsWith("diff --git ")){
			if(this.file != null)
				this.current.addMyFile(this.file,this.fileType, this.plus, this.minus);
			String[] ls = line.split(" "); 
			this.file = ls[2].replace("a/",""); 
			this.minus =0; 
			this.plus = 0; 
			this.fileType="M";
		}
		else if(line.equals("+++ /dev/null")){
			this.fileType="R"; 
		}
		else if(line.equals("--- /dev/null")){
			this.fileType="A"; 
		}		
		
		else if(line.startsWith("+")){
			if(!line.startsWith("+++"))this.plus++; 
		}
		else if(line.startsWith("-")){
			if(!line.startsWith("---"))this.minus++; 
			
		}

	}
	
	public void printCommits(){
		for(int i=0;i<this.commits.size();i++)
			System.out.println(this.commits.get(i));
	}

	public void writeCommits(){
		try{
			FileWriter fstream = new FileWriter("data/Logs/Formatted/"+name+".log");
			BufferedWriter out = new BufferedWriter(fstream);
			for(int i=0;i<this.commits.size();i++)
				out.write(this.commits.get(i).toString());
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}