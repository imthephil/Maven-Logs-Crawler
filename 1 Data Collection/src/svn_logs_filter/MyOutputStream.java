package svn_logs_filter;

import java.io.IOException;
import java.io.OutputStream;

public class MyOutputStream extends OutputStream {
	StringBuilder mBuf;
	public MyOutputStream() {
		mBuf = new StringBuilder();
	}

	@Override
	public void write(int arg0) throws IOException {
		mBuf.append((char) arg0);
		//if((char)arg0=='\\')System.out.println(mBuf.toString());


	}

	public String getString(){	
		return mBuf.toString();
	}
	public int[] count(){
		String st = mBuf.toString(); 
		String[]  sts = st.split("\n");
		String line; 
		int plus = 0; 
		int minus = 0; 
		for(int i=0;i<sts.length;i++){
			line = sts[i];
			if(line.startsWith("+")){
				if(!line.startsWith("+++"))plus++; 
			}
			else if(line.startsWith("-")){
				if(!line.startsWith("---"))minus++; 
			}
		}
		int[] ret = {plus,minus};
		return ret; 
	}
	
	
	public int counLines(){
		String st = mBuf.toString(); 
		return st.split("\n").length;
	}
}

