package com.coremedia.cms.yapu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class Yapu extends Activity {
	public static final String PHOTO_URI = "photoUri";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post2_core_media);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		

		// if this is from the share menu
		if (Intent.ACTION_SEND.equals(action)) {
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				try {
					// Get resource path from intent callee
					Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
					Intent createMessageAndSend = new Intent(this,
							CreateAndSendPhoto.class);
					createMessageAndSend.putExtra(PHOTO_URI, uri);
					startActivity(createMessageAndSend);
					return;
				} catch (Exception e) {
					Log.e(this.getClass().getName(), e.toString());
				}

			} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
				return;
			}
		}

	}
}
