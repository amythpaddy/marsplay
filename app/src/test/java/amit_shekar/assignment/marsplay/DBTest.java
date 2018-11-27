package amit_shekar.assignment.marsplay;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
//import android.support.test.InstrumentationRegistry;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import amit_shekar.assignment.marsplay.database.ImageDatabase;
import amit_shekar.assignment.marsplay.database.ImgDAO;
import amit_shekar.assignment.marsplay.database.ImgEntity;

public class DBTest {
    private ImageDatabase db;
    private ImgDAO dao;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getContext();
        db = Room.inMemoryDatabaseBuilder(context , ImageDatabase.class).build();
        dao = db.getDao();
    }

    @After
    public void closeDb() throws IOException{
        db.close();
    }

    @Test
    public void writeImage() throws Exception{
        ImgEntity img = new ImgEntity();
        img.imageUri="asdf";
        dao.addImg(img);

        assert dao.getAllImg().length==1;
    }
}
