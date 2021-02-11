package com.efficientindia.securescan.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.efficientindia.securescan.Constant.AppConfig;
import com.efficientindia.securescan.R;
import com.efficientindia.securescan.model.interfacee;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Barcodescanner extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String intentData = "";
    boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodescanner);
        initViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Barcodescanner.this, HomeScreen.class));
    }

    private void initViews() {

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
//      txtBarcodeValue.setVisibility(View.GONE);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                    if (isEmail)
                        startActivity(new Intent(Barcodescanner.this, EmailActivity.class).putExtra("email_address", intentData));
                    else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                    }
                }
            }
        });
    }

    private void initialiseDetectorsAndSources() {

        // Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    if (ActivityCompat.checkSelfPermission(Barcodescanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(Barcodescanner.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //    Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                //  Toast.makeText(Barcodescanner.this,"",Toast.LENGTH_SHORT).show();

                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;
                                btnAction.setText("ADD CONTENT TO THE MAIL");
                            } else {
                                isEmail = false;
                                btnAction.setText("LAUNCH URL");
                                intentData = barcodes.valueAt(0).displayValue;
//                              txtBarcodeValue.setVisibility(View.GONE);
                                txtBarcodeValue.setText("");
                                check(intentData);
                            }
                        }
                    });
                }
            }
        });
    }

    public void check(String intentData) {
        cameraSource.stop();
        Toast.makeText(Barcodescanner.this,"Data response"+intentData,Toast.LENGTH_SHORT).show();
        ProgressDialog progressDialog = new ProgressDialog(Barcodescanner.this);
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.CONSTANT).addConverterFactory(GsonConverterFactory.create()).build();
        interfacee apiinterface = retrofit.create(interfacee.class);
        Call<com.efficientindia.securescan.model.Response> call = apiinterface.scan(intentData.trim());
        call.enqueue(new Callback<com.efficientindia.securescan.model.Response>() {
            @Override
            public void onResponse(Call<com.efficientindia.securescan.model.Response> call, Response<com.efficientindia.securescan.model.Response> response) {
                progressDialog.dismiss();
               // Toast.makeText(Barcodescanner.this,"Data responsea"+response.body().getMessage(),Toast.LENGTH_SHORT).show();
                try {
                    if (response.body().getMessage().trim().equals("success")) {
                        if (response.body().getData().getStatus().equals("R")) {
                          //  Toast.makeText(Barcodescanner.this, "Data responser", Toast.LENGTH_SHORT).show();

                            order("R", "", "", "", intentData);

                        } else if (response.body().getData().getStatus().equals("A")) {
                            //  Toast.makeText(Barcodescanner.this,"Data responsea",Toast.LENGTH_SHORT).show();

                            order("A", response.body().getData().getbName(), response.body().getData().getpName(), response.body().getData().getmNumber(), intentData);

                        }
                    } else {
                        order("F", "", "", "", intentData);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<com.efficientindia.securescan.model.Response> call, Throwable t) {
                Toast.makeText(Barcodescanner.this, "Failure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void register(String data) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.register);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        EditText et_name = dialog.findViewById(R.id.name);
        EditText et_email = dialog.findViewById(R.id.email);
        EditText et_mobile = dialog.findViewById(R.id.mobile);
        EditText et_address = dialog.findViewById(R.id.address);
        EditText et_date_purchase = dialog.findViewById(R.id.date_of_purchase);
        EditText et_city = dialog.findViewById(R.id.city);
        EditText et_pincode = dialog.findViewById(R.id.pin);

        Button register = dialog.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_name.getText().toString().equals("")){
                    et_name.setError("Field can't be blank");
                }else if(et_mobile.getText().toString().equals("")){
                    et_mobile.setError("Field can't be blank");
                }else if(et_address.getText().toString().equals("")){
                    et_address.setError("Field can't be blank");
                }else if(et_date_purchase.getText().toString().equals("")){
                    et_date_purchase.setError("Field can't be blank");
                }else if(et_city.getText().toString().equals("")){
                    et_city.setError("Field can't be blank");
                }else if(et_pincode.getText().toString().equals("")){
                    et_pincode.setError("Field can't be blank");
                }else if(et_email.getText().toString().equals("")){
                    et_email.setError("Field can't be blank");
                }else{
                    dialog.dismiss();
                    registerproduct(et_name.getText().toString(),et_email.getText().toString(),et_mobile.getText().toString(),et_address.getText().toString(),et_date_purchase.getText().toString(),data,et_city.getText().toString(),et_pincode.getText().toString());
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    }

    public void registerproduct(String name,String email,String mobile,String address,String date,String data,String city,String pin) {
        ProgressDialog progressDialog=new ProgressDialog(Barcodescanner.this);
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.CONSTANT).addConverterFactory(GsonConverterFactory.create()).build();
        interfacee apiinterface = retrofit.create(interfacee.class);
        Call<com.efficientindia.securescan.model.Response> call = apiinterface.register(name,email,mobile,address,city,pin,date,data);
        call.enqueue(new Callback<com.efficientindia.securescan.model.Response>() {
            @Override
            public void onResponse(Call<com.efficientindia.securescan.model.Response> call, Response<com.efficientindia.securescan.model.Response> response) {
                progressDialog.dismiss();
                if(response.body().getMessage().equals("success"))
                {
                    Toast.makeText(Barcodescanner.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Barcodescanner.this,HomeScreen.class));
                }
            }

            @Override
            public void onFailure(Call<com.efficientindia.securescan.model.Response> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(Barcodescanner.this, "Failure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void reportproduct(String name, String mobile, String remark, String data, Dialog dialog) {
       // Toast.makeText(Barcodescanner.this, "Success"+data, Toast.LENGTH_SHORT).show();

        ProgressDialog progressDialog=new ProgressDialog(Barcodescanner.this);
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.CONSTANT).addConverterFactory(GsonConverterFactory.create()).build();
        interfacee apiinterface = retrofit.create(interfacee.class);
        Call<com.efficientindia.securescan.model.Response> call = apiinterface.report(name, mobile, remark, data);
        call.enqueue(new Callback<com.efficientindia.securescan.model.Response>() {
            @Override
            public void onResponse(Call<com.efficientindia.securescan.model.Response> call, Response<com.efficientindia.securescan.model.Response> response) {
                progressDialog.dismiss();
              //  Toast.makeText(Barcodescanner.this, "Success" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                if(response.body().getMessage().equals("success")){
                    Toast.makeText(Barcodescanner.this,"Submitted successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Barcodescanner.this,HomeScreen.class));
                }
                dialog.dismiss();
                if (ActivityCompat.checkSelfPermission(Barcodescanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }else{
                    try {
                        cameraSource.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<com.efficientindia.securescan.model.Response> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(Barcodescanner.this, "Failure", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void report(String data,Dialog dialogg){

        dialogg.dismiss();
       // Toast.makeText(Barcodescanner.this,""+data,Toast.LENGTH_SHORT).show();
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.report);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        EditText et_name=dialog.findViewById(R.id.mobile);
        EditText et_mobile=dialog.findViewById(R.id.mobile);
        EditText et_remark=dialog.findViewById(R.id.remark);
        Button report = dialog.findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_name.getText().toString().equals("")){
                    et_name.setError("Field can't be blank");
                }else if(et_mobile.getText().toString().equals("")){
                    et_mobile.setError("Field can't be blank");
                }else if(et_remark.getText().toString().equals("")){
                    et_remark.setError("Field can't be blank");
                }else{
                    reportproduct(et_name.getText().toString(),et_mobile.getText().toString(),et_remark.getText().toString(),data,dialog);

                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    public void order(String status, String bNamee, String pNamee, String mNumberr,String data) {

       //   Toast.makeText(Barcodescanner.this,""+status.length(),Toast.LENGTH_SHORT).show();

        final Dialog dialg = new Dialog(this);
        dialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialg.setContentView(R.layout.response);
        dialg.setCanceledOnTouchOutside(false);
        dialg.setCancelable(false);
        TextView message = dialg.findViewById(R.id.message);

        LinearLayout layout_register=dialg.findViewById(R.id.register_layout);
        LinearLayout layout_report=dialg.findViewById(R.id.report_layout);
        ImageView imageView = dialg.findViewById(R.id.img_status_recharge);
        TextView bName = dialg.findViewById(R.id.order_id);
        TextView pName = dialg.findViewById(R.id.txt_status);
        TextView mNumber = dialg.findViewById(R.id.part_number);
        Button register = dialg.findViewById(R.id.btn_Register);
        Button btn_scan = dialg.findViewById(R.id.scan_again);
        Button report = dialg.findViewById(R.id.btn_report);
        Button close = dialg.findViewById(R.id.close);
        if (status.trim().equals("R")) {
            message.setText("Product is Already Registered");
            imageView.setImageResource(R.drawable.success);
            pName.setText("If you have not registered");
            report.setVisibility(View.VISIBLE);
            close.setVisibility(View.VISIBLE);
            register.setVisibility(View.GONE);
            btn_scan.setVisibility(View.GONE);
        } else if (status.trim().equals("A")) {
            message.setText("Product Is Geninue");
            imageView.setImageResource(R.drawable.success);
            bName.setTextColor(getResources().getColor(R.color.green));
            pName.setTextColor(getResources().getColor(R.color.green));
            mNumber.setTextColor(getResources().getColor(R.color.green));
            bName.setText("Business Name:" + bNamee);
            pName.setText("Part Name:" + pNamee);
            mNumber.setText("Part Number:" + mNumberr);
//            report.setVisibility(View.GONE);
//            close.setVisibility(View.GONE);
            layout_register.setVisibility(View.VISIBLE);
//            register.setVisibility(View.VISIBLE);
//            btn_scan.setVisibility(View.VISIBLE);
          //  register();
        }else if(status.trim().equals("F")) {
            message.setText("Product is not genuine");
            imageView.setImageResource(R.drawable.failed);
            bName.setVisibility(View.GONE);
            pName.setVisibility(View.GONE);
            mNumber.setVisibility(View.GONE);
            report.setVisibility(View.VISIBLE);
            close.setVisibility(View.VISIBLE);
//            register.setVisibility(View.GONE);
//            btn_scan.setVisibility(View.GONE);
            layout_report.setVisibility(View.VISIBLE);
        }
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialg.dismiss();
                if (ActivityCompat.checkSelfPermission(Barcodescanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }else{
                    try {
                        cameraSource.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialg.dismiss();
                register(data);
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // dialg.dismiss();
                report(data,dialg);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Barcodescanner.this,HomeScreen.class));
            }
        });
        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

}