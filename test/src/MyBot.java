/**
 * @author Kartik Singhal
 * NetID: KXS180077
 * UTD-ID: 2021469364
 *
 */
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

import org.jibble.pircbot.PircBot;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MyBot extends PircBot {
    
    public MyBot() {
        this.setName("scottbot");
        setAutoNickChange(true);
    }
    
    // Storing authentication keys and endpoints
    private static final String OWMkey = "0868e7b5a590e110be6fd6ce01da36c1";
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather?units=imperial&";
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&";
    private static final String YT_KEY = "AIzaSyCTwbZCY5Y6bfAUbkLVScsOTssVKgscsbs";
    
    // in order : ConsumerKey, ConsumerSecret, AccessToken, AccessTokenSecret
    private static final String[] TWITTER_KEYS = {"gbUCR8DLRnegSBmQJGIqe4xMX",
    		"WGJYnyH0qoW7zFjU4DRiJjysPY6gmFaCtDKXpWLDzyZU7yxfKO",
    		"4034277194-fetHrzqLiEs0VTB58p56Zs8kbEsE9Q9JyU7vql2",
    		"0dM7UI3jmrH2Ue5A2RKmoLctjRSGakMO9eoNy13ovqgMn"};
    
    private static final String TIME_KEY = "LewtSPCKBk632iNzAgz3AhBYGnVhAi";
 
    boolean FLAG = true, FLAG1 = true;
    // A counter to count number of msgs sent by user(s)
    int msgCount = 1, tempCount = 0;
    
    // A bool value to enable confirmation for disconnecting
    Boolean check = true;
    
    // This method is used to send a message when a new user joins the channel
    protected void onJoin(String channel, String sender, String login, String hostname) {
    	if(!sender.equalsIgnoreCase(this.getName()))
    		sendMessage(channel,  sender + ", welcome to channel " + channel);
    	else
    		sendMessage(channel, "Welcome to channel " + channel);
    		sendMessage(channel, "Here you can get temperature of a city, the current time for you, "
    				+ "the current top 10 trending tags on twitter, search for a youtube video by name,"
    				+ " get latest stats on the COVID-19 outbreak, get synonyms, definitions, full-forms of abbreviations, "
    				+ " convert from one unit to another, and get information about works of literature");
    		sendMessage(channel, "Type 'commands' to get a full list of my current capabilities");
    }
    public void onMessage(String channel, String sender,
                       String login, String hostname, String message){
    	msgCount++;
    	FLAG = true;
    	FLAG1 = true;
    	if(message.equalsIgnoreCase("commands")) {
    		
    		sendMessage(channel,"Include the keyword \"weather\" and city name(s) or just a zip code to get current temperature of that city.");
    		sendMessage(channel,"Include the keyword \"twitter\" to get the trending tags.");
    		sendMessage(channel,"Include the keyword \"time\" to get your current time.");
    		sendMessage(channel,"Include the keyword \"youtube\" and your query to get the link to the first video found on youtube using that keyword");
    		sendMessage(channel,"Include the keyword \"corona\" to get the latest updates on the outbreak");
    		sendMessage(channel,"Include the keyword \"abbreviation\" and an abbreviation to get its most common full form.");
    		sendMessage(channel,"Include the keyword \"define\" and your query to get its definition.");
    		sendMessage(channel,"Include the keyword \"literature\" and your query to get information about that literature work.");
    		sendMessage(channel,"Include the keyword \"convert\" and your query, ex. 'convert 1 mile to kilometers'");
    		sendMessage(channel,"Include the keyword \"synonym\" and a word to get its synonyms (and definition).");
    		
    		sendMessage(channel,"Type 'disconnect' to make the bot disconnect the channel and shut down");
    		sendMessage(channel, "Type 'commands' to get a full list of my current capabilities");
    	}
    	
    	// this will disconnect the bot if user types 'disconnect twice'
    	if (message.equalsIgnoreCase("disconnect")) {
    		
    		// checking if it's the first time that disconnect has been written
    		if(check) {
    			tempCount = msgCount;
    			check = !check;
    			sendMessage(channel, "Type 'disconnect' again to confirm you want to shut down the bot");
    		}
    		
    		// checking if its the second time that disconnect has been written, and that it is written directly after the first one
    		else if(!check && tempCount == msgCount - 1){
    			sendMessage(channel, "Disconnecting and shutting down");
    			sendMessage(channel, "Have a great day!");
    			disconnect();
    			dispose();
    		}

    	}
    	
    	// if the user types disconnect once, then something else, the bool value is reset so the bot doesn't disconnect at once the next time the user types disconnect 
    	if(tempCount != msgCount) {
			check = true;
		}
    	
    	// split message into its individual words based on punctuation
    	String[] words = message.split("\\W+");
    	
    	for (int a = 0; a < words.length ; a++ ) {
//    		sendMessage(channel,  sender + ": " + words[a]);
    		
    		// TIME local
    		if (words[a].equalsIgnoreCase("time") && words.length == 1) {
        		try {
					getTime(channel,sender);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
    		// TIME one word name
    		else if (words[a].equalsIgnoreCase("time") && words.length == 2) {
        		for (int b = 0; b < 2; b++) {
	    			try {
						getTime(channel,sender,words[b]);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
    		}
    		// Time two word name
    		else if (words[a].equalsIgnoreCase("time") && words.length == 3) {
        		for (int b = 0; b < 2; b++) {
	    			try {
						getTime(channel,sender,words[b] + "+" + words[b+1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
    		}
    		// WEATHER zip code
        	if (isNumeric(words[a])) {
        		int zip = Integer.parseInt(words[a]);
        		try {
					getWeather(channel, sender, zip);
				}catch (Exception e) {
					e.printStackTrace();
				}
//        		sendMessage(channel,  sender + ": the zip code you entered is " + zip);
        	}
        	// WEATHER 2 word names
        	if (words[a].equalsIgnoreCase("weather")) {
        		for (int b = 0; b < words.length; b++) {
        			if (b==0 && words[0].equalsIgnoreCase("how")) {continue;}
        			if(!isNumeric(words[b])){
        				try {
    						getWeather(channel, sender, words[b] + "%20" + words[b+1]);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
        			}
        		}
        	}
        	// WEATHER 1 word names
        	// FLAG is to stop this from running if weather api has been called for 2 word names
        	if (words[a].equalsIgnoreCase("weather") && FLAG) {
        		for (int b = 0; b < words.length; b++) {
        			if (b==0 && words[0].equalsIgnoreCase("how")) {continue;}
        			if(!isNumeric(words[b])){
        				try {
    						getWeather(channel, sender, words[b]);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
        			}
        		}
        	}
        	// TWITTER trending tags
        	if(words[a].equalsIgnoreCase("twitter")) {
        		try {
					TwitterTrendingTags(channel, sender);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
        	}
        	// YOUTUBE search by name, get url
        	if(words[a].equalsIgnoreCase("youtube")) {
        		try {
					getYoutube(channel, sender, message);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	// CORONA, COVID-19, get worldwide results
        	if(words[a].equalsIgnoreCase("corona")) {
        		try {
					getCoronaInfo(channel,sender);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	// Stands4API
        	if(words[a].equalsIgnoreCase("abbreviation") || words[a].equalsIgnoreCase("define")
        			|| words[a].equalsIgnoreCase("literature") || words[a].equalsIgnoreCase("convert")
        			|| words[a].equalsIgnoreCase("synonym")) {
           		try {
           			String query = words[a+1];
           			for (int i = 2; i < words.length; i++) {
           				query += "+" + words[a+i];
           			}
           			stands4(channel, sender, words[a], query);
           		} catch (Exception e) {
            		e.printStackTrace();
            	}
        	}
    	}
        	
    }
    
    // Checking if a string is numeric
    
    public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        int i = Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
    
    // To get system time
    // TODO: figure out a way to get time for the user instead of the machine running the bot
    void getTime(String channel, String sender) throws Exception {
    	String time = new java.util.Date().toString();
    	sendMessage(channel, sender + ": The time is now: " + time);
    }
    void getTime(String channel, String sender, String place) throws Exception {
    	place = place.replace(" ", "+");
    	String POSTS_API_URL = "https://www.amdoren.com/api/timezone.php?api_key=" + TIME_KEY + "&loc=" + place;
    	Object obj = url_GET_method(POSTS_API_URL);
    	
    	Long error = (Long)((JSONObject) obj).get("error");
    	if(error != 0) {
    		throw new Exception("error " + error);
    	}

    	String time = (String)((JSONObject) obj).get("time");
    	
    	sendMessage(channel, sender + ": The time is now: " + time);
    	sendMessage(channel, sender + ": The error is now: " + error);
    	sendMessage(channel, sender + ": The url is now: " + POSTS_API_URL);


    }
    
    
    // To get temperature based on zip code
    
    void getWeather(String channel, String sender, int z) throws Exception {
    	String POSTS_API_URL = WEATHER_API_URL + "zip=" + z + "&appid=" + OWMkey;
    	Object obj = url_GET_method(POSTS_API_URL); 

        
        // typecasting obj to JSONObject 
        JSONObject jo = (JSONObject) obj; 
        JSONObject main = (JSONObject) jo.get("main");
        double temp = (double) main.get("temp");
        double feels = (double) main.get("feels_like");
        
        double min_d, max_d;
        long min_l, max_l;
        String min, max;
        try {
        	min_d = (double) main.get("temp_min");
        	min = String.valueOf(min_d);
        } catch (ClassCastException e) {
        	min_l = (long) main.get("temp_min");
        	min = String.valueOf(min_l);
        }
        try {
        	max_d = (double) main.get("temp_max");
        	max = String.valueOf(max_d);
        } catch (ClassCastException e) {
        	max_l = (long) main.get("temp_max");
        	max = String.valueOf(max_l);
        }
        
        String city = (String) jo.get("name");
        sendMessage(channel, sender + ": The temperature in " + city + " is " + temp + " Fahrenheit, but it feels like " + feels + " Fahrenheit.");
        sendMessage(channel, "Minimum Temperature: " + min);
        sendMessage(channel, "Maximum Temperature: " + max);
        FLAG = false;
    }
    
    // to get temperature based on name of place
    
    void getWeather(String channel, String sender, String name) throws Exception {
    	String POSTS_API_URL = WEATHER_API_URL + "q=" + name + "&appid=" + OWMkey;
    	Object obj = url_GET_method(POSTS_API_URL); 
        
        // typecasting obj to JSONObject 
        JSONObject jo = (JSONObject) obj; 
        JSONObject main = (JSONObject) jo.get("main");
        double temp = (double) main.get("temp");
        double feels = (double) main.get("feels_like");
        
        double min_d, max_d;
        long min_l, max_l;
        String min, max;
        try {
        	min_d = (double) main.get("temp_min");
        	min = String.valueOf(min_d);
        } catch (ClassCastException e) {
        	min_l = (long) main.get("temp_min");
        	min = String.valueOf(min_l);
        }
        try {
        	max_d = (double) main.get("temp_max");
        	max = String.valueOf(max_d);
        } catch (ClassCastException e) {
        	max_l = (long) main.get("temp_max");
        	max = String.valueOf(max_l);
        }
       
        String city = (String) jo.get("name");
        sendMessage(channel, sender + ": The temperature in " + city + " is " + temp + " Fahrenheit, but it feels like " + feels + " Fahrenheit.");
        sendMessage(channel, "Minimum Temperature: " + min);
        sendMessage(channel, "Maximum Temperature: " + max);
        FLAG = false;
    }
    
    
    // For getting twitter trending tags
    
    void TwitterTrendingTags(String channel, String sender) throws TwitterException {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(TWITTER_KEYS[0])
                    .setOAuthConsumerSecret(TWITTER_KEYS[1])
                    .setOAuthAccessToken(TWITTER_KEYS[2])
                    .setOAuthAccessTokenSecret(TWITTER_KEYS[3]);
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            Trends trends = twitter.getPlaceTrends(1);
            sendMessage(channel, sender + ": The current top 10 trending tags worldwide are:-\n");
            int count = 0;
            for (Trend trend : trends.getTrends()) {
                if (count < 10) {
                	sendMessage(channel,trend.getName() + "\n");
                    count++;
                }
            }
    }
    
    void getYoutube(String channel, String sender, String keyword) throws Exception{
    	keyword = keyword.replace(" ", "%20");
    	String POSTS_API_URL = YOUTUBE_API_URL + "q=" + keyword + "&type=video&key=" + YT_KEY;
    	
    	Object obj = url_GET_method(POSTS_API_URL); 
//      sendMessage(channel, sender + " " + response.body());
        // typecasting obj to JSONObject 
        JSONObject jo = (JSONObject) obj;
        
        JSONArray items= (JSONArray)jo.get("items");
        JSONObject index = (JSONObject) items.get(0);
        String vidId = (String) ((JSONObject)index.get("id")).get("videoId");
        
        sendMessage(channel, sender + ": https://www.youtube.com/watch?v=" + vidId);
    }
    
    void getCoronaInfo(String channel, String sender) throws Exception {
    	String POSTS_API_URL = "https://2019ncov.asia/api/cdr";
    	
    	Object obj = url_GET_method(POSTS_API_URL); 

        JSONObject jo = (JSONObject) obj;
        JSONArray results = (JSONArray)jo.get("results");
        
        JSONObject index = (JSONObject) results.get(0);
        long conf_t = (long) index.get("confirmed");
        String conf = String.format("%,d", conf_t);
        
        index = (JSONObject) results.get(1);
        long deaths_t = (long) index.get("deaths");
        String deaths = String.format("%,d", deaths_t);
        
        index = (JSONObject) results.get(2);
        long rec_t = (long) index.get("recovered");
        String rec = String.format("%,d", rec_t);
        
       	long epoch = (long) jo.get("last_updated");
    	Date update = new Date( epoch );

    	sendMessage(channel, "(Worldwide results)");

    	sendMessage(channel, "Number of confirmed cases: " + conf);
    	sendMessage(channel, "Deaths: " + deaths);
    	sendMessage(channel, "Recovered: " + rec);
    	sendMessage(channel, "(Data last updated on: " + update + ")");
    }
    
    void stands4 (String channel, String sender, String type, String query) throws Exception {

    	String POSTS_API_URL = "https://www.abbreviations.com/services/v2/";
    	if ("abbreviation".equalsIgnoreCase(type)) {
			POSTS_API_URL += "abbr.php?term=" + query;
		} else if ("define".equalsIgnoreCase(type)) {
			POSTS_API_URL += "defs.php?word=" + query;
		} else if ("literature".equalsIgnoreCase(type)) {
			POSTS_API_URL += "literature.php?term=" + query;
		} else if ("convert".equalsIgnoreCase(type)) {
			POSTS_API_URL += "conv.php?expression=" + query;
		} else if ("synonym".equalsIgnoreCase(type)) {
			POSTS_API_URL += "syno.php?word=" + query;
		}
    	POSTS_API_URL += "&uid=8130&tokenid=HM8iFIUpXVOoHyRE&format=json";
       // sendMessage(channel, POSTS_API_URL);

    	Object obj = url_GET_method(POSTS_API_URL); 

        
        //System.out.print(response);
        JSONObject jo = (JSONObject) obj;
        
        // If only one result, it is an object, but if more than one, it is an array type
        boolean flag1 = true, flag2 = true;
        try {
        	JSONArray result = (JSONArray)jo.get("result");
        } catch (Exception e) {
        	flag1 = false;
		}
        try {
        	JSONObject result = (JSONObject)jo.get("result");
        } catch (Exception e) {
        	flag2 = false;
		}
        
        String definition = null, title = null, writer = null, link = null, term = null, synonym = null;
        // if array
        if(flag1) {
        	JSONArray result = (JSONArray)jo.get("result");
        	synonym = (String) ((JSONObject) result.get(0)).get("synonyms");
        	term = (String)((JSONObject) result.get(0)).get("term");
        	definition = (String)((JSONObject) result.get(0)).get("definition");
        	title = (String)((JSONObject) result.get(0)).get("title");
        	writer = (String)((JSONObject) result.get(0)).get("writer");
        	link = (String)((JSONObject) result.get(0)).get("link");
        }
        // if not array
        if(flag2) {
        	JSONObject result = (JSONObject)jo.get("result");
        	synonym = (String) result.get("synonyms");
        	term = (String) result.get("term");
        	definition = (String) result.get("definition");
        	title = (String)result.get("title");
        	writer = (String)result.get("writer");
        	link = (String)result.get("link");
        }
        // convert API has result as string, not object or array
        if (flag1 || flag2) {
        	if (definition != null)
        		sendMessage(channel, term + " - " + definition);
        
        	if (title != null)
        		sendMessage(channel, "Title: " + title);
        	
        	if (writer != null)
        		sendMessage(channel, "Writer: " + writer);
        
        	if (link != null)
        		sendMessage(channel, "Link: " + link);
        
        	if (synonym != null)
        		sendMessage(channel, "Synonyms: " + synonym);
        	else if(synonym == null && type.equalsIgnoreCase("synonym")) {
        		sendMessage(channel, "No synonyms found for " + query);
        	}
        }
        else {
        	String results = (String)jo.get("result");
        	sendMessage(channel, results);
        }
    }
    
    
	private Object url_GET_method(String POSTS_API_URL) throws IOException, InterruptedException, ParseException {
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(POSTS_API_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Object obj = new JSONParser().parse(response.body());
		return obj;
	}
}