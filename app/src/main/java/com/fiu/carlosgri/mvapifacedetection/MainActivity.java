package com.fiu.carlosgri.mvapifacedetection;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE1 = 1;
    private static int RESULT_LOAD_IMAGE2 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        //btn1.setOnClickListener(this);
        //btn1.setOnClickListener(this);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(inte, RESULT_LOAD_IMAGE1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(inte, RESULT_LOAD_IMAGE2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //ImageView imageView = (ImageView) findViewById(R.id.imgview);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inSampleSize = 4;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap myBitmap = BitmapFactory.decodeFile(picturePath, options);
            String myBitMap = convertBitmapToString(myBitmap);
            Log.d("First Bitmap: ", myBitMap);
//                Bitmap myBitmap2 = BitmapFactory.decodeResource(
//                        getApplicationContext().getResources(),
//                        R.drawable.test1,
//                        options);

            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.RED);
            myRectPaint.setStyle(Paint.Style.STROKE);


            ImageView myImageView = (ImageView) findViewById(R.id.imgview);
            Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
            String SECONDBitMap = convertBitmapToString(tempBitmap);
            Log.d("Second Bitmap: ", SECONDBitMap);
            //Bitmap tempBitmap = Bitmap.createScaledBitmap(myBitmap,myBitmap.getWidth(),myBitmap.getHeight(),true);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(myBitmap, 0, 0, null);


            FaceDetector faceDetector = new
                    FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                    .build();
            if (!faceDetector.isOperational()) {
                //new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                return;
            }



            Frame frame = new Frame.Builder().setBitmap(myBitmap).build();

            SparseArray<Face> faces = faceDetector.detect(frame);
            String imgToUpload = "";
            for (int i = 0; i < faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();

                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                //Bitmap bmFace2 = Bitmap.createBitmap(myBitmap, (int) x1, (int) y1, (int) thisFace.getWidth(), (int) thisFace.getHeight());
                Bitmap bmFace = Bitmap.createBitmap(myBitmap, (int) x1, (int) y1, (int) thisFace.getWidth(), (int) thisFace.getHeight());
                imgToUpload = convertBitmapToString(bmFace);
                Log.d("First image", "This is the first image: " + imgToUpload);
//                String imgToUpload2 = convertBitmapToString(bmFace2);
//                Log.d("First image", "GOODLUCK This is the first image: " + imgToUpload2);
            }
            myImageView.setImageBitmap(tempBitmap);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("tempImg", imgToUpload);
            editor.commit();
        }

        if (requestCode == RESULT_LOAD_IMAGE2 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //ImageView imageView = (ImageView) findViewById(R.id.imgview);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inSampleSize = 4;

            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap myBitmap = BitmapFactory.decodeFile(picturePath, options);
//                Bitmap myBitmap = BitmapFactory.decodeResource(
//                        getApplicationContext().getResources(),
//                        R.drawable.test1,
//                        options);

            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.BLUE);
            myRectPaint.setStyle(Paint.Style.STROKE);

            ImageView myImageView = (ImageView) findViewById(R.id.imgview2);
            Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(myBitmap, 0, 0, null);

            FaceDetector faceDetector = new
                    FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                    .build();
            if (!faceDetector.isOperational()) {
                //new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frame);

            //String secondBitMap = convertBitmapToString(myBitmap);
            //Log.d("Second Bitmap: ", secondBitMap);


            //Bitmap bmFace = new Bitmap();
            String imgToUpload2 = "Empty";
            for (int i = 0; i < faces.size(); i++) {
                Log.d("Face Array", "How many faces in here.");
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                //Bitmap bmFace = Bitmap.createBitmap(myBitmap, (int) thisFace.getPosition().x, (int) thisFace.getPosition().y, (int) thisFace.getWidth() + (int) thisFace.getPosition().x, (int) thisFace.getHeight() + (int) thisFace.getPosition().y);

                Bitmap bmFace = Bitmap.createBitmap(myBitmap, (int) x1, (int) y1, (int) thisFace.getWidth(), (int) thisFace.getHeight());
                imgToUpload2 = convertBitmapToString(bmFace);
                Log.d("Second image boom", "This is the second image: " + imgToUpload2);
            }
            myImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            String imgToUpload1 = preferences.getString("tempImg", "Empty");

            if (imgToUpload1.equals("Empty") || imgToUpload2.equals("Empty")) {
                System.out.println("There was a problem preparing the images to send to the server.");
                Log.d("Image Failed Server", "There was a problem preparing the images to send to the server.");
            } else {
                Log.d("Image Success", "Sending imagegs to server.");
                //sendImgToServer(imgToUpload1, imgToUpload2);
                Map<String, String> postData = new HashMap<>();
                postData.put("file", imgToUpload1);
                //postData.put("img2", imgToUpload2);
                postData.put("data", "test123");
                //Log.d("First image", "This is the first image: " + imgToUpload1);
                AsyncTask task = new LoadBitmaps(postData);
                task.execute();
            }
        }
    }

    public String convertBitmapToString(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream); //compress to which format you want.
        byte[] byte_arr = stream.toByteArray();
        String imageStr = Base64.encodeToString(byte_arr, Base64.NO_WRAP);
        return imageStr;
    }
}
    class LoadBitmaps extends AsyncTask<Object, Void, Void> {
        JSONObject postData;

        public LoadBitmaps(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        @Override
        protected Void doInBackground(Object... str) {
            try {
//                URL url = new URL("http://127.0.0.1:8000/faceDetect/pictureData/");
//                HttpURLConnection client = (HttpURLConnection) url.openConnection();
//                client.setRequestMethod("POST");
//                //client.setRequestProperty("Key","Value");
//                client.setRequestProperty("Content-Type", "application/json");
//                client.setDoOutput(true);
//
////                Uri.Builder builder = new Uri.Builder()
////                        .appendQueryParameter("img1", img1)
////                        .appendQueryParameter("img2", img2);
////                //.appendQueryParameter("thirdParam", paramValue3);
////                String query = builder.build().getEncodedQuery();
//
//                OutputStream os = client.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(postData.toString());
//                writer.flush();
//                writer.close();
//                os.close();
//
//                client.connect();
//                int response = client.getResponseCode();
//                System.out.println("The response code is: " + response);

            //try {

                    //URL url = new URL("http://127.0.0.1:8000/faceDetect/pictureData/");
                URL url = new URL("http://10.50.214.67:8000/faceDetect/pictureData/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json");

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                    }

                    conn.disconnect();

            } catch (MalformedURLException error) {
                //Handles an incorrectly entered URL
                error.printStackTrace();
            } catch (SocketTimeoutException error) {
                //Handles URL access timeout.
                error.printStackTrace();
            } catch (IOException error) {
                //Handles input and output errors
                error.printStackTrace();
            }
            return null;
        }
    }
//        public void sendImgToServer(String img1, String img2) {
//            try {
//                URL url = new URL("http://127.0.0.1:8000/faceDetect/pictureData/");
//                HttpURLConnection client = (HttpURLConnection) url.openConnection();
//                client.setRequestMethod("POST");
//                //client.setRequestProperty("Key","Value");
//                client.setRequestProperty("Content-Type", "application/json");
//                client.setDoOutput(true);
//
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("img1", img1)
//                        .appendQueryParameter("img2", img2);
//                //.appendQueryParameter("thirdParam", paramValue3);
//                String query = builder.build().getEncodedQuery();
//
//                OutputStream os = client.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//
//                client.connect();
//                int response = client.getResponseCode();
//                System.out.println("The response code is: " + response);
////            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
////            outputPost.flush();
////            outputPost.close();
//            } catch (MalformedURLException error) {
//                //Handles an incorrectly entered URL
//                error.printStackTrace();
//            } catch (SocketTimeoutException error) {
//                //Handles URL access timeout.
//                error.printStackTrace();
//            } catch (IOException error) {
//                       