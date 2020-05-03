package com.example.tripcalculator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tripcalculator.R;
import com.example.tripcalculator.adapters.SearchResultAdapter;
import com.example.tripcalculator.databinding.ActivitySearchBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class SearchActivity extends AppCompatActivity {

    private static final int REQUEST_FINE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_INTERNET_PERMISSION = 2;
    //map vars
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private SearchView searchView;
    //permission vars
    private static boolean isLocationAllowed = false;
    private static boolean isNetworkConnected = false;
    //search vars
    Snackbar snackbar;

    private RequestQueue requestQueue;
    private final static String TAG = "OSM_REQUEST";
    SearchResultAdapter adapter;
    ActivitySearchBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        snackbar = Snackbar.make(findViewById(R.id.search_layout), "No Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Impostazioni", (v) -> { setSettingsIntent(); });

        requestQueue = Volley.newRequestQueue(this);
        Context context = getApplicationContext();
        adapter = new SearchResultAdapter(this);

        binding.searchResultList.setLayoutManager(new LinearLayoutManager(this));
        binding.searchResultList.setAdapter(adapter);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            adapter.clearSearchResult();
            createRequest(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =  (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchManager != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isLocationAllowed = true;
                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.map_fragment), "Permessi negati!\nLa tua posizione non può essere visualizzata sulla mappa!", Snackbar.LENGTH_LONG);
                }
                break;
            case REQUEST_INTERNET_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    //TODO modificare "map_fragment" nel frammento alternativo della visualizzazione del percorso
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.map_fragment), "Impossibile utilizzare la mappa senza l'accesso ad internet!", Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    private void setSettingsIntent() {
        Intent intent = new Intent();
        intent.setAction((Settings.ACTION_WIRELESS_SETTINGS));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    private void checkPermissions(){
        isLocationAllowed = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
        }else {
            isLocationAllowed = true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }
    }

    private void createRequest(String place){
        String url = "https://nominatim.openstreetmap.org/search?q=" + place + "&format=json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject singleAddress = response.getJSONObject(i);
                        adapter.addLocation(Double.parseDouble(singleAddress.get("lat").toString()), Double.parseDouble(singleAddress.get("lon").toString()), singleAddress.get("display_name").toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEARCH", error.toString());
            }
        });

        jsonArrayRequest.setTag(TAG);
        requestQueue.add(jsonArrayRequest);
    }

    private void registerNetworkCallback(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService((Context.CONNECTIVITY_SERVICE));

        if (connectivityManager != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isNetworkConnected = false;
        }
    };
}
