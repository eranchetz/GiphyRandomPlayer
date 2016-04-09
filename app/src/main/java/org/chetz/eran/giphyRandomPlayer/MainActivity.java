package org.chetz.eran.giphyRandomPlayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String mp4uri = null;
    VideoView vid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.chetz.eran.giphyRandomPlayer.R.layout.activity_main);

        vid = (VideoView) findViewById(org.chetz.eran.giphyRandomPlayer.R.id.videoView);

        String firstVideoUrl = "https://media.giphy.com/media/1IZ7XOCWvoQfu/giphy.mp4";


        //MediaController mc = new MediaController(this);
        vid.setVideoURI(Uri.parse(firstVideoUrl));
        vid.start();
        new GetUrlBG().execute("http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC");

        vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                vid.setVideoURI(Uri.parse(mp4uri));
                vid.start();
                new GetUrlBG().execute("http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private class GetUrlBG extends AsyncTask<String, String, Void> {
        InputStream in = null;
        String result = "";

        protected Void doInBackground(String... params) {
            try {
                String apiurl = params[0];
                //String apiurl = "https://example.com";
                URL url = new URL(apiurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                result = convertStreamToString(in);
                in.close();

            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            //parse JSON data
            try {


                //JSONArray jArray = object.getJSONArray("data");
                JSONObject object = new JSONObject(result);
                JSONObject jDataObj = object.getJSONObject("data");
                Log.i("JSON", jDataObj.toString());
                mp4uri = jDataObj.getString("image_mp4_url");
                Log.i("INFO:", mp4uri);


            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)

        }

        public String convertStreamToString(InputStream is)
                throws IOException {
            /*
             * To convert the InputStream to String we use the
             * Reader.read(char[] buffer) method. We iterate until the
            * Reader return -1 which means there's no more data to
            * read. We use the StringWriter class to produce the string.
            */
            if (is != null) {
                Writer writer = new StringWriter();

                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    is.close();
                }
                return writer.toString();
            } else {
                return "";
            }
        }


    }

}
