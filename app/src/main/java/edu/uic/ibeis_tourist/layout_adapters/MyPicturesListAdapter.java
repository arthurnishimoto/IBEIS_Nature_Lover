package edu.uic.ibeis_tourist.layout_adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import edu.uic.ibeis_tourist.MyPicturesActivity;
import edu.uic.ibeis_tourist.R;
import edu.uic.ibeis_tourist.model.PictureInfo;
import edu.uic.ibeis_tourist.utils.DateTimeUtils;
import edu.uic.ibeis_tourist.utils.FileUtils;

public class MyPicturesListAdapter extends ArrayAdapter<PictureInfo> {

    private Context mContext;
    private List<PictureInfo> mPictureInfoList;

    public MyPicturesListAdapter(List<PictureInfo> pictureInfoList, Context context) {
        super(context, R.layout.my_pictures_list_item, pictureInfoList);
        mContext = context;
        mPictureInfoList = pictureInfoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.my_pictures_list_item, null);

        ImageView picture = (ImageView) rowView.findViewById(R.id.row_picture);
        TextView nameText = (TextView) rowView.findViewById(R.id.row_name);
        TextView speciesText = (TextView) rowView.findViewById(R.id.row_species);
        TextView dateText = (TextView) rowView.findViewById(R.id.row_datetime);
        TextView locationText = (TextView) rowView.findViewById(R.id.row_location);

        try {

            picture.setImageBitmap(
                    FileUtils.getImageBitmap(
                            mPictureInfoList.get(position).getFileName(),
                            MyPicturesActivity.IMG_REQUESTED_HEIGHT, MyPicturesActivity.IMG_REQUESTED_WIDTH, Bitmap.Config.RGB_565));


        } catch (IOException e) {
            //TODO handle exception: remove image entry from database
            e.printStackTrace();
        } catch (NullPointerException e) {
            //TODO handle exception: remove image entry from database
            e.printStackTrace();
            System.out.println(mPictureInfoList.get(position).getFileName());
        }

        nameText.setText("Name: " + mPictureInfoList.get(position).getIndividualName());
        speciesText.setText("Species: " + mPictureInfoList.get(position).getIndividualSpecies());
        dateText.setText("When: " + DateTimeUtils.presentationFormat(mPictureInfoList.get(position).getDateTime()));
        locationText.setText("Where: " + "(" + mPictureInfoList.get(position).getPosition().latitude + ", " +
        mPictureInfoList.get(position).getPosition().longitude + ")");

        return rowView;
    }
}
