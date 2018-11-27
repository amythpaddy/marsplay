package amit_shekar.assignment.marsplay.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import amit_shekar.assignment.marsplay.ActivityImageList;
import amit_shekar.assignment.marsplay.database.ImageDatabase;
import amit_shekar.assignment.marsplay.database.ImgEntity;

public class GridAdapter extends BaseAdapter {
    ImgEntity[] allImg;
    Context mContext;

    public GridAdapter(Context context, ImgEntity[] allImg) {
        this.mContext= context;
        this.allImg =allImg;
    }
    @Override
    public int getCount() {
        return allImg.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imgView;
        if(view == null){
            imgView = new ImageView(mContext);
            imgView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setPadding(8, 8, 8, 8);
            imgView.setElevation(5);
        }else{
            imgView = (ImageView)view;
        }

        Glide.with(mContext).load(allImg[i].imageUri).into(imgView);
        return imgView;
    }
}
