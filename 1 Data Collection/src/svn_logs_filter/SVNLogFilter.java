package svn_logs_filter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import dataStructure.Log;

public class SVNLogFilter {


	private String type;
	private Calendar date;
	private String author;
	private String message;
	private long revision;
	private ArrayList<SVNLogEntry> svnlogEntries;
	private String url; 
	private String serverUrl; 
	private final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",Locale.ENGLISH);
	private ArrayList<String> commits;
	private int status;
	private SVNDiffClient svnDiffCl;
	private int urlType = 0;
	private SVNWCClient svnWcClient; 

	public SVNLogFilter(String url,String name){
		System.out.println("Getting Logs for "+ name +", at repository: "+url);

		this.url = url; 
		this.status =0; 
		boolean f = testUrl(this.url); 
		if(!f){
			System.out.println("!!!Getting Logs for "+ name +", at repository: "+url+"=>"+6);
			return; 
		}
		this.svnlogEntries = new ArrayList<SVNLogEntry>();

		SVNClientManager manager = SVNClientManager.newInstance();
		this.svnWcClient = manager.getWCClient(); 
		this.svnDiffCl = manager.getDiffClient();
		this.svnDiffCl.getDiffGenerator().setDiffAdded(true);
		this.svnDiffCl.getDiffGenerator().setDiffDeleted(true);	
		this.serverUrl = findUrl(url);
		getLogEntry();
		commits = new ArrayList<String>();

		int sc = this.svnlogEntries.size(); 
		int count = 0; 
		for(int i=0;i<sc;i++){
			getCommits(this.svnlogEntries.get(i));	
			if(100*i/sc>count*10)System.out.println(10*++count+"%");

		}

		writeCommits(name);
		System.out.println("Getting Logs for "+ name +", at repository: "+url+"=>"+this.status);

	}


	private void getLogEntry() {
		SVNRepositoryFactoryImpl.setup();
		DAVRepositoryFactory.setup();
		SVNRepository repository = null;
		try { 
			repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(this.url));	
			revision = repository.getLatestRevision();
			repository.log(null,svnlogEntries, 0, revision, true,false);
		}
		catch(Exception e){
			this.status=2;
		}
	}


	private boolean testUrl(String url2){
		SVNRepositoryFactoryImpl.setup();
		DAVRepositoryFactory.setup();
		SVNRepository repository = null; 
		try { 
			repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url2));	
		} catch (SVNException e){
			this.status = 1;
			return false; 
		}

		if(repository!=null){
			// TEST IF CONNECTION OK 
			try {
				repository.testConnection();
			} catch (SVNException e) {
				return false; 
			}
			return true;
		}
		else this.status=3;
		// GET LATEST REVISION 
		return false;
	}
	private String findUrl(String url) {
		String movingUrl = url; 

		boolean test = true; 

		while(!movingUrl.equals("")  && movingUrl.contains("/")&& test){
			if(test){
				String[] sl  = movingUrl.split("/");
				String newMovingUrl = movingUrl.replace("/"+sl[sl.length-1],"");

				test = testUrl(newMovingUrl);
				if(test){
					if(newMovingUrl.equals(movingUrl))movingUrl =""; 
					else movingUrl = newMovingUrl;

				}
			}
		}


		return movingUrl; 


	}





	public void getCommits(SVNLogEntry svnlogEntry){
		this.type = "SVN";
		this.date = Calendar.getInstance(); 
		if(svnlogEntry.getDate()!=null)this.date.setTime(svnlogEntry.getDate());
		this.author = svnlogEntry.getAuthor();
		this.message = svnlogEntry.getMessage(); 
		if(this.message!= null)this.message = this.message.replace(",",".").replace("\n", "|");
		this.revision = svnlogEntry.getRevision(); 

		Map myChangedPaths = svnlogEntry.getChangedPaths(); 

		if (myChangedPaths != null && !myChangedPaths.isEmpty()) {
			for (Iterator paths = myChangedPaths.values().iterator(); paths.hasNext();) {
				SVNLogEntryPath path = (SVNLogEntryPath) paths.next();
				getDiff(this.revision,path.getPath(),path.getType(),0);
			}
		}
		System.out.println("");

	}

	private void getDiff(long rev,String path,char type,int nbatt) {

		//http://old.nabble.com/file/p31961153/Comparision.java
		String durl =""; 


		//System.out.println(path);
		switch(urlType){
		case 0: 
			durl = this.serverUrl+path;
			break;
		case 1: 	
			try {
				SVNURL  serverUrl = SVNURL.parseURIDecoded(this.url);
				durl = serverUrl.getProtocol()+"://"+serverUrl.getHost()+path;		
			} catch (SVNException e2) {
				e2.printStackTrace();
			}
			break; 
		case 2:
			durl = mergeUrls(this.serverUrl,path);
			break; 
		}



		//	System.out.println("Type:"+this.urlType+", Url:"+durl);
		SVNURL svnurl = null;
		try {
			svnurl = SVNURL.parseURIEncoded(durl);
		} catch (SVNException e1) {
			System.out.println("Error 3:"+durl+"-"+urlType+"ser:"+this.serverUrl+"path: "+path);
		}
		int plus =0; 
		int minus =0; 

		//if(type =='M'){
		SVNRevision previous = SVNRevision.create(rev-1);
		SVNRevision current = SVNRevision.create(rev);
		OutputStream os = new MyOutputStream(); 


		try {
			if(type =='M'){
				this.svnDiffCl.doDiff(svnurl, previous, svnurl, current, SVNDepth.EMPTY, false, os);
				int[] c = ((MyOutputStream) os).count();
				plus = c[0]; 
				minus = c[1];
			}
			else if (type =='A'){	
				this.svnWcClient.doGetFileContents(svnurl,current, current, true, os);
				plus = ((MyOutputStream)os).counLines();
			}
			else if (type =='D'||type=='R'){
				this.svnWcClient.doGetFileContents(svnurl,previous, previous, true, os);
				minus = ((MyOutputStream)os).counLines();

			}
			else System.out.println("#################Type chelou ! "+type);

		} catch (SVNException e) {
			if(nbatt<2){
				this.urlType = (this.urlType+1)%3;
				getDiff(rev,path,type,nbatt+1);
				return;
			}
			else{
				//System.out.println("Error 4: "+ this.serverUrl+"-"+this.url+"-"+path);
				//e.printStackTrace();
				//System.out.println(e.getMessage());
			}
		}
		//}
		String daCl = sdf.format(new Date(this.date.getTimeInMillis()));
		if(type =='D')type ='R';
		commits.add(this.revision+","+this.author+","+daCl+","+this.date.getTimeInMillis()+","+type+","+path+","+plus+","+minus);
		System.out.print(".");
	}


	private void writeCommits(String name){
		if(commits.size()==0)return;
		try{
			FileWriter fstream = new FileWriter("data/Logs/SVNFull/"+name+".log");
			BufferedWriter out = new BufferedWriter(fstream);
			for(int i=0;i<this.commits.size();i++)
				out.write(this.commits.get(i).toString()+"\r\n");
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	public int getStatus(){
		return this.status;
	}
	private String mergeUrls(String string, String string2) {
		String[] s1 = string.split("/");
		String[] s2 = string2.split("/");
		int init =0;
		if(s2[0].isEmpty())init =1; 
		int ind = 0;
		int cut = s1.length;
		for(int i=0;i<s1.length;i++){
			int nb = init;
			if(s1[i].equals(s2[nb])){
				ind=i;
				boolean test =true; 
				for(int j=ind+1;j<s1.length;j++){

					test = test && s1[j].equals(s2[++nb]);
				}
				if(test)cut = ind;
			}
		}
		String s=""; 
		for(int i=0;i<cut;i++){
			s+=s1[i]+"/";
		}
		for(int i=init;i<s2.length;i++){
			s+=s2[i];
			if(i<s2.length-1)s+="/";
		}

		return s;
	}
}

