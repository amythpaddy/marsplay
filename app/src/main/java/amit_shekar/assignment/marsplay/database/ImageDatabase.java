package amit_shekar.assignment.marsplay.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ImgEntity.class}, version =1, exportSchema = false)
public abstract class ImageDatabase extends RoomDatabase {
    public abstract ImgDAO getDao();
}
