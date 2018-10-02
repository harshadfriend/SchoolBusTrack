package karjatonline.schoolbustrack;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnS;
    String url = "https://wwwkarjatonlinecom.000webhostapp.com/add.php";
    String url2 = "https://wwwkarjatonlinecom.000webhostapp.com/getdata.php";

    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        btnS=findViewById(R.id.btnS);
        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                new CountDownTimer(10000,1000){
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }
                };//.start();

                LocationManager lm=(LocationManager)getSystemService(LOCATION_SERVICE);
                LocationListener ll=new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        ArrayList nameValuePairs = new ArrayList();

                        nameValuePairs.add(new BasicNameValuePair("name","lat lang"));

                        nameValuePairs.add(new BasicNameValuePair("lat",String.valueOf(location.getLatitude())));
                        nameValuePairs.add(new BasicNameValuePair("lon",String.valueOf(location.getLongitude())));


//        Log.d(“well2”, “msg”);
                        StrictMode.setThreadPolicy(policy);

//        Log.d(“well3”, “msg”);
//http post
                        try{
                            HttpClient httpclient = new DefaultHttpClient();

                            HttpPost httppost = new HttpPost(url);

                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity entity = response.getEntity();
                            InputStream is = entity.getContent();
//            Log.d(“well4”, “msg”);

//            Log.e(“log_tag”, “connection success “);
                            //  Toast.makeText(getApplicationContext(), "Please Wait….", Toast.LENGTH_SHORT).show();
                        }

                        catch(Exception e)
                        {
//            Log.e(“log_tag”, “Error in http connection “+e.toString());
                            Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
//            Log.d(“well5”, “msg”);

                        }

                        getJSON(url2);

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,1,ll);
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("You're Here !"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void mapChange(String[][] x){
        String[][] y=x;
//        Toast.makeText(this, "map changed"+y[x.length-1][0], Toast.LENGTH_SHORT).show();
        double lt=Double.parseDouble(y[x.length-1][0]);
        double ln=Double.parseDouble(y[x.length-1][1]);
        LatLng syd=new LatLng(lt,ln);
        mMap.clear();
//        mMap.setMaxZoomPreference(5);
        mMap.addMarker(new MarkerOptions().position(syd).title("You're Here !"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(syd,17));
    }

    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //           Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                //  tv.setText(s);

                try {
                    JSONArray jsonArray = new JSONArray(s);
                    String[][] heroes = new String[jsonArray.length()][2];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        heroes[i][0] = obj.getString("lat");
                        heroes[i][1]=obj.getString("lon");
                    }
                    mapChange(heroes);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }


                /*try {
                    loadIntoListView(s);

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
}
