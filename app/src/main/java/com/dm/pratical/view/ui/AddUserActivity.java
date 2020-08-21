package com.dm.pratical.view.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dm.pratical.BuildConfig;
import com.dm.pratical.R;
import com.dm.pratical.db.model.Converters;
import com.dm.pratical.db.model.Photo;
import com.dm.pratical.db.model.User;
import com.dm.pratical.utils.AppPreferences;
import com.dm.pratical.utils.Constants;
import com.dm.pratical.utils.Utils;
import com.dm.pratical.utils.dragHelper.OnStartDragListener;
import com.dm.pratical.utils.dragHelper.SimpleItemTouchHelperCallback;
import com.dm.pratical.view.adapter.PhotoListAdapter;
import com.dm.pratical.viewmodel.UserViewModel;
import com.jakewharton.rxbinding2.widget.RxRadioGroup;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function5;
import io.reactivex.observers.DisposableObserver;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener, OnStartDragListener, Constants {
    private AddUserActivity mActivity;

    //rxjava
    private Observable<Boolean> observable;
    private CompositeDisposable disposable;

    //view
    private EditText editUserName, editUserDOB, editUserPhone, editUserEmail;
    private Button btnCamera, btnGallery, btnSubmit;
    private RadioGroup rgGender;
    private ImageView ivCalendar, ivEdit;
    private RecyclerView rvPhotos;
    private RecyclerView.LayoutManager mLayoutManager;

    //data
    private User mUser;
    private int mStatus;    //0=add,1=view,2=edit
    private Uri mImageUri;
    private String URL_IMAGE = "";
    private File photo;
    private ArrayList<Photo> photoList;
    private PhotoListAdapter photoListAdapter;
    private boolean isPendingUser;
    private boolean isFirstTime = true, isSubmitClicked = false;
    private String currBtnClicked = "";
    private AppPreferences mPrefs;

    //draggable
    private ItemTouchHelper mItemTouchHelper;

    //mvvm
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        mActivity = this;

        setViewModel();
        init();
        getIntentData();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isSubmitClicked) savePendingUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotToListActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        userViewModel.clear();
    }


    //methods
    private void init() {
        editUserName = findViewById(R.id.editUserName);
        editUserPhone = findViewById(R.id.editUserPhone);
        editUserDOB = findViewById(R.id.editUserDOB);
        editUserEmail = findViewById(R.id.editUserEmail);
        rgGender = findViewById(R.id.rgGender);
        ivCalendar = findViewById(R.id.ivCalendar);
        ivEdit = findViewById(R.id.ivEdit);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnSubmit = findViewById(R.id.btnSubmit);
        rvPhotos = findViewById(R.id.rvPhotos);

        btnSubmit.setEnabled(false);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivCalendar.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        if (mStatus == KEY_STATUS_ADD) ivEdit.setVisibility(View.GONE);
        else btnSubmit.setVisibility(View.VISIBLE);
        photoList = new ArrayList<>();
        mPrefs = new AppPreferences(mActivity);
        setRecylerView();

        Observable<String> nameObservable = RxTextView.textChanges(editUserName).skip(1).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                Utils.printLogs(mActivity.getLocalClassName(), charSequence.toString());
                mUser.setName(charSequence.toString());
                isValidManually();
                return charSequence.toString();
            }
        });

        Observable<Date> DOBObservable = RxTextView.textChanges(editUserDOB).skip(1).map(new Function<CharSequence, Date>() {
            @Override
            public Date apply(CharSequence charSequence) throws Exception {
                mUser.setDob(Utils.sdf.parse(charSequence.toString()));
                isValidManually();
                return Utils.sdf.parse(charSequence.toString());
            }
        });

        Observable<String> phoneObservable = RxTextView.textChanges(editUserPhone).skip(1).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                mUser.setPhone(charSequence.toString());
                isValidManually();
                return charSequence.toString();
            }
        });


        Observable<Boolean> genderObservable = RxRadioGroup.checkedChanges(rgGender).skip(1).map(new Function<Integer, Boolean>() {
            @Override
            public Boolean apply(Integer integer) throws Exception {
                return integer == 1;
            }
        });

        Observable<String> emailObservable = RxTextView.textChanges(editUserEmail).skip(1).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                mUser.setEmail(charSequence.toString());
                isValidManually();
                return charSequence.toString();
            }
        });

        observable = Observable.combineLatest(nameObservable, DOBObservable, genderObservable, phoneObservable, emailObservable, new Function5<String, Date, Boolean, String, String, Boolean>() {
            @Override
            public Boolean apply(String s, Date date, Boolean aBoolean, String s2, String s3) throws Exception {
                return isValidForm(s, date, aBoolean, s2, s3, true);
            }
        });

        observable.subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                updateButton(aBoolean);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void setRecylerView() {
        mLayoutManager = new GridLayoutManager(mActivity, 3, RecyclerView.VERTICAL, false);

        rvPhotos.setLayoutManager(mLayoutManager);
        rvPhotos.setItemAnimator(new DefaultItemAnimator());
        rvPhotos.setHasFixedSize(true);
        photoListAdapter = new PhotoListAdapter(mActivity, photoList, this);
        rvPhotos.setAdapter(photoListAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(photoListAdapter);

        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvPhotos);
    }

    //get data from previous activity
    private void getIntentData() {
        mStatus = getIntent().getExtras().getInt(KEY_STATUS);
        if (mStatus != KEY_STATUS_ADD) {
            isPendingUser = false;
            mUser = (User) getIntent().getSerializableExtra(KEY_DATA);
//            photoList = mUser.getPhotos();
            Utils.printLogs("photos", "" + mUser.getPhotos().size());
            setPreData();

        } else {
            //get pending user from view model
            mUser = mPrefs.getPendingUser();
            setPreData();
        }
    }

    private void setPreData() {
        if (mUser != null) {
            editUserName.setText(mUser.getName());
            if (mUser.getDob() != null) editUserDOB.setText(Utils.sdf.format(mUser.getDob()));
            editUserPhone.setText(mUser.getPhone());
            editUserEmail.setText(mUser.getEmail());
            if (mUser.getGender().equals("Male")) rgGender.check(R.id.rbMale);
            else rgGender.check(R.id.rbFemale);

            Utils.printLogs("dbPhotos", "" + mUser.getPhotos().size());
            if (mUser.getPhotos().size() > 0) {
                photoList.addAll(mUser.getPhotos());
                setRecylerView();
            }

        } else {
            mUser = new User();
        }
        handleEditMode();
    }

    private void handleEditMode() {
        if (mStatus == KEY_STATUS_VIEW) {
            ivEdit.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);

            editUserName.setEnabled(false);
            editUserPhone.setEnabled(false);
            editUserEmail.setEnabled(false);
            ivCalendar.setEnabled(false);
            btnCamera.setEnabled(false);
            btnGallery.setEnabled(false);
            for (int i = 0; i < rgGender.getChildCount(); i++) {
                rgGender.getChildAt(i).setEnabled(false);
            }

        } else {
            ivEdit.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);

            editUserName.setEnabled(true);
            editUserPhone.setEnabled(true);
            editUserEmail.setEnabled(true);
            ivCalendar.setEnabled(true);
            btnCamera.setEnabled(true);
            btnGallery.setEnabled(true);
            for (int i = 0; i < rgGender.getChildCount(); i++) {
                rgGender.getChildAt(i).setEnabled(true);
            }
        }
    }


    private void setViewModel() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    private void savePendingUser() {
        //save data temporary
        if (mStatus == KEY_STATUS_ADD && mUser != null) {
            mUser.setPending(true);
            mUser.setPhotos(photoList);
            if (mUser.getGender() == null) {
                mUser.setGender("Male");
            }
            if (mUser.getEmail() == null) {
                mUser.setEmail("xxxxxxxxxx");
            } else {
//                deleteUser(mUser);
            }
//            createUser(mUser);
            mPrefs.setPendingUser(mUser);
        }
    }

    private void isValidManually() {
        String name = editUserName.getText().toString();
        Date dob = Converters.toDate(editUserDOB.getText().toString());
        String phone = editUserPhone.getText().toString();
        String email = editUserEmail.getText().toString();

        updateButton(isValidForm(name, dob, false, phone, email, false));
    }

    private boolean isValidForm(String name, Date dob, boolean a, String phone, String email, boolean setError) {
        Utils.printLogs("isValidForm", "isValidForm");
        boolean validName = !name.isEmpty();
        boolean validPhone = !phone.isEmpty() && phone.length() == 10;
        boolean validEmail = !email.isEmpty() && Utils.isEmail(email);
        boolean validDOB = dob != null && Utils.isValidAge(dob);
        RadioButton rb = (RadioButton) findViewById(rgGender.getCheckedRadioButtonId());

        if (setError) {
            if (!validName)
                editUserName.setError(getResources().getString(R.string.error_empty_name));
            else removeError(editUserName);

            if (!validDOB)
                editUserDOB.setError(getResources().getString(R.string.error_invalid_dob));
            else removeError(editUserDOB);

            if (!validPhone)
                editUserPhone.setError(getResources().getString(R.string.error_invalid_phone));
            else removeError(editUserPhone);

            if (!validEmail)
                editUserEmail.setError(getResources().getString(R.string.error_invalid_email));
            else removeError(editUserEmail);
        }

        if (validEmail && validDOB && validPhone && validEmail && photoList.size() > 0) {
            mUser = new User(0, name, dob, rb.getText().toString(), phone, email, photoList, mStatus == KEY_STATUS_ADD);
            return true;
        }


        return false;
    }


    private void addPhoto(String path) {
        if (photoList.size() < 5) {
            Photo photo = new Photo(photoList.size() + 1, path, photoList.size() + 1);
            photoList.add(photo);
            photoListAdapter.notifyDataSetChanged();
        } else {
            btnCamera.setEnabled(false);
            btnGallery.setEnabled(false);
            Toast.makeText(mActivity, getResources().getString(R.string.error_upload_photos_limit), Toast.LENGTH_LONG).show();
        }
        isValidManually();
    }

    private void removeError(EditText editText) {
        editText.setError(null);
    }

    private void updateButton(boolean valid) {
        btnSubmit.setEnabled(valid);
    }


    private void createUser(User user) {
        userViewModel.createUser(user);
    }

    private void updateUser(User user) {
        userViewModel.updateUser(user);
    }

    private void deleteUser(final User user) {
        userViewModel.deleteUser(user);
    }

//    private void deletePendingUsers() {
////        userViewModel.deletePendingUsers();
////    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEdit: {
                //enable all inputs
                mStatus = KEY_STATUS_EDIT;
//                btnSubmit.setEnabled(true);
                handleEditMode();
                break;
            }
            case R.id.ivCalendar: {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                editUserDOB.setText(String.format(getResources().getString(R.string.lbl_date), Utils.convertIntoTwoDigit(dayOfMonth), Utils.convertIntoTwoDigit(monthOfYear + 1), year));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            }
            case R.id.btnCamera: {
                currBtnClicked = "camera";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkPermission();
                else choseFromCamera();
                break;
            }
            case R.id.btnGallery: {
                currBtnClicked = "gallery";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkPermission();
                else chooseFromSD();
                break;
            }
            case R.id.btnSubmit: {
                isSubmitClicked = true;
                mUser.setPending(false);
                mPrefs.deletePendingUser();
                if (mStatus == KEY_STATUS_ADD) createUser(mUser);
                else updateUser(mUser);
                gotToListActivity();
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.IMAGE_PICK:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
//                    URL_IMAGE = filePath;
                    addPhoto(filePath);
                    break;
                case Constants.IMAGE_CAPTURE:
                    URL_IMAGE = photo.getAbsolutePath();
                    addPhoto(photo.getPath());
                    break;
                default:
                    break;
            }
        }
    }

    private void choseFromCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        try {
            photo = Utils.createTemporaryFile("picture", ".jpg");
            photo.delete();
        } catch (Exception e) {
            Toast.makeText(mActivity, getResources().getString(R.string.error_toast_imge_capture), Toast.LENGTH_LONG).show();
        }
        mImageUri = Uri.fromFile(photo);
        mImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    private void chooseFromSD() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.lbl_choose)), IMAGE_PICK);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (mStatus == KEY_STATUS_EDIT) isValidManually();
        if (mStatus != KEY_STATUS_VIEW) mItemTouchHelper.startDrag(viewHolder);

    }


    //Camera Gallery permissions
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        String[] perms = {CAMERA, READ_EXTERNAL_STORAGE};
        requestPermissions(perms, REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS_CAMERA) {
            int hasCameraPermission = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA);
            int hasStoragePermission = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasCameraPermission == PackageManager.PERMISSION_GRANTED && hasStoragePermission == PackageManager.PERMISSION_GRANTED) {
                if (currBtnClicked.equals("camera")) choseFromCamera();
                else chooseFromSD();
                //data
            } else {
                //
            }
            currBtnClicked = "";
        }

    }

    public void gotToListActivity() {
        Intent i = new Intent(mActivity, UserListActivity.class);
        startActivity(i);
        finish();
    }
}
