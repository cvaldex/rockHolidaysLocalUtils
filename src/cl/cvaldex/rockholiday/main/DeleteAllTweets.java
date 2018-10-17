package cl.cvaldex.rockholiday.main;

import java.io.IOException;
import java.text.ParseException;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import cl.cvaldex.rockholiday.twitter.TwitterManager;
import twitter4j.TwitterException;

public class DeleteAllTweets {
	static final Logger logger = LogManager.getLogger(DeleteAllTweets.class);
	/*
	 * Arguments: dbServerName dbServerPort dbName dbUserName dbPassword queryDate
	 */
	public static void main(String[] args) throws IOException, TwitterException, ParseException {
		if(args.length < 5){
			logger.error("Invalid args, must be 5: dbServerName dbServerPort dbName dbUserName dbPassword");
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
		
		logger.info("Deleting all tweets");
			
		TwitterManager twitterManager = new TwitterManager();
		twitterManager.loadConfiguration((DataSource) ds, "public.properties", "key", "value");
			
		twitterManager.deleteAllTweets();
			
		logger.info("Process finished");

	}
}
