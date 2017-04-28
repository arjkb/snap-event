package com.example.android.snapevent;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;

public class MainActivity extends AppCompatActivity
                implements MyRecyclerViewAdapter.RecyclerViewButtonClickListener,
                            CreateEventDialogFragment.CreateEventDialogListener{
    
    public RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myRecyclerView.setAdapter(new MyRecyclerViewAdapter(getImageFileNames()));
    }

    private String[] getDummyText(int size) {
        List<String> dummyString = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
             dummyString.add(" Dummy string " + i);
        }
        return (String []) dummyString.toArray(new String[0]);
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
                Log.v(TAG, "Created image file! " + photoFile.toString());
            } catch (IOException E) {
                // Error occurred while creating the file
                final String TEXT="Error while creating image file! Aborting!";
                Log.v(TAG, TEXT);
                Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();

            }

            // continue only if the file was succesfully created
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
                Log.v(TAG, " Inside onActivityResult() after taking picture!");
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

//                Toast.makeText(getApplicationContext(),
//                        "Photo available in the gallery",
//                        Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.v(TAG, " Inside onActivityResult() after taking picture!");
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
        // Create an image file naem
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SNAPEVENT_" + timestamp + "_";
        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.v(TAG, "storageDir: " + storageDir.toString());
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic()    {
        Log.v(TAG, " Inside galleryAddPic()");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(mCurrentPhotoPath));
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private List<File> getImageFileNames()  {
        List<File> inFiles = new ArrayList<>();

        Log.v(TAG, " Inside getImageFileNames()!");

        /*  Pics taken by app has the word "SNAPEVENT" in filename.
            I know this is shady.
         */
        for (File file: Arrays.asList(storageDir.listFiles()) )  {
            Log.v(TAG, " Inside GFN() Looping!!" + file.toString());
            if (file.isFile())   {
                Log.v(TAG, " Inside GFN() isFile()");
                if (file.toString().contains("SNAPEVENT"))    {
                    Log.v(TAG, " Inside GFN() Adding File!!");
                    inFiles.add(file);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onButton1Click(int position) {
        // method in RecyclerViewButtonClickListener
        Log.v(TAG, " MA: Pressed button 1 at position " + position);
    }

    @Override
    public void onButton2Click(SparseArray<TextBlock> textBlockSparseArray, int position)   {
        // method in RecyclerViewButtonClickListener

        List<Line> lines = getLines(textBlockSparseArray);
        for(Line line: lines)   {
            Log.v(TAG, "Line: " + line.getValue());
        }

        setUpEvent("10 April 2017", 4, "Foo Event", "New York");
    }

    List<Line> getLines(SparseArray<TextBlock> tb)  {
        List<Line> lines = new ArrayList<>();

        Log.v(TAG, " Inside getLines()");
        Log.v(TAG, "textBlockSparseArray Size: " + tb.size());
        for (int i = 0; i < tb.size(); i++) {
            TextBlock textBlock = tb.valueAt(i);
            lines.addAll((Collection<? extends Line>) textBlock.getComponents());
            Log.v(TAG, " TextBlock - " + i + ": " + textBlock.getValue());
        }
        return lines;
    }
//    @Override
//    public void onButton2Click(String detectedText, int position) {
//        // method in RecyclerViewButtonClickListener
//        Log.v(TAG, " MA: Pressed button 2 at position " + position);
//        showCreateEventDialog(detectedText);
//    }

    public void showCreateEventDialog(String dialogMessage) {
        DialogFragment newFragment = new CreateEventDialogFragment(dialogMessage);
        newFragment.show(getSupportFragmentManager(), "CEDF");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // method in CreateEventDialogListener
        Log.v(TAG, " MA: Pressed positive dialog button");
        Toast.makeText(getApplicationContext(),
                        "Creating calendar event. No, not really.",
                        Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // method in CreateEventDialogListener
        Log.v(TAG, " MA: Pressed negative dialog button");

    }

    public Line getDateLine(List<Line> lines)   {
        for(Line line: lines)   {
            if(hasMonth(line.getValue()))   {
                return line;
            }
        }
        return null;
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

    public enum DateType    {
        DAY, MONTH, YEAR
    }

    public void setUpEvent(String date, int month, String firstLine, String lastLine) {
        String splitWords = date;
        String[] strArray = splitWords.split(" ");

        int day = Integer.parseInt(strArray[0]);
        //int y = Integer.parseInt(strArray[1]);
        int year = Integer.parseInt(strArray[2]);

        Log.v(TAG, day + "\n");
        Log.v(TAG, month + "\n");
        Log.v(TAG, year + "\n");
        Log.v(TAG, "First Line: " + firstLine + "\n");
        Log.v(TAG, "Last Line: " + lastLine + "\n");

        Calendar startTime = Calendar.getInstance();
        startTime.set(year, month, day, 8, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, 9, 0);

        long eventTime1 = startTime.getTimeInMillis();
        long eventTime2 = endTime.getTimeInMillis();

        String title = firstLine;
        String location = lastLine;
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", eventTime1);
        intent.putExtra("endTime", eventTime2);//+60*60*1000);
        intent.putExtra("title", title);
        intent.putExtra("description", "Event Description");
        intent.putExtra("eventLocation", location);
        startActivity(intent);
    }
}
