import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common_use.IO;


public class GetSearchResults implements Runnable {

	private String letters; 
	private char[] alphabet;
	private Document data;
	
	public GetSearchResults(String letters, char[] alphabet) {
		this.letters = letters; 
		this.alphabet = alphabet;

	}

	
	public void run() {
		System.out.println("Running for letter:"+letters);
		try {
			getLetters(letters);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		IO.writeXmlFile(this.data, "data/SearchResults/"+this.letters+"_SR.xml");
		//IO.writeXmlFile(this.repos, "data/SearchResults/"+this.letters+"_Repositories.xml");
	}




	public void getLetters(String c) throws ParserConfigurationException, MalformedURLException, SAXException, IOException{
		//String url = "http://localhost:8081/nexus/service/local/lucene/search?q="+c;
		String url = "http://repository.sonatype.org/service/local/lucene/search?q="+c;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new URL(url).openStream());
		NodeList tm =doc.getElementsByTagName("tooManyResults"); 
		boolean tooMany = tm.item(0).getTextContent().equals("true");
		if(tooMany)
			for (char added : alphabet)getLetters(c+added);
		else{ 
			insertData(doc);
			System.out.println(c+": Done");
		}
	}

	private void insertData(Document doc) {
		if(data == null){
			data = doc;
			//NodeList rpd = data.getElementsByTagName("repoDetails");
			//if(rpd.getLength()!=0)data.removeChild(data.getElementsByTagName("repoDetails").item(0));
			
		}
		else{
			int docTC = new Integer(doc.getElementsByTagName("totalCount").item(0).getTextContent());
			Node xmlDC = data.getElementsByTagName("totalCount").item(0);
			int xmlTC = new Integer(xmlDC.getTextContent());
			xmlDC.setTextContent(""+ (docTC+xmlTC));
			Node xmlDATA = data.getElementsByTagName("data").item(0);
			NodeList docDATA = doc.getElementsByTagName("artifact");
			for (int i=0; i<docDATA.getLength();i++)
				xmlDATA.appendChild(data.importNode(docDATA.item(i), true));
			
		}
	}
	
}
