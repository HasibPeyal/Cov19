package com.ece.cov19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ece.cov19.DataModels.FindPatientData;
import com.ece.cov19.DataModels.PatientDataModel;
import com.ece.cov19.DataModels.UserDataModel;
import com.ece.cov19.RecyclerViews.DonorAdapter;
import com.ece.cov19.RecyclerViews.ExplorePatientAdapter;
import com.ece.cov19.RecyclerViews.FindPatientAdapter;
import com.ece.cov19.RetroServices.RetroInstance;
import com.ece.cov19.RetroServices.RetroInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ece.cov19.DataModels.LoggedInUserData.loggedInUserPhone;

public class FindDonorActivity extends AppCompatActivity {


    private RecyclerView patientRecyclerView, donorRecyclerView;
    private EditText districtEditText;
    private ProgressBar patientProgressBar, donorProgressBar;
    private TextView patientTextView, donorTextView, filterTextView, noMatchTextView;
    private ImageView backbtn;
    String bloodGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_donor);

        patientRecyclerView = findViewById(R.id.find_donor_forpatients_recyclerview);
        patientProgressBar = findViewById(R.id.find_donor_forpatients_progress_bar);
        patientTextView = findViewById(R.id.find_donor_forpatients_textview);

        donorRecyclerView = findViewById(R.id.find_donor_fordonors_recyclerview);
        donorTextView = findViewById(R.id.find_donor_fordonors_textview);
        noMatchTextView = findViewById(R.id.find_donor_fordonors_nomatchtextview);
        filterTextView = findViewById(R.id.find_donor_fordonors_label_district);
        districtEditText=findViewById(R.id.find_donor_fordonors_district_edittext);
        donorProgressBar =findViewById(R.id.find_donor_fordonors_progress_bar);
        backbtn=findViewById(R.id.find_donor_fordonors_back_button);

        donorTextView.setVisibility(View.GONE);
        filterTextView.setVisibility(View.GONE);
        districtEditText.setVisibility(View.GONE);
        noMatchTextView.setVisibility(View.GONE);

        FindPatientData.findPatientBloodGroup = "any";
        findPatient();
        findDonor();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        districtEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               findDonor();
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    private void findPatient(){
        patientProgressBar.setVisibility(View.VISIBLE);

        ArrayList<PatientDataModel> patientDataModels;
        FindPatientAdapter findPatientAdapter;
        patientDataModels = new ArrayList<>();
        findPatientAdapter = new FindPatientAdapter(getApplicationContext(), patientDataModels, new FindPatientAdapter.RecyclerViewClickListener() {
        @Override
            public void onClicked(View v, int position) {
                Toast.makeText(getApplicationContext(),"Position: "+position,Toast.LENGTH_SHORT).show();
                findDonor();
            }
        });

        RetroInterface retroInterface = RetroInstance.getRetro();
        Call<ArrayList<PatientDataModel>> searchDonor = retroInterface.ownPatients(loggedInUserPhone);
        searchDonor.enqueue(new Callback<ArrayList<PatientDataModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PatientDataModel>> call, Response<ArrayList<PatientDataModel>> response) {




                patientProgressBar.setVisibility(View.GONE);

                patientDataModels.clear();
                if(response.isSuccessful()){
                    ArrayList<PatientDataModel> initialModels = response.body();
                    for(PatientDataModel initialDataModel : initialModels){

                        patientDataModels.add(initialDataModel);

                    }


                    if(patientDataModels.size() > 0){
                        patientTextView.setVisibility(View.VISIBLE);

                    }
                    patientRecyclerView.setAdapter(findPatientAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
                    patientRecyclerView.setLayoutManager(linearLayoutManager);
                }

                else{
                    Toast.makeText(FindDonorActivity.this, "No Response", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<PatientDataModel>> call, Throwable t) {
                Toast.makeText(FindDonorActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }




    private void findDonor() {
        donorProgressBar.setVisibility(View.VISIBLE);
        donorTextView.setVisibility(View.VISIBLE);
        filterTextView.setVisibility(View.VISIBLE);
        districtEditText.setVisibility(View.VISIBLE);
        noMatchTextView.setVisibility(View.GONE);

        ArrayList<UserDataModel> userDataModels;
        DonorAdapter donorAdapter;
        userDataModels = new ArrayList<>();
        donorAdapter = new DonorAdapter(getApplicationContext(),userDataModels);

        bloodGroup = FindPatientData.findPatientBloodGroup;
        String district;

        if(districtEditText.getText().toString().isEmpty()){
            district="any";
        }
        else{
            district=districtEditText.getText().toString();
        }

        userDataModels.clear();
        RetroInterface retroInterface = RetroInstance.getRetro();
        Call<ArrayList<UserDataModel>> findDonor = retroInterface.findDonor(bloodGroup,district);
        findDonor.enqueue(new Callback<ArrayList<UserDataModel>>() {
            @Override
            public void onResponse(Call<ArrayList<UserDataModel>> call, Response<ArrayList<UserDataModel>> response) {
                donorProgressBar.setVisibility(View.GONE);



                if(response.isSuccessful()){
                    ArrayList<UserDataModel> initialModels = response.body();
                    for(UserDataModel initialDataModel : initialModels){

                        userDataModels.add(initialDataModel);

                    }

                    if(userDataModels.size() == 0){
                        donorTextView.setVisibility(View.GONE);
                        filterTextView.setVisibility(View.GONE);
                        districtEditText.setVisibility(View.GONE);
                        noMatchTextView.setVisibility(View.VISIBLE);
                    }

                    donorRecyclerView.setAdapter(donorAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    donorRecyclerView.setLayoutManager(linearLayoutManager);
                }

                else{
                    Toast.makeText(FindDonorActivity.this, "No Response", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserDataModel>> call, Throwable t) {
                Toast.makeText(FindDonorActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}