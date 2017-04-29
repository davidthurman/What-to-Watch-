package com.david.thurman.whattowatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new JSONTask().execute("https://api.themoviedb.org/3/movie/popular?api_key=5ce391972548ceb04a7fa6bc7502f098&language=en-US&page=1");
    }

    public class JSONTask extends AsyncTask<String, String, String > {

        Movie[] movies;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray videos = parentObject.getJSONArray("results");
                movies = new Movie[videos.length()];
                for (int x = 0; x < videos.length(); x++){
                    JSONObject movieJson = videos.getJSONObject(x);
                    String title = movieJson.getString("title");
                    String year = movieJson.getString("release_date");
                    JSONArray genreIdsJson = movieJson.getJSONArray("genre_ids");
                    int[] genreIds = new int[genreIdsJson.length()];
                    for (int y = 0; y < genreIdsJson.length(); y++){
                        genreIds[y] = genreIdsJson.getInt(y);
                    }
                    String id = movieJson.getString("id");
                    Movie movie = new Movie(title, id);
                    movie.poster = "http://image.tmdb.org/t/p/w185/" + movieJson.getString("poster_path");
                    movie.genreIds = genreIds;
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(movie.poster).getContent());
                        movie.bitmap = bitmap;
                    } catch (Exception e) {
                        return null;
                    }
                    movies[x] = movie;
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int movieRows = movies.length / 2;
            LinearLayout results = (LinearLayout) findViewById(R.id.results);
            for (int x = 0; x < movieRows; x++){
                LinearLayout row = new LinearLayout(MainActivity.this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                for (int y = 0; y < 2; y++) {
                    LinearLayout movieLayout = new LinearLayout(MainActivity.this);
                    movieLayout.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 50));

                    Movie movie = movies[(2 * x) + y];

                    movieLayout.setOrientation(LinearLayout.VERTICAL);
                    ImageButton moviePoster = new ImageButton(MainActivity.this);
                    moviePoster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("Test");
                        }
                    });
                    //moviePoster.setMinimumWidth((int)(row.getWidth() / 2));
                    moviePoster.setImageBitmap(movie.bitmap);
                    movieLayout.addView(moviePoster);
                    TextView movieTitle = new TextView(MainActivity.this);
                    //movieTitle.setText(movie.title);
                    movieLayout.addView(movieTitle);
                    row.addView(movieLayout);
                }
                results.addView(row);
            }
        }


    }
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}
