package amit_shekar.assignment.marsplay;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import amit_shekar.assignment.marsplay.database.ImageDatabase;
import amit_shekar.assignment.marsplay.database.ImgDAO;
import amit_shekar.assignment.marsplay.database.ImgEntity;

public class ActivityMain extends AppCompatActivity {

    Button imageList;
    LinearLayout uploadOptions;
    private Uri imageUri;
    private File storeDir;
    private int CAMERA_REQ_CODE = 1;
    private int GALLERY_REQ_CODE = 2;
    private int WRITE_REQ_CODE = 3;
    private int CROP_REQ_CODE=4;

    ImageDatabase db;
    ImgDAO dao;

    @Override
    protected void onStart() {
        super.onStart();
        db = Room.databaseBuilder(this,
                ImageDatabase.class,
                "images").build();
        dao = db.getDao();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageList = findViewById(R.id.image_list);
        uploadOptions = findViewById(R.id.upload_options);
        imageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageList();
            }
        });
    }

    private void showImageList() {
        startActivity(new Intent(this, ActivityImageList.class));
    }

    public void uploadImg(View view) {
        uploadOptions.setVisibility(View.VISIBLE);
    }

    public void openGallery(View view) {

        uploadOptions.setVisibility(View.GONE);
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_REQ_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            clickPicture();
        }
    }

    public void openCamera(View view) throws IOException {
        uploadOptions.setVisibility(View.GONE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_REQ_CODE);
        } else {
            clickPicture();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQ_CODE) {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            grantUriPermission("com.android.camera",imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            cropIntent.setData(imageUri);
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 250);
            cropIntent.putExtra("outputY", 250);

            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cropIntent, CROP_REQ_CODE);
        } else if (requestCode == GALLERY_REQ_CODE  && resultCode == RESULT_OK) {
            startUpload(data.getData());
        }else if (requestCode == CROP_REQ_CODE && resultCode == RESULT_OK) {

            startUpload(imageUri);
        }
    }

    private void startUpload(Uri data) {
        Toast.makeText(this, "Starting Image Upload...", Toast.LENGTH_SHORT).show();
        saveToDB(data);
        StorageReference store = FirebaseStorage.getInstance().getReference();
        StorageReference upload = store.child(Calendar.getInstance().getTime().toString());
        upload.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ActivityMain.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private File createImage() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "captured" + timeStamp+".jpg";
        storeDir = new File(getFilesDir(), "images/");
        storeDir.mkdirs();
        storeDir = new File(storeDir , imageFileName);
        storeDir.createNewFile();
        return storeDir;
    }

    private void clickPicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "amit_shekar.assignment.marsplay.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQ_CODE);
            }
        }
    }

    private void saveToDB(final Uri imagePath) {
        new Runnable() {
            @Override
            public void run() {

                ImgEntity imgEntity = new ImgEntity();
                imgEntity.imageUri = String.valueOf(imagePath);
                new InsertAsync(dao).execute(imgEntity);
            }
        }.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    static class InsertAsync extends AsyncTask<ImgEntity, Void, Void> {
        private ImgDAO dao;
        InsertAsync(ImgDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ImgEntity... imgEntities) {
            dao.addImg(imgEntities[0]);
            return null;
        }
    }
}
