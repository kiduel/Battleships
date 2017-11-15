package battleships.game.project.battleships.comunication;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import battleships.game.project.battleships.game.Battleship;
import battleships.game.project.battleships.game.HttpEventListener;

public class HttpHelper
{
    static String url = "https://luca-ucsc-teaching-backend.appspot.com/keystore/read?key=<";

    static String url_send =
          " https://luca-ucsc-teaching-backend.appspot.com/keystore/store?key=<";
    static String url_send_2 =  ">&val=<";
    public Context context;
    HttpEventListener listener;

    public static String separator = "//";

    /*
        data format :
         no of players online | player turn | board data | player
     */

    public HttpHelper(Context context, HttpEventListener listener)
    {
        this.context = context;
        this.listener = listener;
    }
    public void getResponse(String magicWord)
    {
        new GetUrlContentTask().execute(url + magicWord +">");

    }
    public void setMagicWord(String magicWord, String message)
    {
        new GetUrlContentTask().execute(url_send+magicWord+url_send_2+message+">");
    }
    private class GetUrlContentTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "o";
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            // this is executed on the main thread after the process is over
            // update your UI here
            if(!result.equals("ok\n")) {
                listener.onResponse(result);
            }
        }
    }

    public String formatMessage(int player_num, Battleship[] b)
    {
        String s = separator+Integer.toString(player_num)+separator;
        for(int i=0; i<7; i++)
        {
            s += Integer.toString(b[i].grid_id.x) + separator + Integer.toString(b[i].grid_id.y) +
                    separator + Integer.toString(b[i].getRotation()) + separator;
        }
      return s;
    }

    public String formatMoveMessage(int unique_id, int x, int y)
    {
        String s = separator+Integer.toString(unique_id)+separator+Integer.toString(x)+separator+
                Integer.toString(y)+separator;
        return s;
    }


}
