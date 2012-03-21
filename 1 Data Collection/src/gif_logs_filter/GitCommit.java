package gif_logs_filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GitCommit {
	private String author; 
	private Date date; 
	private String id; 
	private ArrayList<String>files; 
	private final SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss Z",Locale.ENGLISH);

	public GitCommit(String author, Date date, String id) {
		this.author = author;
		this.date = date;
		this.id = id;
		this.files = new ArrayList<String>(); 
	} 



	public String toString(){
		String s = ""; 

		for (int i =0; i<this.files.size();i++)
			s += this.id +","+this.author +","+this.sdf.format(this.date)+","+this.date.getTime()+","+this.files.get(i)+"\r\n";
			
		return s; 
	}

	
	public void addMyFile(String file, String fileType,int plus, int minus){
		this.files.add(fileType+","+file+","+plus +","+minus);
	}

}
