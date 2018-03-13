package revolhope.splanes.com.wallpaperswitcher.view;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import revolhope.splanes.com.wallpaperswitcher.R;
import revolhope.splanes.com.wallpaperswitcher.callback.OnClickListener;
import revolhope.splanes.com.wallpaperswitcher.controller.RecyclerViewAdapter;
import revolhope.splanes.com.wallpaperswitcher.toolkit.BitmapTool;

public class MainActivity extends AppCompatActivity{

    private static final int REQ_GALLERY = 812;
    private RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle();

        adapter = new RecyclerViewAdapter(this, new OnClickListener() {
            @Override
            public void onSimpleClick(Bitmap bmp) {

                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    //bmp.recycle();

                    //Pop intent
                    Intent i = new Intent(getApplicationContext(), SelectedImgActivity.class);
                    i.putExtra("BMP", filename);
                    startActivity(i);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick() {

                Vibrator vibrator =(Vibrator)getSystemService(VIBRATOR_SERVICE);
                if(vibrator != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(30, 100));
                    } else {
                        vibrator.vibrate(30);
                    }
                }
                if(getSupportActionBar() != null){
                    getSupportActionBar().setTitle(null);
                    getSupportActionBar().setSubtitle(null);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                findViewById(R.id.iv_delete).setVisibility(View.VISIBLE);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Wallpapers"), REQ_GALLERY);
//                WallpaperManager myWallpaperManager
//                        = WallpaperManager.getInstance(getApplicationContext());
//                try {
//                    myWallpaperManager.setResource(R.raw.descarga);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
        });

        findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.recycleSelectedItems();
                adapter.stopAnim();
                findViewById(R.id.iv_delete).setVisibility(View.GONE);
                setTitle();
            }
        });

    }

    private void setTitle(){
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Wallpaper Switcher");
            getSupportActionBar().setSubtitle("Select your favourite images");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){

            if(requestCode == REQ_GALLERY && data != null){

                ArrayList<Uri> uriList = new ArrayList<>();
                if(data.getData()!=null){

                    Uri uri = data.getData();
                    if(uri != null) uriList.add(uri);

                }else if (data.getClipData() != null) {

                    ClipData mClipData = data.getClipData();

                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        if(uri != null) uriList.add(uri);
                    }
                }

                HashMap<Uri, ExifInterface> hashMap = new HashMap<>();
                for(Uri uri : uriList){
                    try {
                        InputStream input = this.getContentResolver().openInputStream(uri);
                        ExifInterface ei = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N && input != null) {
                            ei = new ExifInterface(input);
                        }
                        hashMap.put(uri,ei);

                    }catch (IOException e){
                        Toast.makeText(this,"Oops, it was not possible to get '" + uri.getLastPathSegment() + "' image", Toast.LENGTH_LONG)
                                .show();
                        e.printStackTrace();
                    }
                }

                BitmapTool bitmapTool = new BitmapTool(this);
                adapter.setDataset(bitmapTool.checkRotation(hashMap));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            adapter.stopAnim();
            findViewById(R.id.iv_delete).setVisibility(View.GONE);
            setTitle();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            findViewById(R.id.iv_delete).setVisibility(View.GONE);
            adapter.stopAnim();
            setTitle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
