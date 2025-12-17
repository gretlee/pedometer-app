package com.demo.example.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

//import com.demo.example.AdAdmob;
import com.demo.example.R;

import de.hdodenhof.circleimageview.CircleImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.demo.example.utils.UtilBtimap;
import com.demo.example.simplecropimage.CropImage;


public class Set_Profile_Activity extends AppCompatActivity {
    public static final int REQUEST_CODE_CROP_IMAGE = 3;
    public static final int REQUEST_CODE_GALLERY = 1;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private Bitmap bitmap;
    Typeface font;
    Typeface font1;
    CircleImageView img_photo;
    private File mFileTemp;
    private SharedPreferences mSettings;
    SharedPreferences pref;
    int temp;

    
    @Override 
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setContentView(R.layout.profile_dialog);




        //AdAdmob adAdmob = new AdAdmob( this);
        //adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        //adAdmob.FullscreenAd_Counter(this);






        this.pref = PreferenceManager.getDefaultSharedPreferences(this);
        this.font = Typeface.createFromAsset(getAssets(), "Titillium-Semibold.otf");
        this.font1 = Typeface.createFromAsset(getAssets(), "Titillium-Regular.otf");
        this.img_photo = (CircleImageView) findViewById(R.id.img_photo);
        final EditText editText = (EditText) findViewById(R.id.edtName);
        final EditText editText2 = (EditText) findViewById(R.id.edtweight);
        final EditText editText3 = (EditText) findViewById(R.id.edtheight);
        final EditText editText4 = (EditText) findViewById(R.id.edtsteps);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lin_dial_cancle);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.lin_dial_save);
        ((TextView) findViewById(R.id.txt_cancle)).setTypeface(this.font);
        ((TextView) findViewById(R.id.txt_save)).setTypeface(this.font);
        editText.setTypeface(this.font);
        editText4.setTypeface(this.font1);
        editText3.setTypeface(this.font1);
        editText2.setTypeface(this.font1);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.mSettings = defaultSharedPreferences;
        this.temp = Math.round(Float.parseFloat(defaultSharedPreferences.getString("Goal", "1000").trim()));
        String string = this.pref.getString("img_photo", "");
        if (string.equalsIgnoreCase("")) {
            this.img_photo.setImageResource(R.drawable.add_img);
        } else {
            this.img_photo.setImageBitmap(decodeBase64(string));
        }
        if (this.temp == 0) {
            editText4.setText("");
        } else {
            editText4.setText(this.temp + "");
        }
        String string2 = this.pref.getString("Name", "");
        if (string2.isEmpty()) {
            editText.setText("");
        } else {
            editText.setText(string2);
        }
        float f = this.pref.getFloat("Weight", 0.0f);
        if (f == 0.0f) {
            editText2.setText("");
        } else {
            editText2.setText(String.valueOf(f));
        }
        float f2 = this.pref.getFloat("Height", 0.0f);
        if (f2 == 0.0f) {
            editText3.setText("");
        } else {
            editText3.setText(String.valueOf(f2));
        }
        if ("mounted".equals(Environment.getExternalStorageState())) {
            this.mFileTemp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), TEMP_PHOTO_FILE_NAME);
        } else {
            this.mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        this.img_photo.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Set_Profile_Activity.this.openGallery();
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Set_Profile_Activity.this.finish();
            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                SharedPreferences.Editor edit = Set_Profile_Activity.this.pref.edit();
                edit.putString("Name", editText.getText().toString());
                try {
                    edit.putFloat("Weight", Float.parseFloat(editText2.getText().toString()));
                    edit.putFloat("Height", Float.parseFloat(editText3.getText().toString()));
                    edit.putString("img_photo", Set_Profile_Activity.encodeTobase64(UtilBtimap.bmsave));
                } catch (Exception unused) {
                }
                Set_Profile_Activity.this.mSettings.edit().putString("Goal", editText4.getText().toString()).commit();
                edit.commit();
                Intent intent = new Intent(Set_Profile_Activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Set_Profile_Activity.this.startActivity(intent);
            }
        });
    }

    public static String encodeTobase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodeToString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        Log.d("Image Log:", encodeToString);
        return encodeToString;
    }

    public static Bitmap decodeBase64(String str) {
        byte[] decode = Base64.decode(str, 0);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

    
    public void openGallery() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void startCropImage() {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, this.mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 0);
        intent.putExtra(CropImage.ASPECT_Y, 0);
        startActivityForResult(intent, 3);
    }

    @Override 
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            return;
        }
        if (i == 1) {
            try {
                InputStream openInputStream = getContentResolver().openInputStream(intent.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(this.mFileTemp);
                copyStream(openInputStream, fileOutputStream);
                fileOutputStream.close();
                openInputStream.close();
                startCropImage();
            } catch (Exception e) {
                Log.e("TAG", "Error while creating temp file", e);
            }
        } else if (i == 3) {
            if (intent.getStringExtra(CropImage.IMAGE_PATH) == null) {
                return;
            }
            Bitmap decodeFile = BitmapFactory.decodeFile(this.mFileTemp.getPath());
            this.bitmap = decodeFile;
            this.img_photo.setImageBitmap(decodeFile);
            UtilBtimap.bmsave = this.bitmap;
        }
        super.onActivityResult(i, i2, intent);
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return;
            }
            outputStream.write(bArr, 0, read);
        }
    }

    @Override 
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
