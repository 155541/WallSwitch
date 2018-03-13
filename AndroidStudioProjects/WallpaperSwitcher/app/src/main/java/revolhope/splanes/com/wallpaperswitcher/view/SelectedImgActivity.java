package revolhope.splanes.com.wallpaperswitcher.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;

import revolhope.splanes.com.wallpaperswitcher.R;

public class SelectedImgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_img);

        ImageView img = findViewById(R.id.imageView);
        if(getIntent() != null && getIntent().hasExtra("BMP")){

            String filename = getIntent().getStringExtra("BMP");
            try {
                FileInputStream is = this.openFileInput(filename);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                is.close();

                img.setImageBitmap(bmp);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        findViewById(R.id.btDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Img deleted hohoho", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
