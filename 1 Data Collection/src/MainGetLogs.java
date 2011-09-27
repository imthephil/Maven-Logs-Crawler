
public class MainGetLogs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		char[] alphabet = new char[26+10];
		for (char letter='0'; letter <= '9'; letter++)alphabet[letter-'0']= letter;
		for (char letter='a'; letter <= 'z'; letter++)alphabet[letter-'a'+10]= letter;

		//for (char letter='a'; letter <= 'z'; letter++)alphabet[letter-'a']= letter;

		for (char added : alphabet){
			GetLogs pm = new GetLogs(""+added);
			Thread t = new Thread(pm);
			t.start();
		}
	/*
		GetLogs pm = new GetLogs("ab");
		Thread t = new Thread(pm);
		t.start();
	*/
	}

}
