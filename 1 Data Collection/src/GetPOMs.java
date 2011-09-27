import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import dataStructure.POM;


public class GetPOMs implements Runnable{
	private String letters;
	private int nbart =0;
	private int nbpom=0; 

	public GetPOMs(String letters){
		this.letters = letters; 
	}
	public void run() {
		ArrayList<POM> poms = getPOMLINKS();
		FileWriter out = null;
		String s = "";
		if(poms ==null)return; 
		while(!poms.isEmpty()){
			POM p = poms.get(0);

			try{
				p.fillUpFromLink();
				System.out.print(p.write());
				s+= p.write();
				out = new FileWriter("data/POMS/"+this.letters+"_POM.csv",true);
				out.write(p.write());
				out.close();
				
				
				
				ArrayList<String>deps = p.getDependenciesPairs();
				if(deps.size()!=0){
					out = new FileWriter("data/Dependencies/"+this.letters+"_Dep.csv",true);
					for(int i=0;i<deps.size();i++)
						out.write(deps.get(i)+"\r\n");		
					out.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			poms.remove(0);
		}
	}

	public ArrayList<POM> getPOMLINKS(){
		ArrayList<POM> urlPOMS = new ArrayList<POM>();
		try {
			File fXmlFile = new File("data/SearchResults/"+letters+"_SR.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList artifacts = doc.getElementsByTagName("artifact");
			this.nbart = artifacts.getLength();
			for(int i =0; i<this.nbart;i++){
				Node poms = artifacts.item(i);
				Element pomsE = (Element) poms; 
				String artID = pomsE.getElementsByTagName("artifactId").item(0).getTextContent();
				String version = pomsE.getElementsByTagName("version").item(0).getTextContent();
				String repositoryId = pomsE.getElementsByTagName("repositoryId").item(0).getTextContent();
				String groupId = pomsE.getElementsByTagName("groupId").item(0).getTextContent();
				if(artID.startsWith(letters))// Removing artifacts not starting with corresponding letter since all with start with a letter
					urlPOMS.add(new POM(artID,version,groupId,repositoryId)); // Remove if no POM File 
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}

		System.out.println("Package starting with "+letters +" have "+nbart +" artifacts, including: " +urlPOMS.size()+" Starting with "+ letters);
		return urlPOMS;

	}



	public int getNbart() {
		return nbart;
	}
	public int getNbpom() {
		return nbpom;
	}
}
