package cs.wnmc.salutapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class HostService extends Fragment implements View.OnClickListener{
    public static final String TAG = "Salut-HostService";

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
    public static HostService newInstance(String param1, String param2) {
        HostService fragment = new HostService();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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

//    public void updateClientsList(String device)
//    {
//
//        //find all registered devices and append them
//        Log.d(TAG, "Attempting to access all registered clients");
//        try{
//            Log.d(TAG, "Attempting to re-inflate view");
//            View v =  View.inflate(this, R.layout.fragment_host, null);
//            registered_dev = (TextView)v.findViewById(R.id.registered_dev_view);
//
//        }
//        catch(NullPointerException e)
//        {
//            e.printStackTrace();
//        }
//
////            ArrayList<String> registered = ((MainActivity)getActivity()).network.getReadableRegisteredNames();
////            for(String device : registered)
////                registered_dev.append(device);
//        registered_dev.append(device);
//        Log.d(TAG, "Successfully apended device");
//
//    }

    @Override
    public void onClick(View v)
    {

        if(v.getId() == R.id.send_file_button)
        {
            Log.d(TAG, "Attempting to send message");
            ((MainActivity)getActivity()).sendTestMessage();
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
