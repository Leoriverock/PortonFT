package com.bitgymup.gymup.users;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bitgymup.gymup.GymPromotion;
import com.bitgymup.gymup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bitgymup.gymup.users.UserHome.salir;

public class UserPromo extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private String userId;
    private SharedPreferences userId1;
    //ProgressDialog progreso;
    List<GymPromotion> elements;
    private RequestQueue request;
    private TextView gimnasio_nombre;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;
    RecyclerView recyclerPromotion;
    ArrayList<GymPromotion> listPromotion;

    //Inicializar las variables
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_promo);
        request = Volley.newRequestQueue(this);


        //Asignación de la variable
        gimnasio_nombre  = (TextView) findViewById(R.id.gimnasio_nombre);
        gimnasio_nombre.setText( getUserLogin("namegym"));
        drawerLayout = findViewById(R.id.drawer_layout);
        userId1 = getSharedPreferences("user_login", Context.MODE_PRIVATE);
        String userId = userId1.getString("username", "");
        getGymId(userId);

    }
    private String getUserLogin(String key) {
        SharedPreferences sharedPref = getSharedPreferences("user_login", Context.MODE_PRIVATE);
        String username = sharedPref.getString(key,"");
        return username;
    }

    private void getGymId(String username) {
        String url = "http://gymup.zonahosting.net/gymphp/GetGymByUser.php?username="+username.trim();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                elements = new ArrayList<>();
                    try {
                        jsonObject   = response.getJSONObject(0);
                    //    String id        = jsonObject.optString("id");
                    //    String username  = jsonObject.optString("username");
                        String idGym     = jsonObject.optString("idGym");

                   //     Toast.makeText(getApplicationContext(), idGym.trim(), Toast.LENGTH_LONG).show();
                        String url = "http://gymup.zonahosting.net/gymphp/getPromotionList.php?idgym=" + idGym.trim();
                        loadWebService(url);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void loadWebService(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                elements = new ArrayList<>();
                for (int i= 0; i < response.length(); i++){
                    try {
                        jsonObject   = response.getJSONObject(i);
                        String id        = jsonObject.optString("id");
                        String title     = jsonObject.optString("title");
                        String promotion = jsonObject.optString("promotion");

                        elements.add(new GymPromotion(id, title, promotion));
                        GymPromotionAdapter listAdapter = new GymPromotionAdapter(elements, getApplicationContext(), new GymPromotionAdapter.OnItemClickListener() {

                            @Override
                            public void onItemClick(GymPromotion item) {
                                // acá si se hace click en alguna tarjeta
                            }

                        });
                        RecyclerView recyclerView = findViewById(R.id.recyclerPromotion);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(listAdapter);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                elements = new ArrayList<>();
                elements.add(new GymPromotion("0", getString(R.string.noPromotion), getString(R.string.noPromotions2)));
                GymPromotionAdapter listAdapter = new GymPromotionAdapter(elements, getApplicationContext(), new GymPromotionAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(GymPromotion item) {

                    }
                });
                RecyclerView recyclerView = findViewById(R.id.recyclerPromotion);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(listAdapter);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void ClickMenu(View view){
        //Abrir drawer
        openDrawer(drawerLayout);
        try
        {
            InputMethodManager im = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception ex)
        {
            //Log.e(TAG, ex.toString());
        }
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //Open drawer Layout, es un procedimiento público que no necesita ser instanciado, es visible en toda la APP.
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickMenuOptionsUser(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.menu_user_3);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.acerca_de:
                startActivity(new Intent(this, UserDevelopers.class));
                return true;
            /*case R.id.contacto:
                startActivity(new Intent(this, AdminDevContact.class));
                return true;*/
            default:
                return false;
        }
    }

    public void ClickLogo(View view){
        //Cierre del Drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //Close drawer Layout, verificando condición
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            //Cunando el drawer esta abierto, se CIERRA
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

/*Inicio de los LINKS*/
    public void ClickHomeU(View view){
        redirectActivity(this, UserHome.class);
    }
    public void ClickMiNutri(View view){
        redirectActivity(this, UserSaludNutricion.class);
    }
    public void ClickAgendaU(View view){
        redirectActivity(this, UserReservas.class);
    }
    public void ClickServiciosU(View view){
        redirectActivity(this, UserServicios.class);
    }
    public void ClickMiSalud(View view){
        redirectActivity(this, UserSalud.class);
    }
    public void ClickPagosU(View view){
        redirectActivity(this, UserPagos.class);
    }
    public void ClickPromoU(View view){
        recreate();
    }
    public void ClickMyProfileU(View view){
        redirectActivity(this, UserProfile.class);
    }
    public void ClickLogout(View view){
        //Close APP
        salir(this);
    }

/*Fin de los LINKS*/




    public static void redirectActivity(Activity activity, Class aClass) {
        //Inicializar intent
        Intent intent = new Intent(activity, aClass);
        //Establcer las flags
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Inicio de la Activity
        activity.startActivity(intent);

    }

    @Override
    protected void onPause(){
        super.onPause();
        //Close drawer
        closeDrawer(drawerLayout);
    }



}