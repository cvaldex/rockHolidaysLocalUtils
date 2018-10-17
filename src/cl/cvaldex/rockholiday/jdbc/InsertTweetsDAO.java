package cl.cvaldex.rockholiday.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import cl.cvaldex.rockholiday.vo.TweetVO;

public class InsertTweetsDAO {
	private DataSource ds;

	public InsertTweetsDAO(DataSource ds){
		this.ds = ds;
	}

	public void insertTweets(Collection<TweetVO> tweets) throws IOException{
		PreparedStatement insertTweetPS = null;
		Connection conn = null;

		System.out.println("Elementos a insertar: " + tweets.size());
		
		try {
			conn = ds.getConnection();
			
			Iterator<TweetVO> i = tweets.iterator();
			TweetVO tmpTweet = null;
			
			while(i.hasNext()){
				tmpTweet = i.next();
				insertTweetPS = conn.prepareStatement(assembleQuery());

				//setear parámetros para la Query
				insertTweetPS.setString(1, tmpTweet.getText());
				insertTweetPS.setDate(2, java.sql.Date.valueOf(tmpTweet.getDate()));
				insertTweetPS.setString(3, tmpTweet.getAuthor());

				setImage(insertTweetPS, 4 , tmpTweet.getImage1());
				setImage(insertTweetPS, 5 , tmpTweet.getImage2());
				setImage(insertTweetPS, 6 , tmpTweet.getImage3());
				setImage(insertTweetPS, 7 , tmpTweet.getImage4());
	
				insertTweetPS.execute();
			}
			
			//cerrar elementos de colección a BD
			conn.close();
			insertTweetPS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String assembleQuery(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("INSERT INTO public.tweets (tweet , eventdate , author , image1 , image2 , image3 , image4) ");
		builder.append("VALUES (? , ? , ? , ? , ? , ? , ?) ");
		
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
}
