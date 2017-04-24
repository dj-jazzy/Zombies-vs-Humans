package com.example.mangaramu.zombies_vs_humans;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.ArrayMap;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.mangaramu.zombies_vs_humans.Model.PlayerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mangaramu on 4/23/2017.
 */

public class PullGamedatathread extends Thread {

    ArrayMap<String,PlayerItem> gameusers;
    Boolean running = true;
    String LINK = Resources.getSystem().getString(R.string.URL);
    Handler handle;
    PullGamedatathread(ArrayMap<String,PlayerItem> x, Handler y)
    {
        gameusers=x;
        handle=y;
    }

    @Override
    public void run() {

            AndroidNetworking.get(LINK)
                    .addQueryParameter("Users", "Active")
                    .addQueryParameter("Username",gameusers.valueAt(0).getPlayername()) //pull data of locations from server
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray users = response.getJSONArray("Users");
                                for (int x = 0; x < users.length(); x++) {
                                    String tmpuse = ((JSONObject) users.get(x)).getString("Username");
                                    Double tmplat = ((JSONObject) users.get(x)).getDouble("Latitude");
                                    Double tmplong = ((JSONObject) users.get(x)).getDouble("Longitude");
                                    String tmpstatus = ((JSONObject) users.get(x)).getString("HumOrZomb");

                                    if (gameusers.containsKey(tmpuse)) { //If the user is already within our array
                                        if (tmpuse.equals(gameusers.keyAt(0))) {
                                            gameusers.get(tmpuse).setHuorZomb(tmpstatus);
                                        } else {
                                            gameusers.get(tmpuse).setLattitude(tmplat);
                                            gameusers.get(tmpuse).setLongitude(tmplong);
                                            gameusers.get(tmpuse).setHuorZomb(tmpstatus);

                                        }
                                    } else {// if we do not know of the player
                                        gameusers.put(tmpuse, new PlayerItem(tmpuse, tmplat, tmplong, tmpstatus));
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });

            android.os.SystemClock.sleep(500);// sleep 500 milisecconds

            Message m = Message.obtain();//send empty message prompting
            m.setTarget(handle);
            m.sendToTarget();

    }

    public void norun()
    {
        running=false;
    }
}