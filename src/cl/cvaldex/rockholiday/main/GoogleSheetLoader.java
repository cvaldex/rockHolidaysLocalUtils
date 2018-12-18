package cl.cvaldex.rockholiday.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import cl.cvaldex.rockholiday.jdbc.InsertTweetsDAO;
import cl.cvaldex.rockholiday.parser.TweetsParser;
import cl.cvaldex.rockholiday.vo.TweetVO;

public class GoogleSheetLoader {
	static final Logger logger = LogManager.getLogger(GoogleSheetLoader.class);
	
	public static void main(String[] args) throws Exception {
		if(args.length < 6){
			logger.error("Invalid args, must be 6: dbServerName dbServerPort dbName dbUserName dbPassword imageFolder");
			System.exit(1);
		}
		
		String imageFolder = args[5];
		
		TweetsParser gsParser = new TweetsParser();
		
		gsParser.setSecretsFilePath("/Users/cvaldesc/client_secret.json");
		gsParser.setSheetID("1OjTsrhmYLJIb-scLJuMCGIZwVrsTAzRwbBLFDIjEoaE");
		gsParser.setRange("Tweets!A1:I");
		gsParser.setImageFolder(imageFolder);
		
		logger.info("Reading from Google Sheets Document");
		Collection<TweetVO> tweets = gsParser.parse();
	
		String serverName = args[0];
		int serverPort = new Integer(args[1]).intValue();
		String dbName = args[2];
		String dbUserName = args[3];
		String dbPassword = args[4];
		
		BaseDataSource ds = new PGSimpleDataSource();
		
		ds.setServerName(serverName);
		ds.setPortNumber(serverPort);
		ds.setDatabaseName(dbName);
		ds.setUser(dbUserName);
		ds.setPassword(dbPassword);
		
		logger.info("Inserting into Database");
		InsertTweetsDAO dao = new InsertTweetsDAO((DataSource)ds);
		
		dao.insertTweets(tweets);
		logger.info("Process finished!");
	}
	
	public static void printTweets(Collection<TweetVO> tweets) throws IOException{
		Iterator<TweetVO> i = tweets.iterator();
		TweetVO tmpTweet = null;
		
		int index = 1;

		while(i.hasNext()){
			tmpTweet = i.next();
			
			System.out.println(tmpTweet.toString());
			
			writeFile(tmpTweet.getImage1() , (index + "_1.jpg"));
			writeFile(tmpTweet.getImage2() , (index + "_2.jpg"));
			writeFile(tmpTweet.getImage3() , (index + "_3.jpg"));
			writeFile(tmpTweet.getImage4() , (index + "_4.jpg"));
			
			index++;
		}
	}
	
	public static void writeFile(InputStream input , String fileName) throws IOException{
		byte[] buffer = new byte[input.available()];
		input.read(buffer);
		
		File targetFile = new File(System.getProperty("java.io.tmpdir")+fileName);
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		
		outStream.close();
	}
}
