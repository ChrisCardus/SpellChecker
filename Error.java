public class Error {

	private String error;
	private int lineNumber;
	
	public Error(String error, int lineNumber) {
		this.error = error;
		this.lineNumber = lineNumber;
	}
	
	public String getError() {
		return error;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
}