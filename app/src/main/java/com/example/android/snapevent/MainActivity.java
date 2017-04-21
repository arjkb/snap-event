package com.example.android.snapevent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MyRecyclerViewAdapter.RecyclerViewButtonClickListener {

//    ImageView mImageView1;
    GridView gridView;

    public RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        gridView = (GridView) findViewById(R.id.gridView1);

        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        myRecyclerView.setAdapter(new MyRecyclerViewAdapter(getDummyText(100)));
        myRecyclerView.setAdapter(new MyRecyclerViewAdapter(getImageFileNames()));

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                dispatchTakePictureIntent();
            }
        });

//        mImageView1 = (ImageView) findViewById(R.id.imageView1);
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
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
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
//                setPic();
//
                List<File> fileNames = getImageFileNames();
//
                for(File fileName: fileNames)   {
                    Log.v(TAG, "File Name: " + fileName.toString());
                }

//                gridView.setAdapter(new GridViewAdapter(this, fileNames));

                Toast.makeText(getApplicationContext(),
                        "Photo available in the gallery",
                        Toast.LENGTH_SHORT).show();
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
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
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
//        List<File> inFiles = new ArrayList<>(Arrays.asList(storageDir.listFiles()));

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

//    private void setPic()   {
//        int targetW = mImageView1.getWidth();
//        int targetH = mImageView1.getHeight();
//
//        // get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // decode the image file into a bitmap sized to fill the view
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable =true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView1.setImageBitmap(bitmap);
//    }


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

}
