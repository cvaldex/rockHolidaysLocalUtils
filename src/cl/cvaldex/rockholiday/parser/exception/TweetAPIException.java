package cl.cvaldex.rockholiday.parser.exception;

public class TweetAPIException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TweetAPIException(String message){
		super(message);
	}
	
	public String toString(){
		return super.getMessage();
	}
}
