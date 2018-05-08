/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.pocketcodereset;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

class ResetPocketCodeAsync extends AsyncTask<PocketCodeProject, Void, Integer> {
	private static final String TAG = ResetPocketCodeAsync.class.getSimpleName();
	private final WeakReference<MainActivity> activityReference;
	private final AssetManager assetManager;

	private static final int SUCCESS = 0;
	private static final int DELETE_DIR_FAILED = 1;
	private static final int MKDIR_FAILED = 2;
	private static final int UNZIP_FAILED = 3;

	@IntDef({ SUCCESS, DELETE_DIR_FAILED, MKDIR_FAILED, UNZIP_FAILED })
	@Retention(RetentionPolicy.SOURCE)
	@interface ReturnCode {
	}

	ResetPocketCodeAsync(MainActivity activity) {
		this.activityReference = new WeakReference<>(activity);
		assetManager = activity.getAssets();
		activity.onResetStarted();
	}

	@Override
	protected @ReturnCode Integer doInBackground(PocketCodeProject... projects) {
		File projectsDirectory = new File(MainActivity.Companion.getDefaultRoot());
		if (projectsDirectory.exists() && !FileIO.INSTANCE.deleteDirectory(projectsDirectory)) {
			return DELETE_DIR_FAILED;
		}

		if (!projectsDirectory.mkdirs()) {
			return MKDIR_FAILED;
		}

		for(PocketCodeProject project : projects) {
			try {
				InputStream inputStream = assetManager.open(project.getFilename());
				ZipFile.INSTANCE.unzip(inputStream,
						MainActivity.Companion.getDefaultRoot() + project.getProjectName());
			} catch (IOException e) {
				Log.e(TAG, "Cannot unzip project", e);
				return UNZIP_FAILED;
			}
		}
		return SUCCESS;
	}

	@Override
	protected void onPostExecute(@ReturnCode Integer result) {
		MainActivity activity = activityReference.get();
		if (activity == null || activity.isFinishing()) {
			return;
		}

		switch (result) {
			case SUCCESS:
				Toast.makeText(activity, "Pocket Code successfully reset", Toast.LENGTH_LONG).show();
				break;
			case DELETE_DIR_FAILED:
				Toast.makeText(activity, "Can not delete Pocket Code directory", Toast.LENGTH_LONG).show();
				break;
			case MKDIR_FAILED:
				Toast.makeText(activity, "Can not create Pocket Code directory", Toast.LENGTH_LONG).show();
				break;
			case UNZIP_FAILED:
				Toast.makeText(activity, "Can not unzip Pocket Code project", Toast.LENGTH_LONG).show();
				break;
		}

		activity.onResetFinished();
	}
}
