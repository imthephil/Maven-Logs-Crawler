package dataStructure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class Log {
	private String type; 
	private Calendar date;
	private String author; 
	private String[] files;
	private String revision; 
	private String message; 
	private final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd-HH/mm/ss", Locale.ENGLISH);


	@SuppressWarnings("rawtypes")
	public Log(SVNLogEntry svnlogEntry){
		this.type = "SVN";
		this.date = Calendar.getInstance(); 
		if(svnlogEntry.getDate()!=null)this.date.setTime(svnlogEntry.getDate());
		this.author = svnlogEntry.getAuthor();
		this.message = svnlogEntry.getMessage(); 
		if(this.message!= null)this.message = this.message.replace(",",".").replace("\n", "|");
		this.revision = ""+svnlogEntry.getRevision(); 


		Map myChangedPaths = svnlogEntry.getChangedPaths(); 
		this.files = new String[myChangedPaths.size()];
		// CODE FROM TO_STRING http://grepcode.com/file/repo1.maven.org/maven2/org.jvnet.hudson.svnkit/svnkit/1.1.4-hudson-1/org/tmatesoft/svn/core/SVNLogEntry.java
		if (myChangedPaths != null && !myChangedPaths.isEmpty()) {
			int count = 0; 
			for (Iterator paths = myChangedPaths.values().iterator(); paths.hasNext();) {
				SVNLogEntryPath path = (SVNLogEntryPath) paths.next();
				this.files[count++]= path.toString();
			}
		}
	}

public Log(String log){
	String[] sp = log.split(",");
	this.type = sp[1];
	this.date = Calendar.getInstance(); 
	try {
		this.date.setTime(DATEFORMAT.parse(sp[2]));
	} catch (ParseException e) {
		e.printStackTrace();
	}
	this.author = sp[3];
	this.message = sp[4]; 
	this.revision = sp[5];
	this.files = sp[6].split(";");

}

public Log() {
	this.files = new String[0];
}

public String write() {
	String s = ""; 
	s+= this.type +",";
	s+= DATEFORMAT.format(this.date.getTime())+",";
	s+= this.author+",";
	s+= this.message+",";
	s+= this.revision+",";
	for(int i=0; i<this.files.length;i++){
		s+= this.files[i];
		if(i!= this.files.length-1)s+=";";
	}
	return s;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public Calendar getDate() {
	return date;
}

public void setDate(Calendar date) {
	this.date = date;
}

public String getAuthor() {
	return author;
}

public void setAuthor(String author) {
	this.author = author;
}

public String[] getFiles() {
	return files;
}

public void setFiles(String[] files) {
	this.files = files;
}

public String getRevision() {
	return revision;
}

public void setRevision(String revision) {
	this.revision = revision;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

public SimpleDateFormat getDATEFORMAT() {
	return DATEFORMAT;
}





}
