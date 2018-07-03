package com.ffcc66.sxyj.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.entity.User;
import com.ffcc66.sxyj.response.EntityResponse;
import com.ffcc66.sxyj.util.GsonUtil;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import okhttp3.Call;

public class EditInformationActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.circleimgHead)
    com.ffcc66.sxyj.View.CircleImageView circleimgHead;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etSign)
    EditText etSign;
    @BindView(R.id.radioGroupSex)
    RadioGroup radioGroupSex;
    @BindView(R.id.rbMan)
    RadioButton rbMan;
    @BindView(R.id.rbWoman)
    RadioButton rbWoman;
    @BindView(R.id.tvComplete)
    TextView tvComplete;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private View inflate;
    private TextView choosePhoto;
    private TextView takePhoto;
    private Dialog dialog;


    SharedPreferences sharedPreferences;

    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static  String PHOTO_FILE_NAME = "temp_photo.jpg";
    private boolean changeHeadImg = false;
    private File tempHeadimgFile;
    private File headimg;
    private User user = new User();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_edit_information;
    }

    @Override
    protected void initData() {

        sharedPreferences = getSharedPreferences("userdata",Context.MODE_PRIVATE);
        user.setId(sharedPreferences.getInt("id",0));
        user.setUsername(sharedPreferences.getString("username",""));
        user.setPassword(sharedPreferences.getString("password",""));
        user.setPhone(sharedPreferences.getString("phone",""));
        user.setEmail(sharedPreferences.getString("email",""));
        user.setSex(sharedPreferences.getInt("sex",0));
        user.setSign(sharedPreferences.getString("sign",""));

        PHOTO_FILE_NAME = user.getId()+".jpg";
        etUsername.setText(user.getUsername());
        etSign.setText(user.getSign());
        if (user.getSex() == 0) {
            radioGroupSex.check(rbWoman.getId());
        } else {
            radioGroupSex.check(rbMan.getId());
        }
    }

    @Override
    protected void initListener() {
        btnLogout.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        circleimgHead.setOnClickListener(this);
        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == rbMan.getId()) {
                    user.setSex(1);
                } else {
                    user.setSex(0);
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogout:
                SharedPreferences.Editor editor = getSharedPreferences("userdata", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(EditInformationActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                break;
            case R.id.tvComplete:
                user.setSign(etSign.getText().toString());
                Log.d("edit", "onClick: "+etSign.getText().toString());
                new AlertDialog.Builder(EditInformationActivity.this)
                        .setTitle("确认修改")
                        .setMessage("是否保存信息？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUserinfo(user);
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
                break;
            case R.id.circleimgHead:
                show(view);
                break;
            case R.id.takePhoto:
                camera(view);
                dialog.dismiss();
                break;
            case R.id.choosePhoto:
                gallery(view);
                dialog.dismiss();
                break;

        }
    }

    private void updateUserinfo(final User user) {

        final String userinfo_string = GsonUtil.GsonString(user);
        final Dialog dialog = LoadingDialogUtils.createLoadingDialog(EditInformationActivity.this, "请稍等");
        OkHttpUtils.post()
                .url("http://192.168.137.1:8080/SXYJApi/UserService/updateUserinfo")
                .addParams("userinfo_string",userinfo_string)
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        Toast.makeText(EditInformationActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        EntityResponse<String> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<String>>(){}.getType());
                        String result = (String) entityResponse.getObject();
                        if (result.equals("OK")) {
                            Toast.makeText(EditInformationActivity.this,"修改成功",Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("sign",user.getSign());
                            editor.putInt("sex",user.getSex());
                            editor.commit();

                        } else {
                            Toast.makeText(EditInformationActivity.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }


    /*
     * 从相册获取
     */
    public void gallery(View view) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera(View view) {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempHeadimgFile = new File(this.getExternalFilesDir("headImg").getAbsolutePath(),
                    "temp.jpg");
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempHeadimgFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /*
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempHeadimgFile));
            } else {
                Toast.makeText(EditInformationActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                this.circleimgHead.setImageBitmap(bitmap);
                saveBitmap(bitmap, PHOTO_FILE_NAME);
            }
            try {
                // 将临时文件删除
                if (tempHeadimgFile != null) {
                    tempHeadimgFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveBitmap(Bitmap bitmap, String filename) {
        File PHOTO_DIR = new File(this.getExternalFilesDir("headImg").getAbsolutePath());//设置保存路径

        headimg = new File(PHOTO_DIR, filename);//设置文件名称

        Log.d("", "saveBitmap: ");
        if(headimg.exists()){
            headimg.delete();
        }
        try {

            headimg.createNewFile();
            FileOutputStream fos = new FileOutputStream(headimg);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            changeHeadImg = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show(View view){
        dialog = new Dialog(this,R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.choose_dialog, null);
        //初始化控件
        choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
        takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

//    public void uploadHeadImg(File headimg) {
//
//        Log.d("", "uploadHeadImg: ");
//        OkHttpUtils.post()
//                .addFile("headimg", user.getId() + ".jpg", headimg)
//                .url("http://192.168.137.1:8080/SXYJApi/UserService/uploadHeadImg")
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Log.e("", "onError: ", e);
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//
//                    }
//                });
//    }
}
