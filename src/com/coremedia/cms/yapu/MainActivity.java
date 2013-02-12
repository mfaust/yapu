package com.coremedia.cms.yapu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity {

  private static final int ACTION_TAKE_PHOTO = 1;

  private Uri imageUri;

  Button.OnClickListener mTakePicOnClickListener =
          new Button.OnClickListener() {
            public void onClick(View v) {
              Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
              takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
              imageUri = Uri.fromFile(photo);
              startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
            }
          };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button picBtn = (Button) findViewById(R.id.btnTakePicture);
    setBtnListenerOrDisable(
              picBtn,
              mTakePicOnClickListener,
              MediaStore.ACTION_IMAGE_CAPTURE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode ==  ACTION_TAKE_PHOTO && resultCode == RESULT_OK) {
      Intent myIntent = new Intent(getApplicationContext(), CreateAndSendPhoto.class);
      myIntent.putExtra(Yapu.PHOTO_URI, imageUri);
      startActivityForResult(myIntent, 0);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  private void setBtnListenerOrDisable(
          Button btn,
          Button.OnClickListener onClickListener,
          String intentName
  ) {
    if (isIntentAvailable(this, intentName)) {
      btn.setOnClickListener(onClickListener);
    } else {
      btn.setText(getText(R.string.cannot).toString() + " " + btn.getText());
      btn.setClickable(false);
    }
  }

  public static boolean isIntentAvailable(Context context, String action) {
    final PackageManager packageManager = context.getPackageManager();
    final Intent intent = new Intent(action);
    List<ResolveInfo> list =
            packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
  }
}
