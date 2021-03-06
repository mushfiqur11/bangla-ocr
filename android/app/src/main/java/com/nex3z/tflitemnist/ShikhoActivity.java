package com.nex3z.tflitemnist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.fingerpaintview.FingerPaintView;

import java.io.IOException;

import butterknife.BindView;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShikhoActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShikhoActivity.class.getSimpleName();


    @BindView(R.id.tv_prediction) TextView mTvPrediction;
    @BindView(R.id.tv_probability) TextView mTvProbability;
    @BindView(R.id.tv_timecost) TextView mTvTimeCost;
    @BindView(R.id.fpv_paint) FingerPaintView mFpvPaint;
    @BindView(R.id.result)TextView mTvResult;

    @BindView(R.id.result1)TextView mTvResult1;
    @BindView(R.id.result2)TextView mTvResult2;
    @BindView(R.id.result3)TextView mTvResult3;
    Button btn_clear,btn_detect;
    ImageView captureButton;
    private Classifier mClassifier;
    Bitmap captured_image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

        captureButton = findViewById(R.id.captureButton);
        btn_clear = findViewById(R.id.btn_clear);
        btn_detect = findViewById(R.id.btn_detect);
        mTvPrediction.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bangla_font.ttf"));
        mTvResult1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bangla_font.ttf"));
        mTvResult2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bangla_font.ttf"));
        mTvResult3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bangla_font.ttf"));
        btn_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetectClick();
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClick();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,0);
            }
        });


    }

    void onClearClick() {
        mFpvPaint.clear();
        mTvPrediction.setText(R.string.empty);
        mTvProbability.setText(R.string.empty);
        mTvTimeCost.setText(R.string.empty);
        mTvResult.setVisibility(View.INVISIBLE);
        mTvResult1.setVisibility(View.INVISIBLE);
        mTvResult2.setVisibility(View.INVISIBLE);
        mTvResult3.setVisibility(View.INVISIBLE);

    }

    void onCaptureClick()
    {
        onClearClick();
        if (mClassifier == null) {
            Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized");
            return;}
//        } else if (mFpvPaint.isEmpty()) {
//            Toast.makeText(this, R.string.please_write_a_digit, Toast.LENGTH_SHORT).show();
//            return;
//        }



//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.eight);
//        Bitmap scaled = Bitmap.createScaledBitmap(b, 40, 40, true);

        Bitmap captured_image_scaled = Bitmap.createScaledBitmap(captured_image, 40, 40, true);


        Result result = mClassifier.classify(captured_image_scaled);
        renderResult(result);
    }



    void onDetectClick() {
        if (mClassifier == null) {
            Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized");
            return;}
//        } else if (mFpvPaint.isEmpty()) {
//            Toast.makeText(this, R.string.please_write_a_digit, Toast.LENGTH_SHORT).show();
//            return;
//        }



//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.eight);
//        Bitmap scaled = Bitmap.createScaledBitmap(b, 40, 40, true);

                Bitmap image = mFpvPaint.exportToBitmap(
                        Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT);


        //Bitmap captured_image_scaled = Bitmap.createScaledBitmap(captured_image, 40, 40, true);


        Result result = mClassifier.classify(image);
        renderResult(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        captured_image = (Bitmap) data.getExtras().get("data");
        onCaptureClick();

    }



    private void init() {


        try {

            //mClassifier = new Interpreter(loadModelFile(ShikhoActivity.this,modelFile));
            mClassifier = new Classifier(this);
        } catch (IOException e) {
            Toast.makeText(this, R.string.failed_to_create_classifier, Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "init(): Failed to create Classifier", e);
        }


            //mClassifier = new Classifier(this);

    }

    private void renderResult(Result result) {
        mTvPrediction.setText(String.valueOf(result.getNumber()));
        mTvProbability.setText(String.valueOf(result.getProbability()));
        mTvTimeCost.setText(String.format(getString(R.string.timecost_value),
                result.getTimeCost()));
        if(result.getFlag()==0) {
            mTvResult.setVisibility(View.INVISIBLE);
            mTvResult1.setVisibility(View.INVISIBLE);
            mTvResult2.setVisibility(View.INVISIBLE);
            mTvResult3.setVisibility(View.INVISIBLE);
        }
        else if(result.getFlag()<4){
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult1.setVisibility(View.VISIBLE);
            mTvResult2.setVisibility(View.VISIBLE);
            mTvResult3.setVisibility(View.VISIBLE);
            mTvResult.setText(R.string.not_sure);
        }
        else{
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult1.setVisibility(View.VISIBLE);
            mTvResult2.setVisibility(View.VISIBLE);
            mTvResult3.setVisibility(View.VISIBLE);
            mTvResult.setText(R.string.fail_to_detect);
        }
        mTvResult1.setText(String.valueOf(result.getmNumber1()));
        mTvResult2.setText(String.valueOf(result.getmNumber2()));
        mTvResult3.setText(String.valueOf(result.getmNumber3()));
//        mTvTimeCost.setText(String.format(String.valueOf(Classifier.total)));
    }

}
