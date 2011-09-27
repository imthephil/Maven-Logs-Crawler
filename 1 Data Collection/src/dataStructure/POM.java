package dataStructure;


import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class POM {
	private String url; 
	private String version; 
	private String artifactID; 
	private String repositoryID;
	private String groupID; 
	private SCM scm;
	private ArrayList<String> dependenciesPairs;
	//private String localName; 

	public POM(String artifactID,String version, String groupID,  String repositoryID){
		this.groupID = groupID; 
		this.repositoryID = repositoryID; 	
		this.artifactID = artifactID;
		this.version = version;
		this.dependenciesPairs = new ArrayList<String>();
		this.url = "https://repository.sonatype.org/service/local/artifact/maven/redirect";
		this.url += "?r="+this.repositoryID;//Repo ID
		this.url += "&g=" + this.groupID; //Group ID
		this.url += "&a="+ this.artifactID; //Artifact ID 
		this.url += "&v=" + this.version; 
		this.url += "&e="+"pom";

	}

	public POM(String line) {
		String[] be = line.split(",");
		this.artifactID = be[0]; 
		this.version = be[1]; 
		this.groupID = be[2];
		this.repositoryID = be[3];
		this.url = be[4];
		this.scm = new SCM(be[5],this);
	}

public void fillUpFromLink() {
	try{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new URL(this.url).openStream());
		/*SCM*/
		Element scm = (Element) doc.getElementsByTagName("scm").item(0);
		if(scm != null){
			String scmConnection = scm.getElementsByTagName("connection").item(0).getTextContent();
			String scmURL = scm.getElementsByTagName("url").item(0).getTextContent();
			scmConnection = scmConnection.replaceAll(" ", "").replaceAll("\n","").replaceAll("\t", "");				
			scmURL = scmURL.replaceAll(" ", "").replaceAll("\n","").replaceAll("\t", "");

			if(!(scmConnection ==null && scmURL==null)){
				this.scm = new SCM(scmConnection,scmURL,this);
			}
		}
		/*Dependencies*/
		
		
		NodeList dependencies = doc.getElementsByTagName("dependency");
		int nbdep =dependencies.getLength();
		for(int i =0; i<nbdep;i++){
			Node poms = dependencies.item(i);
			Element pomsE = (Element) poms; 
			String artID = pomsE.getElementsByTagName("artifactId").item(0).getTextContent();
			String grID = pomsE.getElementsByTagName("groupId").item(0).getTextContent();
			this.dependenciesPairs.add(this.groupID+"_"+this.artifactID+"_"+this.version+","+grID+"_"+artID);
		}		
	}
	catch(Exception e){
		//e.printStackTrace();
		e.getLocalizedMessage();
	}
}
public String write(){
	String s ="";
	s+= this.artifactID+",";
	s+= this.version+",";
	s+= this.groupID+",";
	s+= this.repositoryID+",";
	s+= this.url+",";
	if(this.scm != null)s+= this.scm.write()+",";
	else s+=" ,";
	return s+"\r\n"; 
}

public String hash() {
	return this.artifactID+"-"+this.version;
}
public int downloadSCM(){
	return this.scm.DownloadLogs();
}

public String getArtifactID() {
	return artifactID;
}

public void setArtifactID(String artifactID) {
	this.artifactID = artifactID;
}

public ArrayList<String> getDependenciesPairs() {
	return dependenciesPairs;
}

public String getUrl() {
	return url;
}

public String getVersion() {
	return version;
}

public String getRepositoryID() {
	return repositoryID;
}

public String getGroupID() {
	return groupID;
}

public String getScmType() {
	if(this.scm ==null) return "No SCM For this package";
	else return scm.getType();
}

public String getScmUrl(){
	if(this.scm ==null) return "No SCM For this package";
	else return scm.getUrl(); 
}
public String getScmConnection(){
	if(this.scm ==null) return "No SCM For this package";
	else return scm.getConnection(); 
}

}
