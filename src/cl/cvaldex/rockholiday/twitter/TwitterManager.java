package cl.cvaldex.rockholiday.twitter;


import java.io.IOException;
import java.text.ParseException;

import javax.sql.DataSource;

import org.apache.commons.configuration2.DatabaseConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {
	private String oAuthConsumerKey;
	private String oAuthConsumerSecret;
	private String oAuthAccessToken;
	private String oAuthAccessTokenSecret;
	
	private ConfigurationBuilder configurationBuilder;
	private Configuration twitterConfiguration;

	protected static String OAUTH_CONSUMER_KEY = "twitter.api.oAuthConsumerKey";
	protected static String OAUTH_CONSUMER_SECRET = "twitter.api.oAuthConsumerSecret";
	protected static String OAUTH_ACCESS_TOKEN = "twitter.api.oAuthAccessToken";
	protected static String OAUTH_ACCESS_TOKEN_SECRET = "twitter.api.oAuthAccessTokenSecret";
	
	protected static String TWEET_PATTERN_KEY = "twitter.publishPattern";
	protected static String TWEET_MAX_LENGTH_KEY = "twitter.maxLength";
	protected static String TWEET_STORED_DATE_FORMAT_KEY = "twitter.storedDateFormat";
	protected static String TWEET_PUBLISH_DATE_FORMAT_KEY = "twitter.publishDateFormat";

	static final Logger logger = LogManager.getLogger(TwitterManager.class);

	public void deleteAllTweets() throws IOException, ParseException, TwitterException {
		if(configurationBuilder == null){
			configurationBuilder = createConfigurationBuilder();
			twitterConfiguration = configurationBuilder.build();
		}
		
		TwitterFactory tf = new TwitterFactory(twitterConfiguration);
		Twitter twitter = tf.getInstance();
		ResponseList<Status> statusList = null;
		int page = 1;
		int tweetIndex = 1;
		
		do{
			
			statusList = twitter.getUserTimeline();
			
			if(statusList.size() > 0){
				logger.info("Getting page " + page++);
			}
			else{
				logger.info("No more pages to process");
			}
		
			for(Status status : statusList){
				logger.info("Deleting " + tweetIndex++);
				twitter.destroyStatus(status.getId());
			}
			
		}while(statusList.size() > 0);
	}

	private ConfigurationBuilder createConfigurationBuilder() throws TwitterException{
		logger.info("Starting twitter configuration builder");

		if(oAuthConsumerKey == null || oAuthConsumerKey.trim().length() == 0) throw new TwitterException("Missing parameter OAuthConsumerKey");
		if(oAuthConsumerSecret == null || oAuthConsumerSecret.trim().length() == 0) throw new TwitterException("Missing parameter OAuthConsumerSecret");
		if(oAuthAccessToken == null || oAuthAccessToken.trim().length() == 0) throw new TwitterException("Missing parameter OAuthAccessToken");
		if(oAuthAccessTokenSecret == null || oAuthAccessTokenSecret.trim().length() == 0) throw new TwitterException("Missing parameter OAuthAccessTokenSecret");

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().setOAuthConsumerKey(oAuthConsumerKey)
				.setOAuthConsumerSecret(oAuthConsumerSecret)
				.setOAuthAccessToken(oAuthAccessToken)
				.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);

		return configurationBuilder;
	}

	public String getoAuthConsumerKey() {
		return oAuthConsumerKey;
	}

	public void setoAuthConsumerKey(String oAuthConsumerKey) {
		if(oAuthConsumerKey != null){
			this.oAuthConsumerKey = oAuthConsumerKey.trim();
		}
	}

	public String getoAuthConsumerSecret() {
		return oAuthConsumerSecret;
	}

	public void setoAuthConsumerSecret(String oAuthConsumerSecret) {
		if(oAuthConsumerSecret != null){
			this.oAuthConsumerSecret = oAuthConsumerSecret.trim();
		}
	}

	public String getoAuthAccessToken() {
		return oAuthAccessToken;
	}

	public void setoAuthAccessToken(String oAuthAccessToken) {
		if(oAuthAccessToken != null){
			this.oAuthAccessToken = oAuthAccessToken.trim();
		}
	}

	public String getoAuthAccessTokenSecret() {
		return oAuthAccessTokenSecret;
	}

	public void setoAuthAccessTokenSecret(String oAuthAccessTokenSecret) {
		if(oAuthAccessTokenSecret != null){
			this.oAuthAccessTokenSecret = oAuthAccessTokenSecret.trim();
		}
	}

	public void loadConfiguration(DataSource dataSource, String tableName, String keyColumn, String valueColumn){
		DatabaseConfiguration config = new DatabaseConfiguration();
		config.setDataSource(dataSource);
		config.setTable(tableName);
		config.setKeyColumn(keyColumn);
		config.setValueColumn(valueColumn);

		this.setoAuthConsumerKey(config.getString(OAUTH_CONSUMER_KEY));
		this.setoAuthConsumerSecret(config.getString(OAUTH_CONSUMER_SECRET));
		this.setoAuthAccessToken(config.getString(OAUTH_ACCESS_TOKEN));
		this.setoAuthAccessTokenSecret(config.getString(OAUTH_ACCESS_TOKEN_SECRET));
	}
}
