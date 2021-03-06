package cl.cvaldex.rockholiday.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cl.cvaldex.rockholiday.vo.TweetVO;

public class InsertTweetsDAO {
	private DataSource ds;
	private int INSERT_KEY_INDEX = 7; //en debug se descubre que la septima columna es el indice
	static final Logger logger = LogManager.getLogger(InsertTweetsDAO.class);

	public InsertTweetsDAO(DataSource ds){
		this.ds = ds;
	}

	public void insertTweets(Collection<TweetVO> tweets) throws IOException{
		PreparedStatement insertTweetPS = null;
		Connection conn = null;
		int counter = 1;

		logger.info("Elementos a insertar: " + tweets.size());
		
		try {
			conn = ds.getConnection();
			
			Iterator<TweetVO> i = tweets.iterator();
			TweetVO tmpTweet = null;
			int paramIndex = 1; 
			
			while(i.hasNext()){
				tmpTweet = i.next();

				insertTweetPS = conn.prepareStatement(assembleQuery() , Statement.RETURN_GENERATED_KEYS);
				paramIndex = 1;
				
				//setear parámetros para la Query
				insertTweetPS.setString(paramIndex++, tmpTweet.getText());
				insertTweetPS.setDate(paramIndex++, java.sql.Date.valueOf(tmpTweet.getDate()));
				insertTweetPS.setString(paramIndex++, tmpTweet.getAuthor());
				insertTweetPS.setShort(paramIndex++, tmpTweet.getPriority());

				setImage(insertTweetPS, paramIndex++ , tmpTweet.getImage1());
				setImage(insertTweetPS, paramIndex++ , tmpTweet.getImage2());
				setImage(insertTweetPS, paramIndex++ , tmpTweet.getImage3());
				setImage(insertTweetPS, paramIndex++ , tmpTweet.getImage4());
	
				insertTweetPS.execute();
				
				logger.info("Element " + counter++ + " successfully inserted. Key: " + getInsertedKey(insertTweetPS.getGeneratedKeys()) + " - " + getFormattedText(50 , tmpTweet.getText()) + "...");
			}
			
			//cerrar elementos de colección a BD
			conn.close();
			insertTweetPS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String getFormattedText(int length , String text){
		if(text.length() > length){
			return text.substring(0, length);
		}
		
		StringBuilder builder = new StringBuilder(text);
		
		while(builder.length() < length){
			builder.append(" ");
		}
		
		return builder.toString(); 
	}
	
	private String assembleQuery(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("INSERT INTO public.tweets (tweet , eventdate , author , priority, image1 , image2 , image3 , image4) ");
		builder.append("VALUES (? , ? , ? , ? , ? , ? , ? , ?) ");
		
		return builder.toString();
	}
	
	private void setImage(PreparedStatement pstmt , int index , InputStream input) throws SQLException, IOException{
		if(input != null){
			pstmt.setBinaryStream(index, input, input.available());
		}
		else{
			pstmt.setNull(index, java.sql.Types.BINARY);
		}
	}
	
	private long getInsertedKey(ResultSet keys) throws SQLException{
		long key = -1;
		
		if (keys.next()) {
			key = keys.getLong(INSERT_KEY_INDEX);
        }
        else {
            throw new SQLException("Creating tweet failed, no ID obtained.");
        }
		
		return key;
	}
}
