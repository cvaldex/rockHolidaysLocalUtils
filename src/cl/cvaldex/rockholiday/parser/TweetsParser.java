package cl.cvaldex.rockholiday.parser;

import com.google.api.services.sheets.v4.model.*;

import cl.cvaldex.rockholiday.parser.exception.TweetAPIException;
import cl.cvaldex.rockholiday.parser.googlesheets.BaseGoogleSheetsParser;
import cl.cvaldex.rockholiday.vo.TweetVO;
import com.google.api.services.sheets.v4.Sheets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TweetsParser extends BaseGoogleSheetsParser{
    private static int TEXT_INDEX = 0;
    private static int EVENT_DATE_INDEX = 2;
    private static int AUTHOR_INDEX = 3;
    private static int IMAGE_PATH_1_INDEX = 4;
    private static int IMAGE_PATH_2_INDEX = 5;
    private static int IMAGE_PATH_3_INDEX = 6;
    private static int IMAGE_PATH_4_INDEX = 7;
    private static int PRIORITY_INDEX = 8;
    private static int LOAD_CONTROL_INDEX = 9;
    
    private static int MAX_IMAGE_SIZE = 5 * 1000 * 1000; // maximo permitido por la API de Twitter
    
    private String imageFolder = null;

	public Collection<TweetVO> parse() throws Exception {
    	if(secretsFilePath == null || secretsFilePath.trim().length() == 0){
    		throw new Exception("secretsFilePath cannot be null or empty");
    	}

    	if(spreadsheetId == null || spreadsheetId.trim().length() == 0){
    		throw new Exception("spreadsheetId cannot be null or empty");
    	}

    	if(range == null || range.trim().length() == 0){
    		throw new Exception("Cell range cannot be null or empty");
    	}

    	Collection<TweetVO> tweets = new ArrayList<TweetVO>();
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
        	TweetVO tweet = null;

        	short loopIndex = 0;

        	for (List<?> row : values) {
        		//saltar la primera fila ya que son titulos
        		if(loopIndex == 0){
        			loopIndex++;
        			continue;
        		}
        		
        		if(! row.get(LOAD_CONTROL_INDEX).toString().equalsIgnoreCase("NO")){
        			tweet = new TweetVO();

        			tweet.setText(row.get(TEXT_INDEX).toString());
        			tweet.setDate(row.get(EVENT_DATE_INDEX).toString());
        			tweet.setAuthor(row.get(AUTHOR_INDEX).toString());
        			tweet.setPriority(Short.parseShort(row.get(PRIORITY_INDEX).toString()));

                    tweet.setImage1(getInputStream(row.get(IMAGE_PATH_1_INDEX).toString()));
                    tweet.setImage2(getInputStream(row.get(IMAGE_PATH_2_INDEX).toString()));
                    tweet.setImage3(getInputStream(row.get(IMAGE_PATH_3_INDEX).toString()));
                    tweet.setImage4(getInputStream(row.get(IMAGE_PATH_4_INDEX).toString()));

	        		tweets.add(tweet);
        		}
        	}
        }

        return tweets;
    }

    public InputStream getInputStream(String filePath) throws FileNotFoundException{
        if(filePath == null || filePath.trim().length() == 0){
        	return null;
        }
        
        String filePathSeparator = File.separator;
        
        if(! filePath.startsWith(filePathSeparator)){
        	StringBuilder builder = new StringBuilder();
        	builder.append(imageFolder);
        	
        	if(! filePath.endsWith(filePathSeparator)){
        		builder.append(filePathSeparator);
        	}
        	
        	builder.append(filePath);
        	filePath = builder.toString();
        }

        File tmpFile = new File(filePath);
        if(! tmpFile.exists()){
            throw new FileNotFoundException("File " + filePath + " doesn't exists");
        }
        
        if(tmpFile.length() > MAX_IMAGE_SIZE){
        	throw new TweetAPIException("File " + filePath + " size exceeds Twitter API limits");
        }

        return new FileInputStream(tmpFile);
    }
    
    public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}
}
