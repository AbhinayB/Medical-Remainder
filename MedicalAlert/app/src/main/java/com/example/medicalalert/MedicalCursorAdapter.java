package com.example.medicalalert;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MedicalCursorAdapter extends CursorAdapter{
    public MedicalCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.note_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String meddecr = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.MEDICINE_DESC));
        String medstrt = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.MEDICINE_START));
        String medendt = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.MEDICINE_END));
        String medtime = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.MEDICINE_TIME));
        String medid = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.MEDICINE_ID));
        int pos = meddecr.indexOf(10);
        if (pos != -1) {
            meddecr = meddecr.substring(0, pos) + " ...";
        }

        TextView dec = (TextView) view.findViewById(R.id.tvNote);
        TextView st = (TextView) view.findViewById(R.id.start);
        TextView ed = (TextView) view.findViewById(R.id.end);
        TextView tm = (TextView) view.findViewById(R.id.time);
        String _name="Image-"+ medid +".jpg";
        ImageView imv=(ImageView) view.findViewById(R.id.imageDocIcon);
        dec.setText(meddecr);
        st.setText(medstrt);
        ed.setText(medendt);
        tm.setText(medtime);
        ArrayList img_path = new ArrayList<String>();
        File cacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/medical_images");
        File[] file_array = cacheDir.listFiles();
        for (int i = 0; i < file_array.length; i++) {
            img_path.add(file_array[i].getName());
        }

        if (img_path.contains(_name)) {
            Drawable d = new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/medical_images/" + _name));
            imv.setImageDrawable(d);
        } else {
        }



    }
}
