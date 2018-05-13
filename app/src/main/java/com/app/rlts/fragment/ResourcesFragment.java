package com.app.rlts.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.rlts.R;
import com.app.rlts.entity.Beacon;
import com.app.rlts.entity.StateVO;
import com.app.rlts.interfaces.AsyncResponse;
import com.app.rlts.manager.SessionManager;
import com.app.rlts.manager.SpinnerAdapter;
import com.app.rlts.task.AsyncGetBeaconsTask;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class ResourcesFragment extends Fragment implements AsyncResponse {

    View inflateView;
    SessionManager session;
    Button sendButton;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    ArrayList<Beacon> beaconArray = new ArrayList<>();

    EditText notif_title;
    EditText notif_body;

    Spinner spinner;
    SpinnerAdapter myAdapter;

    Button attachmentButton;

    GoogleSignInClient mGoogleSignInClient;
    DriveClient mDriveClient;
    DriveResourceClient mDriveResourceClient;

    public static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int PICKFILE_RESULT_CODE = 1;
    public static final int REQUEST_CODE_CREATOR = 2;

    String filepath;
    String filename;
    File file;
    Uri uri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        inflateView = inflater.inflate(R.layout.fragment_resources, container, false);

        session = new SessionManager(getActivity().getApplicationContext());

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        new AsyncGetBeaconsTask(this).execute();

        notif_title = (EditText) inflateView.findViewById(R.id.resources_title);
        spinner = (Spinner) inflateView.findViewById(R.id.spinner_location_resources);
        notif_body = (EditText) inflateView.findViewById(R.id.resources_body);

        attachmentButton = (Button) inflateView.findViewById(R.id.attachment_button);

        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("*/*");
                startActivityForResult(
                        Intent.createChooser(chooseFile, "Choose a file"), PICKFILE_RESULT_CODE);
            }
        });

        mGoogleSignInClient = buildGoogleSignInClient();

        sendButton = (Button) inflateView.findViewById(R.id.resources_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                calendar = Calendar.getInstance();
//                String date = dateFormat.format(calendar.getTime());
//                String time = dateFormat.format(calendar.getTime());
//
//                String title = notif_title.getText().toString();
//                ArrayList<String> locations = myAdapter.getListState();
//                String body = notif_body.getText().toString();
//
//                HashMap<String, String> user = session.getUserDetails();
//                String username = user.get(SessionManager.KEY_NAME);
//
//                WebNotification notification = new WebNotification(date, time, title, locations, body, username);
//                new AsyncSendNotificationTask(notification).execute();
//
//                new AsyncGetBeaconsTask(ResourcesFragment.this).execute();
//
//                notif_title.getText().clear();
//                notif_body.getText().clear();
//
//                Toast.makeText(getActivity(), R.string.resource_sent, Toast.LENGTH_LONG).show();

//                Task<GoogleSignInAccount> task = mGoogleSignInClient.silentSignIn();
//                updateViewWithGoogleSignInAccountTask(task);

                startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
            }
        });

        return inflateView;

    }

    private void createFile() {
        // [START create_file]
        final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = rootFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();

                        OutputStream outputStream = contents.getOutputStream();
                        //InputStream in = new FileInputStream(file);
                        InputStream in = getContext().getContentResolver().openInputStream(uri);


                        try {
                            try {
                                // Transfer bytes from in to out
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    outputStream.write(buf, 0, len);
                                }
                            } catch (Exception e){
                                Toast.makeText(getActivity(), "New contents created.", Toast.LENGTH_LONG).show();
                            }finally {
                                outputStream.close();
                            }
                        }catch (Exception e){
                            Toast.makeText(getActivity(), "New contents created2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        finally {
                            in.close();
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(filename)
                                .setStarred(true)
                                .build();

                        return mDriveResourceClient.createFile(parent, changeSet, contents);
                    }
                });
        // [END create_file]
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CODE_SIGN_IN:

                Log.i(TAG, "Sign in request code");
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Signed in successfully.");
                    // Use the last signed in account here since it already have a Drive scope.
                    mDriveClient = Drive.getDriveClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getContext()));
                    // Build a drive resource client.
                    mDriveResourceClient =
                            Drive.getDriveResourceClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getContext()));
                    // Start camera.
//                    startActivityForResult(
//                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
                    Toast.makeText(getActivity(), "Signed in successfully.", Toast.LENGTH_LONG).show();
                    //saveFileToDrive();
                    createFile();
                }
                break;

            case PICKFILE_RESULT_CODE:

                Uri content_describer = data.getData();
                uri = data.getData();
                filepath = content_describer.getPath();
                file = new File(filepath);
                filename = getFileName(uri);

                Toast.makeText(getActivity(), filepath, Toast.LENGTH_LONG).show();
                break;

            case REQUEST_CODE_CREATOR:

                Log.i(TAG, "creator request code");
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    Toast.makeText(getActivity(), "File successfully saved.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(getActivity(), signInOptions);
    }

    @Override
    public void retrieveBeacons(ArrayList<Beacon> bList) {

        try{
            beaconArray.clear();
            for (int i = 0; i < bList.size(); i++) {
                if(!(bList.get(i).getLocationName().equalsIgnoreCase("NA"))){
                    this.beaconArray.add(bList.get(i));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }

        ArrayList<StateVO> listVOs = new ArrayList<>();

        StateVO header = new StateVO();
        header.setLocation(getString(R.string.send_to_location));
        header.setSelected(false);
        listVOs.add(header);

        for (int i = 0; i < beaconArray.size(); i++) {
            StateVO stateVO = new StateVO();
            stateVO.setLocation(beaconArray.get(i).getLocationName());
            stateVO.setId(beaconArray.get(i).getBeaconId());
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }

        myAdapter = new SpinnerAdapter(getActivity(), 0, listVOs);
        spinner.setAdapter(myAdapter);
    }
}
