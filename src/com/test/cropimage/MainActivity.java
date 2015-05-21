package com.test.cropimage;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final int PHOTO_HRAPH = 1; // 拍照
	public static final int PHOTO_ZOOM = 2; // 缩放
	public static final int PHOTO_RESOULT = 3; // 结果

	public static final String IMAGE_UNSPECIFIED = "image/*"; // 图片的MIME类型

	private String filePath = "";
	private String fileName = "/temp.jpg";
	ImageView imageView = null;
	Button button0 = null;
	Button button1 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.imageID);
		button0 = (Button) findViewById(R.id.btn_01);
		button1 = (Button) findViewById(R.id.btn_02);

		button0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 打开图片库
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(Media.EXTERNAL_CONTENT_URI,
						IMAGE_UNSPECIFIED);
				startActivityForResult(intent, PHOTO_ZOOM);
			}
		});

		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					filePath = Environment.getExternalStorageDirectory()
							.getPath() + fileName;
					// 打开照相机
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 设置相片保存路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(filePath)));
					startActivityForResult(intent, PHOTO_HRAPH);
				} else {
					Toast.makeText(MainActivity.this, "存储卡不可用，请从相册选取", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_HRAPH) { // 拍照
			startPhotoZoom(Uri.fromFile(new File(filePath)));
		} else if (data != null) {
			if (requestCode == PHOTO_ZOOM) { // 读取相册缩放图片
				startPhotoZoom(data.getData());
			} else if (requestCode == PHOTO_RESOULT) { // 处理缩放结果
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					// 压缩文件
					photo.compress(CompressFormat.JPEG, 75, stream);
					imageView.setImageBitmap(photo);
					// 删除临时文件
					new File(filePath).delete();
				}

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 调用系统剪切程序剪切图片
	 * 
	 * @param uri
	 *            图片的Uri
	 */
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_RESOULT);
	}

}