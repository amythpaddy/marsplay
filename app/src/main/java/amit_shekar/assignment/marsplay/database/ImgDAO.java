package amit_shekar.assignment.marsplay.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface ImgDAO {
    @Insert
    public void addImg(ImgEntity img);

    @Query("SELECT * FROM ImgEntity")
    public ImgEntity[] getAllImg();
}
