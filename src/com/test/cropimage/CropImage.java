package com.test.cropimage;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.widget.Toast;

/**
 * <p>
 * 功能：
 * <ul>
 * <li>打开照相机拍照并剪切</li>
 * <li>从相册选择图片并剪切</li>
 * </ul>
 * 图片剪切后的大小默认130*130像素<br>
 * 请在使用本类实例的Activity的onActivityResult方法中调用本类实例的onResult方法，以获取剪切后的Bitmap实例
 * </p>
 * 
 * @author shichaohui@meiriq.com
 * 
 */
public class CropImage {

	private Context context = null;

	private static final int FLAG_CAMERA = 1; // 打开相机
	private static final int FLAG_ALBUMS = 2; // 打卡图片库
	private static final int FLAG_CROP = 3; // 执行剪切

	private static final String IMAGE_UNSPECIFIED = "image/*"; // 图片的MIME类型

	private String filePath = ""; // 照相时图片的保存路径
	private String fileName = "/temp.jpg"; // 照相时图片的保存名字

	private int aspectX = 1; // 剪切后的图片宽度高度比例
	private int aspectY = 1;
	private int outputX = 130; // 剪切后的图片宽度
	private int outputY = 130; // 剪切后的图片高度

	public CropImage(Context context) {
		this.context = context;
	}

	/** 打开相册，选择图片后执行剪切 */
	public void openAlbums() {
		// 打开图片库
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
		((Activity) context).startActivityForResult(intent, FLAG_ALBUMS);
	}

	/** 打开相机，拍照后执行剪切 */
	public void openCamera() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ fileName;
			// 打开照相机
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 设置相片保存路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(filePath)));
			((Activity) context).startActivityForResult(intent, FLAG_CAMERA);
		} else {
			Toast.makeText(context, "存储卡不可用，请从相册选取", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Activity的onActivityResult方法中调用此方法，
	 * 返回已个裁剪好的Bitmap实例，此方法会返回null值，请注意null值判断
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return
	 */
	public Bitmap onResult(int requestCode, int resultCode, Intent data) {
		File file = new File(filePath);
		if (requestCode == FLAG_CAMERA) {
			/* resultCode == -1说明选定了图片，否则就是没有选定图片直接退出了相机 */
			if (resultCode == -1) {
				startPhotoZoom(Uri.fromFile(file));
			} else if (file.exists()) {
				file.delete(); // 删除临时的图片文件
			}
		} else if (requestCode == FLAG_ALBUMS) {
			if (data == null) {
				return null;
			}
			Uri uri = data.getData();
			if (uri != null)
				startPhotoZoom(uri);
		} else if (requestCode == FLAG_CROP) {
			if (file.exists())
				file.delete(); // 删除临时的图片文件
			return getBitmapFromIntent(data);
		}
		return null;
	}

	/**
	 * 从Intent中解析出一个Bitmap实例
	 * 
	 * @param data
	 *            数据源
	 * @return
	 */
	private Bitmap getBitmapFromIntent(Intent data) {
		if (data == null) {
			return null;
		}
		Bitmap photo = null;
		Bundle extras = data.getExtras();
		if (extras != null) {
			photo = extras.getParcelable("data");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// 压缩文件
			photo.compress(CompressFormat.JPEG, 75, stream);
		}
		return photo;
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
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("return-data", true);
		((Activity) context).startActivityForResult(intent, FLAG_CROP);
	}

}
