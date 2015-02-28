package edu.uic.ibeis_tourist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import edu.uic.ibeis_tourist.interfaces.LocalDatabaseInterface;
import edu.uic.ibeis_tourist.layout_adapters.MyPicturesListAdapter;
import edu.uic.ibeis_tourist.local_database.LocalDatabase;
import edu.uic.ibeis_tourist.model.PictureInfo;

//TODO Implement RecyclerView instead of ListView
public class MyPicturesActivity extends ActionBarActivity {

    public static final int IMG_REQUESTED_HEIGHT = 200;
    public static final int IMG_REQUESTED_WIDTH = 200;

    private LocalDatabaseInterface localDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pictures);

        localDb = new LocalDatabase();
        localDb.getAllPictures(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_pictures, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:
                return true;

            case R.id.my_pictures_map_view:
                Intent mapViewIntent = new Intent(this, MyPicturesMapActivity.class);
                startActivity(mapViewIntent);

        }
        return super.onOptionsItemSelected(item);
    }

    public void displayPictureInfoList(final List<PictureInfo> pictureInfoList) {
        final ListView listView = (ListView) findViewById(R.id.my_pictures_list_view);
        MyPicturesListAdapter adapter = new MyPicturesListAdapter(pictureInfoList, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myPictureDetailIntent = new Intent(listView.getContext(), MyPictureDetailActivity.class);
                myPictureDetailIntent.putExtra("pictureInfo", pictureInfoList.get(position));
                startActivity(myPictureDetailIntent);
            }
        });
    }
}
