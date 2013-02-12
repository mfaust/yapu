package com.coremedia.cms.yapu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import static android.media.ExifInterface.TAG_GPS_LATITUDE;
import static android.media.ExifInterface.TAG_GPS_LONGITUDE;


public class CreateAndSendPhoto extends Activity {
	private Uri imageUri = null;
	private YapuConnector connector;
	private EditText descriptionTextField;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		connector = new YapuConnector();
		connector.init(this, getPreferences(Context.MODE_PRIVATE));

		Intent intent = getIntent();
		imageUri = (Uri) intent.getExtras().get(Yapu.PHOTO_URI);

		setContentView(R.layout.activity_create_and_send_photo);
		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setImageURI(imageUri);

	}

        public void cancel(View view) {
          Intent myIntent = new Intent(view.getContext(), MainActivity.class);
          startActivityForResult(myIntent, 0);
        }
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_and_send_photo, menu);
		return true;
	}

	public void send(View view) {
		if (imageUri != null) {
			connector.upload(imageUri, createDescription(imageUri));
		}
	}

	protected void onResume() {
		super.onResume();
		connector.resume();
	}

	private String createDescription(Uri imageUri) {
		StringBuilder description = new StringBuilder();

		ExifInterface exifData;
		try {
			exifData = new ExifInterface(imageUri.getPath());

			description.append(exifData.getAttribute(TAG_GPS_LATITUDE))
					.append(",")
					.append(exifData.getAttribute(TAG_GPS_LONGITUDE))
					.append(",")
					.append(getDescription());
			
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Could not read Exif data", e);
		}

		return description.toString();
	}

	private String getDescription() {
		return getDescriptionTextField().getText().toString().trim();
	}

	private EditText getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = (EditText) findViewById(R.id.editText1);
		}

		return descriptionTextField;
	}
}
