package amit_shekar.assignment.marsplay;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

import amit_shekar.assignment.marsplay.adapter.GridAdapter;
import amit_shekar.assignment.marsplay.database.ImageDatabase;
import amit_shekar.assignment.marsplay.database.ImgDAO;
import amit_shekar.assignment.marsplay.database.ImgEntity;

public class ActivityImageList extends AppCompatActivity {

    private ImageView imageView;
    private RelativeLayout fullImage;
    private GridView imageList;
    private ImageDatabase db;
    private ImgDAO dao;
    private ImgEntity[] img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        imageView = findViewById(R.id.image_view);
        imageList = findViewById(R.id.image_list);
        fullImage = findViewById(R.id.full_img);

        Glide.with(this).load(R.drawable.test).into(imageView);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(imageView.getContext()));
        imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fullImage.setVisibility(View.VISIBLE);
                Glide.with(ActivityImageList.this)
                        .load(img[i].imageUri).into(imageView);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        db= Room.databaseBuilder(this,
                ImageDatabase.class,
                "images").build();
        dao = db.getDao();
        new GetListAsync(dao).execute();

    }

    private void getImages(ImgEntity[] imgs){
        img = imgs;
        imageList.setAdapter(new GridAdapter(this , imgs));
    }

    public void hideImage(View view){
        fullImage.setVisibility(View.GONE);
    }
    class GetListAsync extends AsyncTask<Void, Void, ImgEntity[]> {
        private ImgDAO dao;

        GetListAsync(ImgDAO dao) {
            this.dao = dao;
        }

        @Override
        protected ImgEntity[] doInBackground(Void... voi) {
            return dao.getAllImg();
        }

        @Override
        protected void onPostExecute(ImgEntity[] imgEntities) {
            super.onPostExecute(imgEntities);
            getImages(imgEntities);
        }
    }
}
