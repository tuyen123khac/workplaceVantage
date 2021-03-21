package com.tuyenvo.tmavantage.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.tuyenvo.tmavantage.adapters.AllUsersAdapter;
import com.tuyenvo.tmavantage.models.User;
import com.tuyenvo.tmavantage.R;
import com.tuyenvo.tmavantage.utilities.Constants;
import com.tuyenvo.tmavantage.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements SearchView.OnQueryTextListener {

    PreferenceManager preferenceManager;
    FirebaseFirestore database;
    List<User> userArrayList;
    AllUsersAdapter allUsersAdapter;
    //SinchClient sinchClient;
    //Call call;
    /*final String APP_KEY = "bf843bff-243e-4ae8-96fa-496a5fd26e21";
    final String APP_SECRET = "MSENQSoD50GJKZEAJ8jR2A==";
    final String ENV_HOST = "clientapi.sinch.com";*/

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_contact, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//Prevent Keyboard push the tab
        preferenceManager = new PreferenceManager(getActivity());// Check cai nay, no xai context cua activity

        /*auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        sinchClient = Sinch.getSinchClientBuilder()
                .context(getContext())
                .userId(firebaseUser.getUid())
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENV_HOST)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportManagedPush(true);
        sinchClient.setSupportPushNotifications(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        sinchClient.start();*/

        userArrayList = new ArrayList<>();
        allUsersAdapter = new AllUsersAdapter(this, userArrayList);
        fetchAllUsers();
        //setUpRecyclerView(fragmentView);
        //setUpSearchView(fragmentView);
        return fragmentView;
    }

    private void setUpRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(allUsersAdapter);
    }

    private void fetchAllUsers(){
        database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String myUserID = preferenceManager.getString(Constants.KEY_USER_ID);
                        if(task.isSuccessful() && task.getResult() != null){
                            userArrayList.clear();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                if(myUserID.equals(documentSnapshot.getId())){
                                    continue;
                                }
                                User user = new User(documentSnapshot.getId(),documentSnapshot.getString(Constants.KEY_USERNAME),documentSnapshot.getString(Constants.KEY_PASSWORD));
                                userArrayList.add(user);
                            }
                            allUsersAdapter.setUserArrayListFull();
                            allUsersAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


/*        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userArrayList.add(user);
                }
                allUsersAdapter.setUserArrayListFull();
                allUsersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

   /* private class SinchCallListener implements CallListener{
        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(getContext(), "Ringing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEstablished(Call call) {
            Toast.makeText(getContext(), "Call is established", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEnded(Call endedCall) {
            Toast.makeText(getContext(), "Call is ended", Toast.LENGTH_SHORT).show();
            call = null;
            endedCall.hangup();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    private void setUpSearchView(View view){
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(this);
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        allUsersAdapter.getFilter().filter(newText);
        return false;
    }

   /* // Chua su dung
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchItem.setActionView(searchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }*/
    /*// Chua su dung
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_logout){
            if(firebaseUser != null){
                auth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*private class SinchCallClientListener implements CallClientListener{
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("CALLING");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    call.hangup();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Answer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    incomingCall.answer();
                    incomingCall.addCallListener(new SinchCallListener());
                    Toast.makeText(getContext(), "Call is started", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();
        }
    }

    public void callUser(User user){
        if(call == null){
            call = sinchClient.getCallClient().callUser(user.getId());
            call.addCallListener(new SinchCallListener());
            openCallerDialog(call);
        }
    }

    private void openCallerDialog(Call call) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("ALERT");
        alertDialog.setMessage("CALLING");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hangup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.hangup();
            }
        });
        alertDialog.show();
    }*/
}