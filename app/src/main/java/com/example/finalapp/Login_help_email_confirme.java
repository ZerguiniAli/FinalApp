package com.example.finalapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login_help_email_confirme#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login_help_email_confirme extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    
    EditText et1,et2 ,et3 ,et4,et5;
    Button bt;

    public Login_help_email_confirme() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login_help_email_confirme.
     */
    // TODO: Rename and change types and number of parameters
    public static Login_help_email_confirme newInstance(String param1, String param2) {
        Login_help_email_confirme fragment = new Login_help_email_confirme();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        View view = inflater.inflate(R.layout.fragment_login_help_email_confirme, container, false);
        et1 = view.findViewById(R.id.c1);
        et2 = view.findViewById(R.id.c2);
        et3 = view.findViewById(R.id.c3);
        et4 = view.findViewById(R.id.c4);
        et5 = view.findViewById(R.id.c5);
        bt = view.findViewById(R.id.button);
        String code = getArguments().getString("code");
        String email = getArguments().getString("email");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vcode = et1.getText().toString() +
                        et2.getText().toString()+
                        et3.getText().toString()+
                        et4.getText().toString()+
                        et5.getText().toString();
                if (vcode.equals(code))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    New_Password newPassword = new New_Password();
                    newPassword.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.loginhelpcomfirme ,newPassword).commit();

                }
                else
                {
                    Toast.makeText(getActivity(), "wrong code", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}