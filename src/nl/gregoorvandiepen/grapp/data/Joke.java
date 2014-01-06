package nl.gregoorvandiepen.grapp.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Joke {
	/*
	{
		"success":true,
		"result":[
			{
				"gid":24,
				"author":35,
				"uniqid":"0edae502408c6c13aea49acbcc3c2ebfd7fd7472",
				"title":"Een nigger die een hele kip opeet",
				"created_at":"2013-12-17T12:14:00.000Z"
			},
			{
				"gid":25,
				"author":35,
				"uniqid":"2c5179d62aeec4dd63d4d4fe0ac40c2c87695710",
				"title":"Test",
				"created_at":"2013-12-17T12:27:49.000Z"
			}
		]
	}
	*/
	
	//private variables
	int gid = 0;
	int author = 0;
	String uniqid = null;
	String title = null;
	// created_at even achterwege gelaten. Dat gedoe met datetimeparsers begin ik wel aan als ik weet waar het voor nodig is

    public Joke(	int gid,
    				int author,
    				String uniqid,
    				String title
				){
    	this.gid 	= gid;
    	this.author = author;
    	this.uniqid = uniqid;
    	this.title 	= title;
    }
    
    public Joke(JSONObject joke) throws JSONException{
    	this.gid 	= joke.getInt("gid");
    	this.author = joke.getInt("author");
    	this.uniqid = joke.getString("uniqid");
    	this.title 	= joke.getString("title");
    }
     
    public int getID(){
        return this.gid;
    }
    public String getUniqid(){
        return this.uniqid;
    }
    public int getAuthor(){
        return this.author;
    }
    public String getTitle(){
        return this.title;
    }
    public String getURL(){
    	return "http://vandiepen.org:81/grapp/node-server/grappen/"+ this.uniqid +".3gp";
    }
}
