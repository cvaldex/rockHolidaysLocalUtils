package cl.cvaldex.rockholiday.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import cl.cvaldex.rockholiday.jdbc.SelectTweetsDAO;
import cl.cvaldex.rockholiday.vo.TweetVO;

public class BackupDataBase {
	static final Logger logger = LogManager.getLogger(BackupDataBase.class);
	public String FIELD_SEPARATOR = "|";
	
	public static void main(String[] args) throws Exception {
		if(args.length < 6){
			logger.error("Invalid args, must be 6: dbServerName dbServerPort dbName dbUserName dbPassword imageFolder");
			System.exit(1);
		}
		
		String outputFolderPath = args[5];
		
		File outputFolder = new File (outputFolderPath);
		
		if(!outputFolder.exists()){
			logger.error(outputFolderPath + " doesn't exists!");
			System.exit(1);
		}
		
		if(!outputFolder.isDirectory()){
			logger.error(outputFolderPath + " isn't a directory!");
			System.exit(1);
		}
		
		if(!outputFolder.canWrite()){
			logger.error(outputFolderPath + " is read only!");
			System.exit(1);
		}
	
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
		
		logger.info("Reading from Database");
		SelectTweetsDAO dao = new SelectTweetsDAO((DataSource)ds);
		
		Collection<TweetVO> tweets = dao.getAllTweets();
		
		logger.info("Tweets to backup: " + tweets.size());
		
		outputFolderPath = outputFolderPath + "tweet_backup_" + DateFormatUtils.format(new GregorianCalendar(), "yyyyMMdd_hhmmss");
		outputFolder = new File(outputFolderPath);
		
		if(!outputFolder.mkdir()){
			logger.error(outputFolderPath + " cannot be created!");
			System.exit(1);
		}
		
		writeTweets(tweets , outputFolderPath);
		
		logger.info("Creating output folder: " + tweets.size());
		
		
		
		logger.info("Process finished!");
	}
	
	public static void writeTweets(Collection<TweetVO> tweets, String outputFolder) throws IOException{
		//Iterator<TweetVO> i = tweets.iterator();
		//TweetVO tmpTweet = null;
		
		int index = 1;

		//while(i.hasNext()){
		for(TweetVO tweet : tweets){
			//tmpTweet = i.next();
			
			System.out.println(tweet.toString());
			
			writeFile(tweet.getImage1() , (index + "_1.jpg"));
			writeFile(tweet.getImage2() , (index + "_2.jpg"));
			writeFile(tweet.getImage3() , (index + "_3.jpg"));
			writeFile(tweet.getImage4() , (index + "_4.jpg"));
			
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
