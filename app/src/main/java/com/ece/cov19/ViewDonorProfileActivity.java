package com.ece.cov19;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ece.cov19.DataModels.RequestDataModel;
import com.ece.cov19.DataModels.UserDataModel;
import com.ece.cov19.RetroServices.RetroInstance;
import com.ece.cov19.RetroServices.RetroInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ece.cov19.DataModels.FindPatientData.findPatientAge;
import static com.ece.cov19.DataModels.FindPatientData.findPatientBloodGroup;
import static com.ece.cov19.DataModels.FindPatientData.findPatientName;
import static com.ece.cov19.DataModels.FindPatientData.findPatientPhone;
import static com.ece.cov19.DataModels.LoggedInUserData.loggedInUserName;
import static com.ece.cov19.DataModels.LoggedInUserData.loggedInUserPass;
import static com.ece.cov19.DataModels.LoggedInUserData.loggedInUserPhone;

public class ViewDonorProfileActivity extends AppCompatActivity {

    private TextView nameTextView, phoneTextView, bloodGroupTextView, addressTextView, ageTextView, donorInfoTextView, sendRequestSuggestion;
    private ImageView genderImageView;
    private Button askForHelpBtn, acceptBtn, declineBtn;
    private ImageView backbtn;
    private ProgressBar progressBar;
    String name, age, bloodGroup, donorphone, donorInfo, address,requestedBy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donor_profile);


        nameTextView = findViewById(R.id.donor_profile_name);
        phoneTextView = findViewById(R.id.donor_profile_phone);
        bloodGroupTextView = findViewById(R.id.donor_profile_blood_group);
        addressTextView = findViewById(R.id.donor_profile_address);
        ageTextView = findViewById(R.id.donor_profile_age);
        donorInfoTextView = findViewById(R.id.donor_profile_type);
        genderImageView = findViewById(R.id.donor_profile_gender_icon);
        askForHelpBtn = findViewById(R.id.donor_profile_ask_for_help_button);
        acceptBtn = findViewById(R.id.donor_profile_accept_button);
        declineBtn = findViewById(R.id.donor_profile_decline_button);
        backbtn = findViewById(R.id.donor_profile_back_button);
        sendRequestSuggestion = findViewById(R.id.donor_profile_send_request_suggestion);
        progressBar = findViewById(R.id.donor_profile_progressBar);


        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        age = intent.getStringExtra("age");
        bloodGroup = intent.getStringExtra("blood");
        donorphone = intent.getStringExtra("phone");
        donorInfo = intent.getStringExtra("donorinfo");
        address = intent.getStringExtra("address");
        if(intent.hasExtra("activity")){
            if(intent.getStringExtra("activity").equals("PatientRequestsActivity")){
                requestedBy="donor";
            }
            else{
                requestedBy="patient";
            }
        }

        nameTextView.setText(name);
        if (donorphone.equals(loggedInUserPhone)) {
            phoneTextView.setText(donorphone);

        } else {
            phoneTextView.setText("Not Permitted!");
        }
        bloodGroupTextView.setText(bloodGroup);
        addressTextView.setText(address);
        ageTextView.setText(age);
        donorInfoTextView.setText(donorInfo);

        if (findPatientName.equals("") && findPatientAge.equals("") && findPatientPhone.equals("") && findPatientBloodGroup.equals("any")) {
            askForHelpBtn.setVisibility(View.GONE);
            sendRequestSuggestion.setVisibility(View.VISIBLE);

        }
        else{
            requestsOperation("getStatus");
        }

        if (intent.getStringExtra("gender").equals("male")) {
            genderImageView.setImageResource(R.drawable.profile_icon_male);
        } else {
            genderImageView.setImageResource(R.drawable.profile_icon_female);

        }



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        askForHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(askForHelpBtn.getText().toString().toLowerCase().equals("ask for help")) {
                    passWordAlertDialog();
                }


            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(acceptBtn.getText().toString().toLowerCase().equals("accept request")) {
                    requestsOperation("accept");

                    //Push Notification


                    RetroInterface retroInterface = RetroInstance.getRetro();
                    Call<UserDataModel> incomingResponse = retroInterface.sendNotification(donorphone,"Request Accepted",loggedInUserName +" has accepted your request. Check Patient Responses.");
                    incomingResponse.enqueue(new Callback<UserDataModel>() {
                        @Override
                        public void onResponse(Call<UserDataModel> call, Response<UserDataModel> response) {

                        }

                        @Override
                        public void onFailure(Call<UserDataModel> call, Throwable t) {

                        }
                    });
                }
                else if(acceptBtn.getText().toString().toLowerCase().equals("call donor")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+donorphone));
                    startActivity(intent);
                }

            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(declineBtn.getText().toString().toLowerCase().equals("decline request")) {
                    requestsOperation("decline");


                    //Push Notification


                    RetroInterface retroInterface = RetroInstance.getRetro();

                    Call<UserDataModel> incomingResponse = retroInterface.sendNotification(donorphone,"Request Declined",loggedInUserName +" has declined your request. Check Patient Responses");
                    incomingResponse.enqueue(new Callback<UserDataModel>() {
                        @Override
                        public void onResponse(Call<UserDataModel> call, Response<UserDataModel> response) {

                        }

                        @Override
                        public void onFailure(Call<UserDataModel> call, Throwable t) {

                        }
                    });
                }
                else if(declineBtn.getText().toString().toLowerCase().equals("send sms")){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("smsto:"));
                    intent.putExtra("address", donorphone);
                    intent.putExtra("sms_body","Hello! I would like to contact with you.");
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void passWordAlertDialog() {

//                asking password with alertdialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(ViewDonorProfileActivity.this);
        builder.setMessage("Enter Password");

// Set up the input
        final EditText pass = new EditText(getApplicationContext());

        float density = getResources().getDisplayMetrics().density;
        int paddingDp = (int) (12 * density);
        pass.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        pass.setHint("******");
        pass.setBackgroundResource(R.drawable.edit_text_dark);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(pass);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (pass.getText().toString().equals(loggedInUserPass)) {

                    sendRequest();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                    ;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void sendRequest() {
        progressBar.setVisibility(View.VISIBLE);
        RetroInterface retroInterface = RetroInstance.getRetro();
        Call<RequestDataModel> requestFromPatient = retroInterface.sendRequest(donorphone, findPatientName, findPatientAge, findPatientPhone, findPatientBloodGroup, "patient");
        requestFromPatient.enqueue(new Callback<RequestDataModel>() {
            @Override
            public void onResponse(Call<RequestDataModel> call, Response<RequestDataModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body().getServerMsg().equals("Success")) {
                    Toast.makeText(ViewDonorProfileActivity.this, "Request Sent! Wait For Donor's Response", Toast.LENGTH_SHORT).show();




                    //Push Notification


                    RetroInterface retroInterface = RetroInstance.getRetro();
                    Call<UserDataModel> incomingResponse = retroInterface.sendNotification(donorphone,"Incoming Request from Patient",findPatientName +" has sent you a request. Check Donor Requests.");
                    incomingResponse.enqueue(new Callback<UserDataModel>() {
                        @Override
                        public void onResponse(Call<UserDataModel> call, Response<UserDataModel> response) {

                        }

                        @Override
                        public void onFailure(Call<UserDataModel> call, Throwable t) {

                        }
                    });



                } else {
                    Toast.makeText(ViewDonorProfileActivity.this, response.body().getServerMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RequestDataModel> call, Throwable t) {
                Toast.makeText(ViewDonorProfileActivity.this, t.toString(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void requestsOperation(String operation) {
        progressBar.setVisibility(View.VISIBLE);
        RetroInterface retroInterface = RetroInstance.getRetro();
        //Toast.makeText(this, donorphone + findPatientName + findPatientAge + findPatientBloodGroup + findPatientPhone, Toast.LENGTH_SHORT).show();
        Call<RequestDataModel> lookforRequestFromPatient = retroInterface.requestsOperation(donorphone, findPatientName, findPatientAge, findPatientBloodGroup, findPatientPhone, requestedBy,operation);
        lookforRequestFromPatient.enqueue(new Callback<RequestDataModel>() {
            @Override
            public void onResponse(Call<RequestDataModel> call, Response<RequestDataModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if(response.body().getServerMsg().equals("no requests")) {
                        if(donorphone.equals(loggedInUserPhone)){
                            askForHelpBtn.setVisibility(View.GONE);
                            acceptBtn.setVisibility(View.GONE);
                            declineBtn.setVisibility(View.GONE);
                        } else {
                            askForHelpBtn.setVisibility(View.VISIBLE);
                            askForHelpBtn.setText("Ask for Help");
                            acceptBtn.setVisibility(View.GONE);
                            declineBtn.setVisibility(View.GONE);
                        }
                    }
                    else if (response.body().getServerMsg().equals("Pending")) {
                        if(getIntent().getStringExtra("activity").equals("DonorResponseActivity")){
                            askForHelpBtn.setVisibility(View.VISIBLE);
                            askForHelpBtn.setText("Pending");
                        }
                        else {
                            askForHelpBtn.setVisibility(View.GONE);
                            acceptBtn.setVisibility(View.VISIBLE);
                            acceptBtn.setText("Accept Request");
                            declineBtn.setVisibility(View.VISIBLE);
                            declineBtn.setText("Decline Request");
                        }

                    }
                    else if (response.body().getServerMsg().equals("Accepted")) {
                        askForHelpBtn.setVisibility(View.GONE);
                        acceptBtn.setVisibility(View.VISIBLE);
                        acceptBtn.setText("Call Donor");
                        declineBtn.setVisibility(View.VISIBLE);
                        declineBtn.setText("Send SMS");
                        phoneTextView.setText(donorphone);

                    }
                    else if (response.body().getServerMsg().equals("Declined")) {
                            askForHelpBtn.setVisibility(View.VISIBLE);
                            askForHelpBtn.setText("Declined");
                            acceptBtn.setVisibility(View.GONE);
                            declineBtn.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onFailure(Call<RequestDataModel> call, Throwable t) {
                Toast.makeText(ViewDonorProfileActivity.this, t.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
