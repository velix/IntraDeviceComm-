package cs.wnmc.salutapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements SalutDataCallback, View.OnClickListener, HostService.onClientRegisteredListener
{

    /*
        This simple activity demonstrates how to use the Salut library from a host and client perspective.
     */

    public static final String TAG = "Salut-MainActivity";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    public Button hostingBtn;
    public Button discoverBtn;
    SalutDataCallback callback;
    private boolean isHost = false;


    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HostService hostService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostingBtn = (Button) findViewById(R.id.hosting_button);
        discoverBtn = (Button) findViewById(R.id.discover_services);

        hostingBtn.setOnClickListener(this);
        discoverBtn.setOnClickListener(this);

        //fragment management
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        /*Create a data receiver object that will bind the callback
        with some instantiated object from our app. */
        dataReceiver = new SalutDataReceiver(this, this);


        /*Populate the details for our awesome service. */
        serviceData = new SalutServiceData("testAwesomeService", 60606,
                "HOST");

        /*Create an instance of the Salut class, with all of the necessary data from before.
        * We'll also provide a callback just in case a device doesn't support WiFi Direct, which
        * Salut will tell us about before we start trying to use methods.*/
        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                // wiFiFailureDiag.show();
                // OR
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });

    }

    //HOST
    protected void setupNetwork()
    {


        this.isHost = true;

        if(!network.isRunningAsHost)
        {
            //add fragment
            hostService = HostService.newInstance("", "");
            Log.d(TAG, "Attempting to add MainActivity layout");
            fragmentTransaction.add(R.id.main_container, hostService);
            Log.d(TAG, "Attempting to commit fragment's layout");
            fragmentTransaction.commit();

            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
//                    Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Client: " + salutDevice.instanceName + " registered");

//                    HostService hostService =  (HostService)fragmentManager.findFragmentById(R.id.hostServiceFragment);
//                    Log.d(TAG, "Attempting to update registered clients list on fragment");
//                    hostService.updateClientsList(salutDevice.instanceName);

                }
            });


            hostingBtn.setText("Stop Service");
            discoverBtn.setAlpha(0.5f);
            discoverBtn.setClickable(false);
        }
        else
        {
            network.stopNetworkService(false);
            hostingBtn.setText("Start Service");
            discoverBtn.setAlpha(1f);
            discoverBtn.setClickable(true);
            fragmentManager.beginTransaction().remove(hostService).commit();
        }
    }

    protected void sendTestMessage()
    {
            Log.i(TAG, "Host attempting to send some data");
            Message myMessage = new Message();
            myMessage.description = "See you on the other side!";

            network.sendToAllDevices(myMessage, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "Oh no! The data failed to send.");
                }
            });

//        SalutDevice deviceToSendTo = network.registeredClients.get(0);
//
//        network.sendToDevice(deviceToSendTo, myMessage, new SalutCallback() {
//            @Override
//            public void call() {
//                Log.e(TAG, "Oh no! The data failed to send.");
//            }
//        });

    }

    //CLIENT
    private void discoverServices()
    {

        if(!network.isRunningAsHost && !network.isDiscovering)
        {

            network.discoverNetworkServices(new SalutCallback() {
                @Override
                public void call() {
                    SalutDevice host = network.foundDevices.get(0);
                    Toast.makeText(getApplicationContext(), "Device: " + host.instanceName + " found.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Host: " + host.instanceName + " found.");

                    Toast.makeText(getApplicationContext(), "Attempting to register with host", Toast.LENGTH_SHORT).show();

                    network.registerWithHost(host, new SalutCallback() {
                        @Override
                        public void call() {
                            Log.d(TAG, "We're now registered.");
                            Toast.makeText(getApplicationContext(), "Registered with host", Toast.LENGTH_SHORT).show();
                        }
                    }, new SalutCallback() {
                        @Override
                        public void call() {
                            Log.d(TAG, "We failed to register.");
                            Toast.makeText(getApplicationContext(), "FAILED to register with host", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, true);


            discoverBtn.setText("Stop Discovery");
            hostingBtn.setAlpha(0.5f);
            hostingBtn.setClickable(false);
        }
        else
        {
            network.stopServiceDiscovery(true);
            discoverBtn.setText("Discover Services");
            hostingBtn.setAlpha(1f);
            hostingBtn.setClickable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*Create a callback where we will actually process the data.*/
    @Override
    public void onDataReceived(Object data) {
        Log.d(TAG, "Received network data.");
        Toast.makeText(getApplicationContext(), "Received data successfully", Toast.LENGTH_SHORT).show();
        try
        {
            Message newMessage = LoganSquare.parse(data.toString(), Message.class);
            Log.d(TAG, newMessage.description);  //See you on the other side!
            //Do other stuff with data.
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network data.");
        }
    }

    @Override
    public void onClick(View v) {

        if(!Salut.isWiFiEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(v.getId() == R.id.hosting_button)
        {
            setupNetwork();

        }
        else if(v.getId() == R.id.discover_services)
        {
            discoverServices();
        }
        else if (v.getId() == R.id.disconnect)
        {
            Button disconnectBtn = (Button) findViewById(R.id.disconnect);
            disconnectBtn.setText("Disconnect");
            disconnectBtn.setAlpha(1f);
            disconnectBtn.setClickable(false);

            onDestroy();

            disconnectBtn.setClickable(true);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if(this.isHost)
            //pass whether to close wifi
            network.stopNetworkService(false);
        else
            //can pass callback onSuccess, onFailure
            //single argument form disables Wifi
            network.unregisterClient(false);
    }

    @Override
    public void onClientRegistered(String device) {

    }
}
