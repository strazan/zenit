package main.java.zenit.filesystem;

public class RunnableClass {
	
	private String path;
	private String paArguments;
	private String vmArguments;
	
	public RunnableClass(String path, String paArguments, String vmArguments) {
		this.path = path;
		this.paArguments = paArguments;
		this.vmArguments = vmArguments;
	}
	
	public String toString() {
		String nl = "\n";
		String string = path + nl + paArguments + nl + vmArguments;
		return string;
	}

}
