package signin.project.com.finalproject;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//Adds new Places to the Server
public class AddPlaces extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "AddPeople";

    GoogleSignInAccount acct;
    private Spinner Cat;
    String category, name, address, comments, mAddressOutput, rating;

    //To get Current Address
    protected static GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;

    protected String errorMessage;
    EditText placeName, placeAdd, placeCom;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);

        buildGoogleApiClient();

        acct = getIntent().getExtras().getParcelable("user");

        //Sets up UI
        Cat = (Spinner)findViewById(R.id.PlaceCategory);
        placeName = (EditText) findViewById(R.id.PlaceName);
        placeAdd = (EditText) findViewById(R.id.PlaceAddress);
        placeCom = (EditText) findViewById(R.id.PlaceComments);


        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> spinner1Adapter = ArrayAdapter
                .createFromResource(this, R.array.Categories,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinner1Adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        Cat.setAdapter(spinner1Adapter);


    }
    //[Starts Location Code] Code based off of https://developer.android.com/training/building-location.html
    //and corresponding github
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    //Gets the current address based of Location
    protected void fetchCurAddress() {
        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.

        if (mCurrentLocation == null) {

            Log.wtf(TAG, errorMessage);

            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;


        // Using getFromLocation() returns an array of Addresses for the area immediately
        // surrounding the given latitude and longitude. The results are a best guess and are
        // not guaranteed to be accurate.
        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + mCurrentLocation.getLatitude() +
                    ", Longitude = " + mCurrentLocation.getLongitude(), illegalArgumentException);
        }


        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
        } else {
            Address address = addresses.get(0);

            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            mAddressOutput = address.getAddressLine(0);
            for (int i = 1; i < address.getMaxAddressLineIndex(); i++) {
                mAddressOutput = mAddressOutput + " " + address.getAddressLine(i);
            }
            System.out.println("output: " + mAddressOutput);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }



    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mCurrentLocation == null) {
            startLocationUpdates();
        }

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();

        fetchCurAddress();

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    //fills Address into edit field
    public void FillAddress(View V){
        placeAdd.setText(mAddressOutput);

    }

    //finds value of radio buttons
    public void findRating(View v){
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.radio1:
                if (checked)
                    rating = "1";
                    break;
            case R.id.radio2:
                if (checked)
                    rating = "2";
                    break;
            case R.id.radio3:
                if (checked)
                    rating = "3";
                break;
            case R.id.radio4:
                if (checked)
                    rating = "4";
                break;
            case R.id.radio5:
                if (checked)
                    rating = "5";
                break;
        }
    }

    //Sends Post Request to add user's place
    public void addPlace(View v) {
        category = (String) Cat.getSelectedItem();

        if (category.equals("Choose a Category")) {
            Toast.makeText(AddPlaces.this, "Please select a category", Toast.LENGTH_LONG).show();
        } else {
            name = placeName.getText().toString();
            address = placeAdd.getText().toString();
            comments = placeCom.getText().toString();

            final ProgressDialog mDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data", true);
            Map<String, String> jsonParams = new HashMap<String, String>();

            //creates JSON Object to send to API
            jsonParams.put("gId", acct.getId());
            jsonParams.put("category", category);
            jsonParams.put("name", name);
            jsonParams.put("address", address);
            jsonParams.put("rating", rating);
            jsonParams.put("comments", comments);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://finalprojectapi.herokuapp.com/index.php/users/" + acct.getId() + "/places";

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                    new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mDialog.dismiss();
                            Toast.makeText(AddPlaces.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mDialog.dismiss();
                            Toast.makeText(AddPlaces.this, "That didn't work!", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", System.getProperty("http.agent"));
                    return headers;
                }
            };
            queue.add(postRequest);
        }
    }


}
