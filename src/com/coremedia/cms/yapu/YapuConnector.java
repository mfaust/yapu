package com.coremedia.cms.yapu;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class YapuConnector {
	final static private String APP_KEY = "btl3takvvcfs6gd";
	final static private String APP_SECRET = "8xwdzmwd7gqoi5i";

	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	// In the class declaration section:
	private DropboxAPI<AndroidAuthSession> mDBApi;

	private SharedPreferences sharedPref;
	private Activity activity;

	public void init(Activity activity, SharedPreferences sharedPref) {
		this.sharedPref = sharedPref;
		this.activity = activity;
		final AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		final AndroidAuthSession session = new AndroidAuthSession(appKeys,
				ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);

		if (sharedPref.getString("dropbox.key", null) != null) {
			AccessTokenPair access = new AccessTokenPair(sharedPref.getString(
					"dropbox.key", null), sharedPref.getString(
					"dropbox.secret", null));
			mDBApi.getSession().setAccessTokenPair(access);
		} else {
			mDBApi.getSession().startAuthentication(activity);
		}
	}

	public void upload(Uri imageUri, String description) {
		try {
			String path = getPath(imageUri);
			Log.e("DbExampleLog", "Uploading " + imageUri.getPath()+ "/" + path);
			Log.e("DbExampleLog", "Description " + description);
			
			AssetFileDescriptor descr = activity.getContentResolver().openAssetFileDescriptor(imageUri, "r");
			Entry newEntry = mDBApi.putFile("/" + path,
					descr.createInputStream(), descr.getLength(), null, null);
			Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
			
			String descrFilename = path;
			if(path.indexOf(".") != -1) {
				descrFilename = path.substring(0, path.lastIndexOf(".")) + ".txt";
			}
			Log.e("DbExampleLog", "Description " + description + " uploading to " + descrFilename);
			Entry descEntry = mDBApi.putFile("/" + descrFilename,
					new ByteArrayInputStream(description.getBytes()), description.length(), null, null);
			Log.i("DbExampleLog", "The uploaded file's rev is: " + descEntry.rev);
		} catch (DropboxUnlinkedException e) {
			// User has unlinked, ask them to link again here.
			Log.e("DbExampleLog", "User has unlinked.");
		} catch (DropboxException e) {
			Log.e("DbExampleLog", "Something went wrong while uploading.");
		} catch (Exception e) {
			Log.e("DbExampleLog", "File not found.");
		} 
	}

	private String getPath(Uri uri) {
		String path = "";
		
		String[] projection = { MediaStore.MediaColumns.DATA };
		ContentResolver cr = activity.getContentResolver();

		Cursor metaCursor = cr.query(uri, projection, null, null, null);
		if (metaCursor != null) {
			try {
				if (metaCursor.moveToFirst()) {
					path = metaCursor.getString(0);

				}
			} finally {
				metaCursor.close();
			}
		}
		
		return path.substring(path.lastIndexOf("/"), path.length());
	}

	public void resume() {
		if (mDBApi.getSession().authenticationSuccessful()) {
			try {
				// MANDATORY call to complete auth.
				// Sets the access token on the session
				mDBApi.getSession().finishAuthentication();

				if (sharedPref.getString("dropbox.key", null) == null) {
					AccessTokenPair tokens = mDBApi.getSession()
							.getAccessTokenPair();

					// Provide your own storeKeys to persist the access token
					// pair
					// A typical way to store tokens is using SharedPreferences

					Editor editor = sharedPref.edit();
					editor.putString("dropbox.key", tokens.key);
					editor.putString("dropbox.secret", tokens.secret);
					editor.commit();
				}

				AccessTokenPair access = new AccessTokenPair(
						sharedPref.getString("dropbox.key", null),
						sharedPref.getString("dropbox.secret", null));
				mDBApi.getSession().setAccessTokenPair(access);
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}

		// ...
	}
}
