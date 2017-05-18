package com.example.android.snapevent;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
                implements MyRecyclerViewAdapter.RecyclerViewButtonClickListener    {
    
    public RecyclerView myRecyclerView;
    static final int MY_PERMISSIONS_REQ_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.v(TAG, " storageDir assigned: " + storageDir);

        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23)    {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)   {
                        dispatchTakePictureIntent();
                    } else  {
                        // permission check not granted
                        // request permission check
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQ_WRITE_EXTERNAL_STORAGE);
                    }
                }
                else    {
                    // SDK < 23. Don't worry about permissions
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myRecyclerView.setAdapter(new MyRecyclerViewAdapter(getImageFileNames()));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)    {
            case MY_PERMISSIONS_REQ_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                    // permission granted
                    dispatchTakePictureIntent();
                } else {
                    // permission denied.
                    Toast.makeText(getApplicationContext(),
                            "Permission denied by user!",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    String TAG = "CAMERA";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent()    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // make sure that some app can indeed handle the take picture intent
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)  {
            // create the file where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException E) {
                // Error occurred while creating the file
                final String TEXT="Error while creating image file! Aborting! ";
                Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
            }

            // continue only if the file was successfully created
            if(photoFile != null)   {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Toast.makeText(getApplicationContext(), "Starting camera", Toast.LENGTH_SHORT).show();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE)    {
            if(resultCode == RESULT_OK) {
                Log.v(TAG, " Inside onActivityResult() RESULT_OK!");

                galleryAddPic();
                List<File> fileNames = getImageFileNames();
                for(File fileName: fileNames)   {
                    Log.v(TAG, "File Name: " + fileName.toString());
                }

                Snackbar detectTextSnackbar = Snackbar.make(
                                                        findViewById(R.id.my_coordinator_layout),
                                                        "Photo saved to gallery!",
                                                        Snackbar.LENGTH_LONG
                );
                detectTextSnackbar.show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.v(TAG, " Inside onActivityResult() RESULT_CANCELED!");
                Toast.makeText(getApplicationContext(),
                        "Photo not taken",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    String mCurrentPhotoPath;
    File storageDir;
    private File createImageFile() throws IOException   {
        // Create an image file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SNAPEVENT_" + timestamp + "_";
        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic()    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(mCurrentPhotoPath));
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private List<File> getImageFileNames()  {
        List<File> inFiles = new ArrayList<>();

        /*  Pics taken by app has the word "SNAPEVENT" in filename.
            I know this is shady.
         */
        if( storageDir.listFiles() != null) {
            for (File file : Arrays.asList(storageDir.listFiles())) {
                if (file.isFile()) {
                    if (file.toString().contains("SNAPEVENT")) {
                        if (file.length() == 0) {
                            boolean delete_status = file.delete();
                        } else {
                            inFiles.add(file);
                        }
                    }
                }
            }
        }
        return inFiles;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent aboutPageIntent = new Intent(this, AboutPageActivity.class);
            startActivity(aboutPageIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onButton1Click(int position) {
        // Responds when button1 is clicked
        // button1 has been temporarily disabled
    }

    @Override
    public void onButton2Click(SparseArray<TextBlock> textBlockSparseArray, int position)   {
        // method in RecyclerViewButtonClickListener

        try {
            List<Line> lines = getLines(textBlockSparseArray);

            Line dateLine = getDateLine(lines);
            Line eventTitle = lines.get(0);
            Line eventLocation = lines.get(lines.size() - 1);

            final int MONTH = parseDate(dateLine, DateType.MONTH);
            final int DAY = parseDate(dateLine, DateType.DAY);
            final int YEAR = parseDate(dateLine, DateType.YEAR);

            Toast.makeText(getApplicationContext(), "Creating calendar event!", Toast.LENGTH_SHORT).show();
            setUpEvent(eventTitle.getValue(), DAY, MONTH, YEAR, eventLocation.getValue(), getEventDescription(lines));
            Toast.makeText(getApplicationContext(), "Kindly verify all event details!", Toast.LENGTH_LONG).show();

        } catch (DateAbsentException E)   {
            Toast.makeText(getApplicationContext(), "Could not detect date!", Toast.LENGTH_LONG)
                    .show();
        } catch (IndexOutOfBoundsException E)   {
            E.printStackTrace();
        }
    }

    List<Line> getLines(SparseArray<TextBlock> tb)  {
        List<Line> lines = new ArrayList<>();

        for (int i = 0; i < tb.size(); i++) {
            TextBlock textBlock = tb.valueAt(i);
            lines.addAll((Collection<? extends Line>) textBlock.getComponents());
        }
        return lines;
    }

    public Line getDateLine(List<Line> lines) throws DateAbsentException {
        /*
            The line that contains the month is assumed to contain the entire date.
            Throw an exception if no lines contain the month; an event is worthless without the month
         */
        for(Line line: lines)   {
            if(hasMonth(line.getValue()))   {
                return line;
            }
        }
        throw new DateAbsentException();
    }

    public int parseDate(final Line dateLine, int resourceType)   {
        final String[] dateLineStrings = dateLine.getValue().toString().split(" ");
        final int EXPECTED_FIELD_COUNT = 3;

        int day = 0;

        switch (resourceType)   {
            case DateType.DAY:
                for(String s: dateLineStrings)  {
                    s = s.replaceAll("([.,;:])", "");
                    try {
                        day = Integer.parseInt(s);
                        if (day >= 1 && day <= 31)  {
                            return day;
                        }
                    } catch (NumberFormatException e)   {
                        // happens on failed attempts to convert non-numerical strings
                        // to integer
                    }
                }
                break;

            case DateType.MONTH:
                for(String dateLineString: dateLineStrings) {
                    if(hasMonth(dateLineString))    {
                        return getMonth(dateLineString);
                    }
                }
                break;

            case DateType.YEAR:
                for(String s: dateLineStrings)  {
                    s = s.replaceAll("([.,;:])", "");
                    try {
                        if (s.matches("^[0-9]{4}$"))  {
                            return Integer.parseInt(s);
                        }
                    } catch (NumberFormatException e)   {
                        // happens on failed attempts to convert non-numerical strings
                        // to integer
                    }
                }

                // if year couldn't be detected from dateline, return current year
                return Calendar.getInstance().get(Calendar.YEAR);
        }
        return DateType.INVALID;
    }

    public boolean hasMonth(String s)    {
        if(s.toLowerCase().contains("jan")) {
            return true;
        } else if(s.toLowerCase().contains("feb"))  {
            return true;
        } else if(s.toLowerCase().contains("mar"))  {
            return true;
        } else if(s.toLowerCase().contains("apr"))  {
            return true;
        } else if(s.toLowerCase().contains("may"))  {
            return true;
        } else if(s.toLowerCase().contains("jun"))  {
            return true;
        } else if(s.toLowerCase().contains("jul"))  {
            return true;
        } else if(s.toLowerCase().contains("aug"))  {
            return true;
        } else if(s.toLowerCase().contains("sep"))  {
            return true;
        } else if(s.toLowerCase().contains("oct"))  {
            return true;
        } else if(s.toLowerCase().contains("nov"))  {
            return true;
        } else if(s.toLowerCase().contains("dec"))  {
            return true;
        } else  {
            return false;
        }
    }

    public int getDay(String s)   {
        int day = 0;
        try {
            day = Integer.parseInt(s);
        } catch (NumberFormatException e)   {
            Log.e(TAG, " getDay(). NumberFormatException " + e.toString());
        }
        return day;
    }

    public int getYear(String s)    {
        int year = 0;
        try {
            year = Integer.parseInt(s);
        } catch (NumberFormatException e)   {
            Log.e(TAG, " getYear(). NumberFormatException " + e.toString());
        }
        return year;
    }

    public int getMonth(String s)    {
        if(s.toLowerCase().contains("jan")) {
            return Calendar.JANUARY;
        } else if(s.toLowerCase().contains("feb"))  {
            return Calendar.FEBRUARY;
        } else if(s.toLowerCase().contains("mar"))  {
            return Calendar.MARCH;
        } else if(s.toLowerCase().contains("apr"))  {
            return Calendar.APRIL;
        } else if(s.toLowerCase().contains("may"))  {
            return Calendar.MAY;
        } else if(s.toLowerCase().contains("jun"))  {
            return Calendar.JUNE;
        } else if(s.toLowerCase().contains("jul"))  {
            return Calendar.JULY;
        } else if(s.toLowerCase().contains("aug"))  {
            return Calendar.AUGUST;
        } else if(s.toLowerCase().contains("sep"))  {
            return Calendar.SEPTEMBER;
        } else if(s.toLowerCase().contains("oct"))  {
            return Calendar.OCTOBER;
        } else if(s.toLowerCase().contains("nov"))  {
            return Calendar.NOVEMBER;
        } else if(s.toLowerCase().contains("dec"))  {
            return Calendar.DECEMBER;
        } else  {
            return Month.INVALID;
        }
    }

    public String getEventDescription(List<Line> lines)    {
        String description = "";
        for(Line line: lines)   {
            description += line.getValue() + "\n";
        }
        return description;
    }

    static final int REQUEST_CREATE_CAL_EVENT = 2;
    public void setUpEvent(String title,
                           final int DAY,
                           final int MONTH,
                           final int YEAR,
                           final String location,
                           String eventDescription) {

        Calendar startTime = Calendar.getInstance();
        startTime.set(YEAR, MONTH, DAY);

        Calendar endTime = Calendar.getInstance();
        endTime.set(YEAR, MONTH, DAY);

        long eventTime1 = startTime.getTimeInMillis();
        long eventTime2 = endTime.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", eventTime1);
        intent.putExtra("endTime", eventTime2);//+60*60*1000);
        intent.putExtra("allDay", true);
        intent.putExtra("title", title);
        intent.putExtra("description", eventDescription);
        intent.putExtra("eventLocation", location);
        startActivity(intent);
    }
}

class DateAbsentException extends Exception {
    DateAbsentException()   {
        super();
    }
}

interface DateType  {
    public static final int DAY = 1;
    public static final int MONTH = 2;
    public static final int YEAR = 3;
    public static final int INVALID = 0;
}

interface Month {
    public static final int JAN = 0;
    public static final int FEB = 1;
    public static final int MAR = 2;
    public static final int APR = 3;
    public static final int MAY = 4;
    public static final int JUN = 5;
    public static final int JUL = 6;
    public static final int AUG = 7;
    public static final int SEP = 8;
    public static final int OCT = 9;
    public static final int NOV = 10;
    public static final int DEC = 11;
    public static final int INVALID = 999;
}