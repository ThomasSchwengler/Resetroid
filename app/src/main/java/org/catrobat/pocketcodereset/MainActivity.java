package org.catrobat.pocketcodereset;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Pocket Code";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.remove_all_projects_button).setOnClickListener(this);
	}

	public boolean checkPermissions() {
		return ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
				&& (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
	}

	public void requestPermissions() {
		String[] permissions = new String[]{
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		};

		ActivityCompat.requestPermissions(this, permissions, 0);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 0 && grantResults.length == 2
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED
				&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
			removeAllProjects();
		}
	}

	private void removeAllProjects() {
		if (!checkPermissions()) {
			requestPermissions();
		} else {
			new RemoveAllProjectsAsync(this).execute();
		}
	}

	@Override
	public void onClick(View v) {
		removeAllProjects();
	}

	private static class RemoveAllProjectsAsync extends AsyncTask<Void, Void, Boolean> {
		private WeakReference<MainActivity> activityReference;

		RemoveAllProjectsAsync(MainActivity context) {
			activityReference = new WeakReference<>(context);
		}

		private static boolean deleteDirectory(File directory) {
			if (directory.exists()) {
				File[] files = directory.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.isDirectory()) {
							deleteDirectory(file);
						} else {
							file.delete();
						}
					}
				}
			}
			return directory.delete();
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			File projectsDirectory = new File(DEFAULT_ROOT);
			return !projectsDirectory.exists() || deleteDirectory(projectsDirectory);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			MainActivity activity = activityReference.get();
			if (activity == null || activity.isFinishing()) {
				return;
			}

			if (result) {
				Toast.makeText(activity, R.string.projects_removed, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(activity, R.string.projects_remove_failed, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
