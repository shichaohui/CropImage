package com.test.cropimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private CropImage cropImage = null;
	private Button btn_01 = null;
	private Button btn_02 = null;
	private ImageView imageView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cropImage = new CropImage(this);

		btn_01 = (Button) findViewById(R.id.btn_01);
		btn_02 = (Button) findViewById(R.id.btn_02);
		imageView = (ImageView) findViewById(R.id.imageID);

		btn_01.setOnClickListener(this);
		btn_02.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap bitmap = cropImage.onResult(requestCode, resultCode, data);
		if (bitmap != null) {
			System.out.println(bitmap.getWidth() + "___" + bitmap.getHeight());
			imageView.setImageBitmap(bitmap);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_01:
			cropImage.openAlbums();
			break;

		case R.id.btn_02:
			cropImage.openCamera();
			break;

		default:
			break;
		}
	}

}
