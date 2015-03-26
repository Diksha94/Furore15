package com.example.dsi.furore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SelfieTimeline extends ActionBarActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMAGE = 2;
    int lastPosition = 0;
    private Toolbar toolbar;
    public static int number = 0;
    ArrayList<Float> lista = new ArrayList<>();
    public static ImageLoader imageLoader = ImageLoader.getInstance();

    StaggeredGridView gridView;

    static DisplayImageOptions defaultOptions;
    ImageLoaderConfiguration config;

    ArrayList<String> image_urls = new ArrayList<>(), ids = new ArrayList<>(), fb_ids = new ArrayList<>();


    int drawables[] = {R.drawable.art, R.drawable.dance, R.drawable.game, R.drawable.art, R.drawable.dance, R.drawable.game,
            R.drawable.art, R.drawable.dance, R.drawable.game, R.drawable.art, R.drawable.dance, R.drawable.game, R.drawable.art, R.drawable.dance, R.drawable.game, R.drawable.art, R.drawable.dance, R.drawable.game,
            R.drawable.art, R.drawable.dance, R.drawable.game, R.drawable.art, R.drawable.dance, R.drawable.game};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_timeline);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Selfie!!");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configUIL();

//        new imageUrlLoader().execute();

        //for randomising the selfie image height
        lista.add((float) 0.9);
        lista.add((float) 1.05);
        lista.add((float) 0.95);
        lista.add((float) 1.1);

        initFloatingMenu();

        gridView = (StaggeredGridView) findViewById(R.id.grid_view);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View footer = inflater.inflate(R.layout.footer, null);
        gridView.addFooterView(footer, "potato", true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String url = (String) view.getTag();
                SelfieDetails.launch(SelfieTimeline.this, view.findViewById(R.id.imageView), "http://microblogging.wingnity.com/JSONParsingTutorial/jolie.jpg");
            }
        });
        gridView.setAdapter(new GridViewAdapter());

    }

    private void configUIL() {
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
//                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // resource or drawable
                .displayer(new FadeInBitmapDisplayer(300)).build();

        config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

    }

    public class GridViewAdapter extends BaseAdapter {

        public GridViewAdapter() {
            imageLoader = ImageLoader.getInstance();
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            } else {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_top);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        class Holder {
            DynamicHeightImageView iv;
            TextView textView;

            Holder(View v) {
                iv = (DynamicHeightImageView) v.findViewById(R.id.imageView);
                textView = (TextView) v.findViewById(R.id.imageText);
            }
        }

        @Override
        public int getCount() {
            return drawables.length;
        }

        @Override
        public Object getItem(int position) {
            return drawables[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder mHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.single_image_view, parent, false);
                mHolder = new Holder(convertView);
                convertView.setTag(mHolder);
            } else {
                mHolder = (Holder) convertView.getTag();
            }
            mHolder.iv = (DynamicHeightImageView) convertView.findViewById(R.id.imageView);
            mHolder.textView = (TextView) convertView.findViewById(R.id.imageText);
            mHolder.textView.setText("this is random text");
            mHolder.iv.setImageResource(drawables[position]);
            //load using auil
            imageLoader.displayImage("http://microblogging.wingnity.com/JSONParsingTutorial/jolie.jpg"
                    , mHolder.iv, defaultOptions);

            mHolder.iv.setHeightRatio(getRandomHeight(position));
            setAnimation(convertView, position);

            return convertView;
        }

    }


    private void initFloatingMenu() {


        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.camera);

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();


        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// change the camera icon
        Drawable cameraDrawable = getResources().getDrawable(R.drawable.camera);
        ImageView cameraIcon = new ImageView(this);

        SubActionButton cameraButton = itemBuilder.setContentView(cameraIcon).build();
        cameraButton.setBackgroundDrawable(cameraDrawable);


        Drawable attachDrawable = getResources().getDrawable(R.drawable.camera);
        ImageView attachIcon = new ImageView(this);
        SubActionButton attachButton = itemBuilder.setContentView(attachIcon).build();
        attachButton.setBackgroundDrawable(attachDrawable);


        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(cameraButton)
                .addSubActionView(attachButton)
                .attachTo(actionButton)

                .build();


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //function to open camera and capture an image
                if (actionMenu.isOpen()) {
                    actionMenu.close(true);
                }
                clickPicture();
            }
        });

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //function to select an existing image
                if (actionMenu.isOpen()) {
                    actionMenu.close(true);
                }
                selectPicture();
            }
        });

    }

    private void selectPicture() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void clickPicture() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(picIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //add code for displaying picture and uploading it
            dialog(imageBitmap);
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bm = BitmapFactory.decodeFile(picturePath);
//add function to display and send the data
            dialog(bm);
        }
    }

    void dialog(Bitmap bitmap) {
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bitmap);
        new AlertDialog.Builder(this)
                .setView(iv)
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //function to upload
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    public class imageUrlLoader extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost post = new HttpPost("http://bitsmate.in/furore/selfie_timeline.php?page_no=" + number);
//                HttpPost httpPost = new HttpPost("http://microblogging.wingnity.com/JSONParsingTutorial/jolie.jpg");
                HttpResponse response = httpClient.execute(post);
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                parsedata(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("raj", "" + image_urls);
        }

        public static final String IMG_URL = "img_url", ID = "id", FB_ID = "fb_id";

        private void parsedata(String data) {
//            Log.d("raj", data);
            try {
                JSONObject object = new JSONObject(data);
//                Log.d("raj", "object = " + object);
                for (int i = 0; i < object.length(); i++) {
                    JSONObject subObject = object.getJSONObject("" + i);
//                    Log.d("raj", "" + subObject);
                    String img_url = subObject.getString(IMG_URL);
                    String id = subObject.getString(ID);
                    String fb_id = subObject.getString(FB_ID);
                    image_urls.add(img_url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private float getRandomHeight(int position) {
        return lista.get(position % 4);

    }

}
