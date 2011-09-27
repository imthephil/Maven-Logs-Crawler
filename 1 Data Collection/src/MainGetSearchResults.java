
public class MainGetSearchResults {

	public static void main(String[] args) {
		char[] alphabet = new char[26+10];
		for (char letter='0'; letter <= '9'; letter++)alphabet[letter-'0']= letter;
		for (char letter='a'; letter <= 'z'; letter++)alphabet[letter-'a'+10]= letter;

		for (char added : alphabet)	{		
			GetSearchResults sr = new GetSearchResults("a"+added, alphabet);
			Thread t = new Thread(sr);
			t.start(); 
		}

		/*
		 GetSearchResults sr = new GetSearchResults("ac", alphabet);
		Thread t = new Thread(sr);
		t.start(); 
		*/
	
	
	}
}
