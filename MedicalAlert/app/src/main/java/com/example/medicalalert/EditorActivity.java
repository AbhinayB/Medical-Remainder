package com.example.medicalalert;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.medicalalert.R.id.editText;
import static com.example.medicalalert.R.id.imageView;


public class EditorActivity extends ActionBarActivity {

    private Boolean image_change;
    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;
    private String old_startdate;
    private String old_enddate;
    private String old_time;
    private TextView startdate;
    private TextView enddate;
    private TextView time_foralert;
    private ImageView imgsc;
    private String new_startdate="";
    private String new_enddate="";
    private String new_time="";
    private String note_id_inprogress;
    private final int requestCode = 20;
    String newText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = (EditText) findViewById(editText);
        startdate =(TextView) findViewById(R.id.startdate);
        enddate =(TextView) findViewById(R.id.enddate);
        time_foralert=(TextView) findViewById(R.id.timer);
        imgsc=(ImageView)findViewById(imageView);
        image_change=false;
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd" );
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(MedicProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            startdate.setText( sdf.format( new Date() ));
            enddate.setText(sdf.format( new Date() ));
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.MEDICINE_ID + "=" + uri.getLastPathSegment();

            note_id_inprogress=uri.getLastPathSegment();




            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MEDICINE_DESC));
            old_startdate= cursor.getString(cursor.getColumnIndex(DBOpenHelper.MEDICINE_START));
            old_enddate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MEDICINE_END));
            old_time = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MEDICINE_TIME));

            editor.setText(oldText);
            startdate.setText(old_startdate);
            enddate.setText(old_enddate);
            time_foralert.setText(old_time);
            String _name="Image-"+ note_id_inprogress +".jpg";
            Drawable d = new BitmapDrawable(getApplicationContext().getResources(), BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/medical_images/" + _name));
            imgsc.setImageDrawable(d);

        }
        imgsc.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imgsc.setImageBitmap(bitmap);
            image_change=true;
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void showDatePickerDialog2(View v) {
        DialogFragment newFragment = new DatePickerFragment2();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(MedicProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, getString(R.string.note_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        Intent intent = new Intent(getBaseContext(), RecieveAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(note_id_inprogress), intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        finish();
    }
    private void finishSaving()
    {
        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                } else if (oldText.equals(newText) && old_time.equals(new_time) && old_enddate.equals(new_enddate) && old_startdate.equals(new_startdate) && !image_change) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }

        }
        finish();
    }
    private void finishEditing() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start date can't be past date.");
        builder.setMessage("Please select the present or future date.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("End date can't be past date.");
        builder2.setMessage("Please select the present or future date.");

        builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        newText = editor.getText().toString().trim();
        new_startdate = startdate.getText().toString().trim();
        new_enddate = enddate.getText().toString().trim();
        new_time = time_foralert.getText().toString().trim();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date st_date = new Date();
        Date ed_date = new Date();
        Date cu_date = new Date();
        try {
            cu_date=dateFormat.parse(dateFormat.format(cu_date));
            st_date=dateFormat.parse(new_startdate);
            ed_date=dateFormat.parse(new_enddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (st_date.before(cu_date))
        {
            AlertDialog alert = builder.create();
            alert.show();
        }else if(ed_date.before(cu_date))
        {
            AlertDialog alert = builder2.create();
            alert.show();
        }else
        {
            finishSaving();
        }
    }

    private void updateNote(String noteText) {
        Bitmap bm=((BitmapDrawable)imgsc.getDrawable()).getBitmap();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.MEDICINE_DESC, noteText);
        values.put(DBOpenHelper.MEDICINE_START,new_startdate);
        values.put(DBOpenHelper.MEDICINE_END,new_enddate);
        values.put(DBOpenHelper.MEDICINE_TIME,new_time);
        //values.put(DBOpenHelper.MEDICINE_IMG,"imgsrcccccc");
        getContentResolver().update(MedicProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        SaveImage(bm);
        alarmaction();
    }
    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/medical_images");
        String fname = "Image-"+ note_id_inprogress +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void insertNote(String noteText) {
        Bitmap bm=((BitmapDrawable)imgsc.getDrawable()).getBitmap();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.MEDICINE_DESC, noteText);
        values.put(DBOpenHelper.MEDICINE_START,new_startdate);
        values.put(DBOpenHelper.MEDICINE_END,new_enddate);
        values.put(DBOpenHelper.MEDICINE_TIME,new_time);
        //values.put(DBOpenHelper.MEDICINE_IMG,"imgsrcccccc");
        Uri urit=getContentResolver().insert(MedicProvider.CONTENT_URI, values);
        String id=urit.getLastPathSegment();
        note_id_inprogress=id;
        setResult(RESULT_OK);
        SaveImage(bm);
        alarmaction();
    }
    private void alarmaction()
    {
        String[] tma=new String[2];
        String[] dta=new String[3];
        dta=new_startdate.split("/");
        tma = new_time.split(":");
        Intent alarmIntent = new Intent(EditorActivity.this, RecieveAlarm.class);
        alarmIntent.putExtra("desc_medication",newText);
        alarmIntent.putExtra("id_medication",note_id_inprogress);
        alarmIntent.putExtra("startdate_medication",new_startdate);
        alarmIntent.putExtra("enddate_medication",new_enddate);
        alarmIntent.putExtra("time_medication",new_time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        String dateString = dta[0]+"-"+dta[1]+"-"+dta[2]+" "+tma[0]+":"+tma[1]+":00";
        Date date=new Date();
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());
        //calendar1.set(2018,02,18);
        calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tma[0]));
        calendar1.set(Calendar.MINUTE, Integer.parseInt(tma[1]));
        calendar1.set(Calendar.SECOND,00);
        Long mlsc=(date.getTime()-calendar1.getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditorActivity.this, Integer.parseInt(note_id_inprogress), alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 100;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis()+mlsc,
                AlarmManager.INTERVAL_DAY, pendingIntent);
        //---------------------

        Calendar calender2 = Calendar.getInstance();
        String[] edt=new String[3];
        edt=new_enddate.split("/");
        String dateString2 = edt[0]+"-"+edt[1]+"-"+edt[2]+" "+tma[0]+":"+tma[1]+":59";
        Date date2=new Date();
        try {
            date2 = sdf.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*Log.e("sdsdssdds.......",""+edt[0]+"/"+edt[1]+"/"+edt[2]);
        //calender2.setTimeInMillis(System.currentTimeMillis());
        //calender2.set(Integer.parseInt(edt[0]),Integer.parseInt(edt[1]),Integer.parseInt(edt[2]),Integer.parseInt(tma[0]),Integer.parseInt(tma[1])+2,00);*/
        Intent cancellationIntent = new Intent(this, CancelAlarm.class);
        cancellationIntent.putExtra("key", pendingIntent);
        cancellationIntent.putExtra("desc_medication",newText);
        cancellationIntent.putExtra("id_medication",note_id_inprogress);
        PendingIntent cancellationPendingIntent = PendingIntent.getBroadcast(this, 0, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, date2.getTime()+100000, cancellationPendingIntent);
        //---------------------
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
