package amit_shekar.assignment.marsplay.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

@Entity
public class ImgEntity {
    @PrimaryKey
    @NonNull
    public String imageUri;
}
