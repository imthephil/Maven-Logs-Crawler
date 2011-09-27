
public class MainGetPOMs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//char[] alphabet = new char[26];
		char[] alphabet = new char[26+10];
		for (char letter='0'; letter <= '9'; letter++)alphabet[letter-'0']= letter;
		for (char letter='a'; letter <= 'z'; letter++)alphabet[letter-'a'+10]= letter;

		//for (char letter='a'; letter <= 'z'; letter++)alphabet[letter-'a']= letter;

		for (char added : alphabet){
			GetPOMs pm = new GetPOMs("a"+added);
			Thread t = new Thread(pm);
			t.start();
		}
		/*
	GetPOMs pm = new GetPOMs("ac");
	Thread t = new Thread(pm);
	t.start();
		 */
	}
}
