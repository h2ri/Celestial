package com.hari.development.celestial;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class MainActivity extends Activity {


    MarsWeather helper = MarsWeather.getInstance();
    TextView mTxtDegrees, mTxtWeather, mTxtError;
    ImageView mImageView;
    final static String RECENT_API_ENDPOINT = "http://marsweather.ingenology.com/v1/latest/";

    //fir image from flikr
    final static String
            FLICKR_API_KEY = "7d5446e8ebe40120fc01826101966770",
            IMAGES_API_ENDPOINT = "https://api.flickr.com/services/rest/?format=json&nojsoncallback=1&sort=random&method=flickr.photos.search&" +
                    "tags=mars,planet,rover&tag_mode=all&api_key=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtDegrees = (TextView) findViewById(R.id.degrees);
        mTxtError = (TextView) findViewById(R.id.error);
        mTxtWeather = (TextView) findViewById(R.id.weather);

        mImageView = (ImageView)findViewById(R.id.main_bg);

        loadWeatherData();

        try {
            searchRandomImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadWeatherData(){

        CustomJsonRequest request = new CustomJsonRequest(Request.Method.GET,
                RECENT_API_ENDPOINT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String minTemp, maxTemp , atmo;
                        int avgTemp;
                        try {

                            response = response.getJSONObject("report");

                            minTemp = response.getString("min_temp");
                            minTemp = minTemp.substring(0, minTemp.indexOf("."));

                            maxTemp = response.getString("max_temp");
                            maxTemp = maxTemp.substring(0, maxTemp.indexOf("."));

                            avgTemp = (Integer.parseInt(minTemp)+Integer.parseInt(maxTemp))/2;

                            atmo = response.getString("atmo_opacity");

                            mTxtDegrees.setText(avgTemp+"°");
                            mTxtWeather.setText(atmo);

                        } catch (JSONException e) {
                            txtError(e);
                            //e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtError(error);
                    }
                }
        );
        request.setPriority(Request.Priority.HIGH);
        helper.add(request);
    }

    private void txtError(Exception e) {
        mTxtError.setVisibility(View.VISIBLE);
        e.printStackTrace();
    }


    //image method

    private void searchRandomImage() throws Exception {
        if (FLICKR_API_KEY.equals(""))
            throw new Exception("You didn't provide a working Flickr API!");

        CustomJsonRequest request = new CustomJsonRequest
                (Request.Method.GET, IMAGES_API_ENDPOINT+ FLICKR_API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray images = response.getJSONObject("photos").getJSONArray("photo");
                            int index = new Random().nextInt(images.length());

                            JSONObject imageItem = images.getJSONObject(index);

                            String imageUrl = "http://farm" + imageItem.getString("farm") +
                                    ".static.flickr.com/" + imageItem.getString("server") + "/" +
                                    imageItem.getString("id") + "_" + imageItem.getString("secret") + "_" + "c.jpg";

                            loadImg(imageUrl);

                        } catch (Exception e) { imageError(e); }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imageError(error);
                    }
                });
        request.setPriority(Request.Priority.LOW);
        helper.add(request);
    }

    private void loadImg(String imageUrl) {
        // Retrieves an image specified by the URL, and displays it in the UI
        ImageRequest request = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageError(error);
                    }
                });

        // we don't need to set the priority here;
        // ImageRequest already comes in with
        // priority set to LOW, that is exactly what we need.
        helper.add(request);
    }
    int mainColor = Color.parseColor("#FF5722");

    private void imageError(Exception e) {
        mImageView.setBackgroundColor(mainColor);
        e.printStackTrace();
    }
}
