package svn_logs_filter;

public class MainSVNLogFilter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//new SVNLogFilter("http://svn.apache.org/repos/asf/maven/jxr/tags/jxr-2.3","jxr");
		
		//System.out.println(merge("https://svn.java.net/svn/wadl~svn/trunk/wadl","/trunk/wadl/pom.xml"));
		
		new SVNLogFilter("http://svn.apache.org/repos/asf/abdera/","abdera");
	}

}
	