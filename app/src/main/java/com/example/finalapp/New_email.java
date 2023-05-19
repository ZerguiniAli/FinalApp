package com.example.finalapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link New_email#newInstance} factory method to
 * create an instance of this fragment.
 */
public class New_email extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Random random = new Random();

    int randomNum = random.nextInt(90000) + 10000;

    EditText em ;
    Button bt ;

    public New_email() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment New_email.
     */
    // TODO: Rename and change types and number of parameters
    public static New_email newInstance(String param1, String param2) {
        New_email fragment = new New_email();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_email, container, false);

        String username = getArguments().getString("Key");


        em = view.findViewById(R.id.newemail);

        bt = view.findViewById(R.id.button);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = em.getText().toString();
                if(email.isEmpty())
                {
                    Toast.makeText(getActivity(), "please enter ur email address", Toast.LENGTH_SHORT).show();
                }
                else if (isValidEmail(email))
                {
                    sendEmail();
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("email" , email);
                    bundle.putString("random" , String.valueOf(randomNum));

                    Create_confirme_email newCreateConfirmeEmail = new Create_confirme_email();
                    newCreateConfirmeEmail.setArguments(bundle);



                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.newemailbody ,newCreateConfirmeEmail).commit();


                }
                else
                {
                    Toast.makeText(getActivity(), "pleasse enter a valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean isValidEmail(String email) {
        // Regex pattern for email validation
        String emailRegex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    protected static class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        private String recipient;
        private String subject;
        private String message;
        String error ;


        public SendEmailTask(String recipient, String subject, String message) {
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Set up the JavaMail properties.
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            // Set up the email authentication credentials.
            String username = "crops.service@gmail.com";
            String password = "yiyhojhygftuftpr";
            javax.mail.Session session = javax.mail.Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                // Create a new email message.
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(username));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                msg.setSubject(subject);
                msg.setText(message);

                // Send the email message.
                Transport.send(msg);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                error = e.toString();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                //Toast.makeText(getActivity(), "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getActivity(), "Error sending email"+error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendEmail() {
        // Get the recipient, subject, and message from the EditTexts.
        String recipient = em.getText().toString();
        String subject = "CROPS";
        String message = "Your verication code for CROPS is : "+ randomNum;

        // Start the AsyncTask to send the email in the background.
        new SendEmailTask(recipient, subject, message).execute();
    }
}