package util;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.io.FileReader;
import java.io.BufferedReader;

public class KeyValueParse
{

    private HashMap<String, String> mChannelGame = new HashMap<String, String>();

    public KeyValueParse() { }
    public KeyValueParse(String cfgfile) { configure(cfgfile); }
    public void configure(String cfgfile) 
    {      // format: key\tgameid
        BufferedReader bfrd = null;
        try
       	{
            FileReader inst = new FileReader(cfgfile);
            bfrd = new BufferedReader(inst);
            String line = null;
            while ((line=bfrd.readLine()) != null && line.length() > 0)
	    {
                if (line.startsWith("#")) { continue; }

                String [] items = line.split("\t| ");
                if (items.length != 2)
	       	{
                    System.err.printf("error format: %s\n", line);
                    continue;
                }

                    String key = items[0];
                    String value = items[1];

                    mChannelGame.put(key, value);

            }
            bfrd.close();
        } catch (Exception ex)
       	{
            ex.printStackTrace();
        }
    }

    public String getGameId(String key)
    {
	String gameid = mChannelGame.get(key);        
	return gameid;
    }

}
