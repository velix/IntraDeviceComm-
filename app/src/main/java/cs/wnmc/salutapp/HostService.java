package cs.wnmc.salutapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.net.URI;


public class HostService extends Fragment implements View.OnClickListener{
    private static final String TAG = "Salut-HostService";
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;

    public interface onClientRegisteredListener {
        void onClientRegistered(String device);
    }
    onClientRegisteredListener mclientListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TextView registered_dev;

    public Button sendBtn;

    public HostService() {
        // Required empty public constructor


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * */
     // TODO: Rename and change types and number of parameters
    public static HostService newInstance() {
        HostService fragment = new HostService();
        Bundle args = new Bundle();

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        try{
            mclientListener = (onClientRegisteredListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "In fragment OnCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Inflating fragment layout");
        View v =  inflater.inflate(R.layout.fragment_host, container, false);

        sendBtn = (Button)v.findViewById(R.id.send_file_button);

        sendBtn.setOnClickListener(this);

        return v;


    }

    public void updateClientsList()
    {

        //find all registered devices and append them
        Log.d(TAG, "Attempting to access all registered clients");
        try{
            Log.d(TAG, "Attempting to re-inflate view");
            View v =  View.inflate(getActivity(), R.layout.fragment_host, null);
            registered_dev = (TextView)v.findViewById(R.id.registered_dev_view);

        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }

            ArrayList<String> registered = ((MainActivity)getActivity()).network.getReadableRegisteredNames();
            for(String device : registered)
                registered_dev.append(device);

//        View.inflate(getActivity(), R.layout.fragment_host, null);
        Log.d(TAG, "Successfully appended device");

    }

    @Override
    public void onClick(View v)
    {

        if(v.getId() == R.id.send_file_button)
        {
            startSelectImage();
        }
    }

    public void startSelectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User has picked an image. Transfer it to group owner i.e peer using
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "ActivityResult not ok");
            return;
        }

        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if (data == null) {
                Log.d(TAG, "ActivityResult data null");
                Toast.makeText(getActivity().getApplicationContext(), "Try another image, previous was null", Toast.LENGTH_SHORT).show();
                startSelectImage();
            }
            Log.d(TAG, "Selected image");
            android.net.Uri uri = data.getData();

            initiateSending(uri);

        }
    }

    private void initiateSending(Uri uri)
    {
        Log.d(TAG, "Initiating attempt to create God message");
//        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File file = new File(baseDir + File.separator + "cat.jpg");

//        File file = new File("/storage/sdcard1/cat.jpg");


        URI jURI;
        try {
            jURI = new URI(uri.toString());
            String path = jURI.getPath();
            Log.d(TAG, "Successful uri transform. Path is: "+path);

            File file = new File(path);
            Log.d(TAG, "Successful file creation from uri.");

            int size = (int) file.length();
            Log.d(TAG, "bytes buffer size: " + size);
            byte[] bytes = new byte[size];


            try {
//                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                BufferedInputStream buf = new BufferedInputStream(getActivity().getContentResolver().openInputStream(uri));
                int res = buf.read(bytes, 0, bytes.length);
                Log.d(TAG, "Result from buffer read: " + res);
                buf.close();


                String imageAsBytes = Base64.encodeToString(bytes, Base64.DEFAULT);
                Log.d(TAG, "Successful bytes encoding.");

                Message image = new Message();

                String[] columns = path.split("/");
                image.name = columns[columns.length-1];
                Log.d(TAG, "God Object name: " + image.name);
//                columns = image.name.split(".");
//                image.type = columns[columns.length - 1];
//                Log.d(TAG, "God Object name: " + image.name + "type " + image.type);

//                image.name = "newFile.jpg";
                image.encodedMessage = imageAsBytes;


                ((MainActivity)getActivity()).send(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
