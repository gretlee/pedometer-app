
package com.demo.example.simplecropimage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.example.R;

public class CropImage extends MonitoredActivity {
    public static final String ACTION_INLINE_DATA = "inline-data";
    public static final String ASPECT_X = "aspectX";
    public static final String ASPECT_Y = "aspectY";
    public static final int CANNOT_STAT_ERROR = -2;
    public static final String CIRCLE_CROP = "circleCrop";
    public static final String IMAGE_PATH = "image-path";
    public static final int NO_STORAGE_ERROR = -1;
    public static final String ORIENTATION_IN_DEGREES = "orientation_in_degrees";
    public static final String OUTPUT_X = "outputX";
    public static final String OUTPUT_Y = "outputY";
    public static final String RETURN_DATA = "return-data";
    public static final String RETURN_DATA_AS_BITMAP = "data";
    public static final String SCALE = "scale";
    public static final String SCALE_UP_IF_NEEDED = "scaleUpIfNeeded";
    private static final String TAG = "CropImage";
    ImageView img_cancle;
    ImageView img_done;
    ImageView img_left;
    ImageView img_right;
    private int mAspectX;
    private int mAspectY;
    private Bitmap mBitmap;
    private ContentResolver mContentResolver;
    HighlightView mCrop;
    private String mImagePath;
    private CropImageView mImageView;
    private int mOutputX;
    private int mOutputY;
    boolean mSaving;
    private boolean mScale;
    boolean mWaitingToPick;
    TextView txt_cancle;
    TextView txt_done;
    TextView txt_left;
    TextView txt_right;
    final int IMAGE_MAX_SIZE = 1024;
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    private Uri mSaveUri = null;
    private boolean mDoFaceDetection = true;
    private boolean mCircleCrop = true;
    private final Handler mHandler = new Handler();
    private boolean mScaleUp = true;
    private final BitmapManager.ThreadSet mDecodingThreads = new BitmapManager.ThreadSet();
    Runnable mRunFaceDetection = new AnonymousClass7();

    @Override 
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContentResolver = getContentResolver();
        requestWindowFeature(1);
        setContentView(R.layout.cropimage);
        this.mImageView = (CropImageView) findViewById(R.id.image);
        showStorageToast(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString(CIRCLE_CROP) != null) {
                if (Build.VERSION.SDK_INT > 11) {
                    this.mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                this.mCircleCrop = true;
                this.mAspectX = 1;
                this.mAspectY = 1;
            }
            String string = extras.getString(IMAGE_PATH);
            this.mImagePath = string;
            this.mSaveUri = getImageUri(string);
            this.mBitmap = getBitmap(this.mImagePath);
            if (extras.containsKey(ASPECT_X) && (extras.get(ASPECT_X) instanceof Integer)) {
                this.mAspectX = extras.getInt(ASPECT_X);
                if (extras.containsKey(ASPECT_Y) && (extras.get(ASPECT_Y) instanceof Integer)) {
                    this.mAspectY = extras.getInt(ASPECT_Y);
                    this.mOutputX = extras.getInt(OUTPUT_X);
                    this.mOutputY = extras.getInt(OUTPUT_Y);
                    this.mScale = extras.getBoolean(SCALE, true);
                    this.mScaleUp = extras.getBoolean(SCALE_UP_IF_NEEDED, true);
                } else {
                    throw new IllegalArgumentException("aspect_y must be integer");
                }
            } else {
                throw new IllegalArgumentException("aspect_x must be integer");
            }
        }
        if (this.mBitmap == null) {
            Log.d(TAG, "finish!!!");
            finish();
            return;
        }
        getWindow().addFlags(1024);
        this.img_cancle = (ImageView) findViewById(R.id.discard);
        this.img_left = (ImageView) findViewById(R.id.rotateLeft);
        this.img_right = (ImageView) findViewById(R.id.rotateRight);
        this.img_done = (ImageView) findViewById(R.id.save);
        this.txt_cancle = (TextView) findViewById(R.id.txt_cancle);
        this.txt_left = (TextView) findViewById(R.id.txt_left);
        this.txt_right = (TextView) findViewById(R.id.txt_right);
        this.txt_done = (TextView) findViewById(R.id.txt_done);
        this.img_cancle.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                CropImage.this.setResult(0);
                CropImage.this.finish();
                CropImage.this.img_cancle.setImageResource(R.drawable.crop_cancle_click);
                CropImage.this.img_left.setImageResource(R.drawable.crop_left_btn);
                CropImage.this.img_right.setImageResource(R.drawable.crop_right_btn);
                CropImage.this.img_done.setImageResource(R.drawable.crop_done_btn);
                CropImage.this.txt_cancle.setTextColor(Color.parseColor("#796ef0"));
                CropImage.this.txt_left.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_right.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_done.setTextColor(Color.parseColor("#3c3c3c"));
            }
        });
        this.img_left.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(-90.0f);
                CropImage cropImage = CropImage.this;
                cropImage.mBitmap = Bitmap.createBitmap(cropImage.mBitmap, 0, 0, CropImage.this.mBitmap.getWidth(), CropImage.this.mBitmap.getHeight(), matrix, true);
                CropImage.this.mImageView.setImageBitmap(CropImage.this.mBitmap);
                CropImage.this.mRunFaceDetection.run();
                CropImage.this.img_cancle.setImageResource(R.drawable.crop_cancle_btn);
                CropImage.this.img_left.setImageResource(R.drawable.crop_left_click);
                CropImage.this.img_right.setImageResource(R.drawable.crop_right_btn);
                CropImage.this.img_done.setImageResource(R.drawable.crop_done_btn);
                CropImage.this.txt_cancle.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_left.setTextColor(Color.parseColor("#796ef0"));
                CropImage.this.txt_right.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_done.setTextColor(Color.parseColor("#3c3c3c"));
            }
        });
        this.img_right.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                CropImage cropImage = CropImage.this;
                cropImage.mBitmap = Bitmap.createBitmap(cropImage.mBitmap, 0, 0, CropImage.this.mBitmap.getWidth(), CropImage.this.mBitmap.getHeight(), matrix, true);
                CropImage.this.mImageView.setImageBitmap(CropImage.this.mBitmap);
                CropImage.this.mRunFaceDetection.run();
                CropImage.this.img_cancle.setImageResource(R.drawable.crop_cancle_btn);
                CropImage.this.img_left.setImageResource(R.drawable.crop_left_btn);
                CropImage.this.img_right.setImageResource(R.drawable.crop_right_click);
                CropImage.this.img_done.setImageResource(R.drawable.crop_done_btn);
                CropImage.this.txt_cancle.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_left.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_right.setTextColor(Color.parseColor("#796ef0"));
                CropImage.this.txt_done.setTextColor(Color.parseColor("#3c3c3c"));
            }
        });
        this.img_done.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                try {
                    CropImage.this.onSaveClicked();
                } catch (Exception unused) {
                    CropImage.this.finish();
                }
                CropImage.this.img_cancle.setImageResource(R.drawable.crop_cancle_btn);
                CropImage.this.img_left.setImageResource(R.drawable.crop_left_btn);
                CropImage.this.img_right.setImageResource(R.drawable.crop_right_btn);
                CropImage.this.img_done.setImageResource(R.drawable.crop_done_btn);
                CropImage.this.txt_cancle.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_left.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_right.setTextColor(Color.parseColor("#3c3c3c"));
                CropImage.this.txt_done.setTextColor(Color.parseColor("#796ef0"));
            }
        });
        startFaceDetection();
    }

    private Uri getImageUri(String str) {
        return Uri.fromFile(new File(str));
    }

    private Bitmap getBitmap(String str) {
        Uri imageUri = getImageUri(str);
        try {
            InputStream openInputStream = this.mContentResolver.openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(openInputStream, null, options);
            openInputStream.close();
            if (options.outHeight > 1024 || options.outWidth > 1024) {
                i = (int) Math.pow(2.0d, (int) Math.round(Math.log(1024.0d / Math.max(options.outHeight, options.outWidth)) / Math.log(0.5d)));
            }
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = i;
            InputStream openInputStream2 = this.mContentResolver.openInputStream(imageUri);
            Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream2, null, options2);
            openInputStream2.close();
            return decodeStream;
        } catch (FileNotFoundException unused) {
            Log.e(TAG, "file " + str + " not found");
            return null;
        } catch (IOException unused2) {
            Log.e(TAG, "file " + str + " not found");
            return null;
        }
    }

    private void startFaceDetection() {
        if (isFinishing()) {
            return;
        }
        this.mImageView.setImageBitmapResetBase(this.mBitmap, true);
        Util.startBackgroundJob(this, null, "Please wait…", new Runnable() { 
            @Override 
            public void run() {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                final Bitmap bitmap = CropImage.this.mBitmap;
                CropImage.this.mHandler.post(new Runnable() { 
                    @Override 
                    public void run() {
                        if (bitmap != CropImage.this.mBitmap && bitmap != null) {
                            CropImage.this.mImageView.setImageBitmapResetBase(bitmap, true);
                            CropImage.this.mBitmap.recycle();
                            CropImage.this.mBitmap = bitmap;
                        }
                        if (CropImage.this.mImageView.getScale() == 1.0f) {
                            CropImage.this.mImageView.center(true, true);
                        }
                        countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                    CropImage.this.mRunFaceDetection.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, this.mHandler);
    }

    
    public void onSaveClicked() throws Exception {
        HighlightView highlightView;
        int i;
        Bitmap createBitmap;
        if (this.mSaving || (highlightView = this.mCrop) == null) {
            return;
        }
        this.mSaving = true;
        Rect cropRect = highlightView.getCropRect();
        int width = cropRect.width();
        int height = cropRect.height();
        try {
            Bitmap createBitmap2 = Bitmap.createBitmap(width, height, this.mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            if (createBitmap2 == null) {
                return;
            }
            new Canvas(createBitmap2).drawBitmap(this.mBitmap, cropRect, new Rect(0, 0, width, height), (Paint) null);
            if (this.mCircleCrop) {
                Canvas canvas = new Canvas(createBitmap2);
                Path path = new Path();
                float f = width / 2.0f;
                path.addCircle(f, height / 2.0f, f, Path.Direction.CW);
                canvas.clipPath(path, Region.Op.DIFFERENCE);
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            }
            int i2 = this.mOutputX;
            if (i2 != 0 && (i = this.mOutputY) != 0) {
                if (this.mScale) {
                    createBitmap = Util.transform(new Matrix(), createBitmap2, this.mOutputX, this.mOutputY, this.mScaleUp);
                    if (createBitmap2 != createBitmap) {
                        createBitmap2.recycle();
                    }
                } else {
                    createBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.RGB_565);
                    Canvas canvas2 = new Canvas(createBitmap);
                    Rect cropRect2 = this.mCrop.getCropRect();
                    Rect rect = new Rect(0, 0, this.mOutputX, this.mOutputY);
                    int width2 = (cropRect2.width() - rect.width()) / 2;
                    int height2 = (cropRect2.height() - rect.height()) / 2;
                    cropRect2.inset(Math.max(0, width2), Math.max(0, height2));
                    rect.inset(Math.max(0, -width2), Math.max(0, -height2));
                    canvas2.drawBitmap(this.mBitmap, cropRect2, rect, (Paint) null);
                    createBitmap2.recycle();
                }
                createBitmap2 = createBitmap;
            }
            Bundle extras = getIntent().getExtras();
            if (extras != null && (extras.getParcelable(RETURN_DATA_AS_BITMAP) != null || extras.getBoolean(RETURN_DATA))) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(RETURN_DATA_AS_BITMAP, createBitmap2);
                setResult(-1, new Intent().setAction(ACTION_INLINE_DATA).putExtras(bundle));
                finish();
                return;
            }
            Bitmap finalCreateBitmap = createBitmap2;
            Util.startBackgroundJob(this, null, "Saving image", new Runnable() { 
                @Override 
                public void run() {
                    CropImage.this.saveOutput(finalCreateBitmap);
                }
            }, this.mHandler);
        } catch (Exception e) {
            throw e;
        }
    }

    
    public void saveOutput(Bitmap bitmap) {
        Uri uri = this.mSaveUri;
        if (uri != null) {
            OutputStream outputStream = null;
            try {
                try {
                    outputStream = this.mContentResolver.openOutputStream(uri);
                    if (outputStream != null) {
                        bitmap.compress(this.mOutputFormat, 90, outputStream);
                    }
                    Util.closeSilently(outputStream);
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(this.mSaveUri.toString());
                    intent.putExtras(bundle);
                    intent.putExtra(IMAGE_PATH, this.mImagePath);
                    intent.putExtra(ORIENTATION_IN_DEGREES, Util.getOrientationInDegree(this));
                    setResult(-1, intent);
                } catch (IOException e) {
                    Log.e(TAG, "Cannot open file: " + this.mSaveUri, e);
                    setResult(0);
                    finish();
                    Util.closeSilently(outputStream);
                    return;
                }
            } catch (Throwable th) {
                Util.closeSilently(outputStream);
                throw th;
            }
        } else {
            Log.e(TAG, "not defined image url");
        }
        bitmap.recycle();
        finish();
    }

    @Override 
    protected void onPause() {
        super.onPause();
        BitmapManager.instance().cancelThreadDecoding(this.mDecodingThreads);
    }

    
    @Override 
    public void onDestroy() {
        super.onDestroy();
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    
    
    class AnonymousClass7 implements Runnable {
        Matrix mImageMatrix;
        int mNumFaces;
        float mScale = 1.0f;
        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];

        AnonymousClass7() {
        }

        
        public void handleFace(FaceDetector.Face face) {
            PointF pointF = new PointF();
            face.getMidPoint(pointF);
            pointF.x *= this.mScale;
            pointF.y *= this.mScale;
            HighlightView highlightView = new HighlightView(CropImage.this.mImageView);
            Rect rect = new Rect(0, 0, CropImage.this.mBitmap.getWidth(), CropImage.this.mBitmap.getHeight());
            float f = (int) pointF.x;
            float f2 = (int) pointF.y;
            RectF rectF = new RectF(f, f2, f, f2);
            float f3 = -(((int) (face.eyesDistance() * this.mScale)) * 2);
            rectF.inset(f3, f3);
            if (rectF.left < 0.0f) {
                rectF.inset(-rectF.left, -rectF.left);
            }
            if (rectF.top < 0.0f) {
                rectF.inset(-rectF.top, -rectF.top);
            }
            if (rectF.right > rect.right) {
                rectF.inset(rectF.right - rect.right, rectF.right - rect.right);
            }
            if (rectF.bottom > rect.bottom) {
                rectF.inset(rectF.bottom - rect.bottom, rectF.bottom - rect.bottom);
            }
            highlightView.setup(this.mImageMatrix, rect, rectF, CropImage.this.mCircleCrop, (CropImage.this.mAspectX == 0 || CropImage.this.mAspectY == 0) ? false : true);
            CropImage.this.mImageView.add(highlightView);
        }

        
        public void makeDefault() {
            int i;
            HighlightView highlightView = new HighlightView(CropImage.this.mImageView);
            int width = CropImage.this.mBitmap.getWidth();
            int height = CropImage.this.mBitmap.getHeight();
            Rect rect = new Rect(0, 0, width, height);
            int min = (Math.min(width, height) * 4) / 5;
            if (CropImage.this.mAspectX == 0 || CropImage.this.mAspectY == 0) {
                i = min;
            } else if (CropImage.this.mAspectX > CropImage.this.mAspectY) {
                i = (CropImage.this.mAspectY * min) / CropImage.this.mAspectX;
            } else {
                i = min;
                min = (CropImage.this.mAspectX * min) / CropImage.this.mAspectY;
            }
            int i2 = (width - min) / 2;
            int i3 = (height - i) / 2;
            highlightView.setup(this.mImageMatrix, rect, new RectF(i2, i3, i2 + min, i3 + i), CropImage.this.mCircleCrop, (CropImage.this.mAspectX == 0 || CropImage.this.mAspectY == 0) ? false : true);
            CropImage.this.mImageView.mHighlightViews.clear();
            CropImage.this.mImageView.add(highlightView);
        }

        private Bitmap prepareBitmap() {
            if (CropImage.this.mBitmap == null) {
                return null;
            }
            if (CropImage.this.mBitmap.getWidth() > 256) {
                this.mScale = 256.0f / CropImage.this.mBitmap.getWidth();
            }
            Matrix matrix = new Matrix();
            float f = this.mScale;
            matrix.setScale(f, f);
            return Bitmap.createBitmap(CropImage.this.mBitmap, 0, 0, CropImage.this.mBitmap.getWidth(), CropImage.this.mBitmap.getHeight(), matrix, true);
        }

        @Override 
        public void run() {
            this.mImageMatrix = CropImage.this.mImageView.getImageMatrix();
            Bitmap prepareBitmap = prepareBitmap();
            this.mScale = 1.0f / this.mScale;
            if (prepareBitmap != null && CropImage.this.mDoFaceDetection) {
                this.mNumFaces = new FaceDetector(prepareBitmap.getWidth(), prepareBitmap.getHeight(), this.mFaces.length).findFaces(prepareBitmap, this.mFaces);
            }
            if (prepareBitmap != null && prepareBitmap != CropImage.this.mBitmap) {
                prepareBitmap.recycle();
            }
            CropImage.this.mHandler.post(new Runnable() { 
                @Override 
                public void run() {
                    CropImage.this.mWaitingToPick = AnonymousClass7.this.mNumFaces > 1;
                    if (AnonymousClass7.this.mNumFaces > 0) {
                        for (int i = 0; i < AnonymousClass7.this.mNumFaces; i++) {
                            AnonymousClass7 anonymousClass7 = AnonymousClass7.this;
                            anonymousClass7.handleFace(anonymousClass7.mFaces[i]);
                        }
                    } else {
                        AnonymousClass7.this.makeDefault();
                    }
                    CropImage.this.mImageView.invalidate();
                    if (CropImage.this.mImageView.mHighlightViews.size() == 1) {
                        CropImage.this.mCrop = CropImage.this.mImageView.mHighlightViews.get(0);
                        CropImage.this.mCrop.setFocus(true);
                    }
                    if (AnonymousClass7.this.mNumFaces > 1) {
                        Toast makeText = Toast.makeText(CropImage.this, "Multi face crop help", Toast.LENGTH_SHORT);
                        makeText.setGravity(17, 0, 0);
                        makeText.show();
                    }
                }
            });
        }
    }

    public static void showStorageToast(Activity activity) {
        showStorageToast(activity, calculatePicturesRemaining(activity));
    }

    public static void showStorageToast(Activity activity, int i) {
        String str;
        if (i == -1) {
            str = Environment.getExternalStorageState().equals("checking") ? "Preparing card" : "No storage card";
        } else {
            str = i < 1 ? "Not enough space" : null;
        }
        if (str != null) {
            Toast makeText = Toast.makeText(activity, str, Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        }
    }

    public static int calculatePicturesRemaining(Activity activity) {
        String file;
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                file = Environment.getExternalStorageDirectory().toString();
            } else {
                file = activity.getFilesDir().toString();
            }
            StatFs statFs = new StatFs(file);
            return (int) ((statFs.getAvailableBlocks() * statFs.getBlockSize()) / 400000.0f);
        } catch (Exception unused) {
            return -2;
        }
    }

    public boolean isTablet(Context context) {
        return ((context.getResources().getConfiguration().screenLayout & 15) == 4) || ((context.getResources().getConfiguration().screenLayout & 15) == 3);
    }
}



