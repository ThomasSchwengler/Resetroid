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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainActivity extends AppCompatActivity {
	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pocket Code";
	private static final String ASSET_FILE = "8946.catrobat";
	private static final String ASSET_FILE_UNZIPPED = "/Rover Steuerung mit Nachrichten/";


	private static final int RESET_POCKET_CODE = 0;
	@IntDef({RESET_POCKET_CODE})
	@Retention(RetentionPolicy.SOURCE)
	@interface RequestCode {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean checkPermissions() {
		return ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
				&& (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
	}

	private void requestPermissions(@RequestCode int requestCode) {
		String[] permissions = new String[]{
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		};

		ActivityCompat.requestPermissions(this, permissions, requestCode);
	}

	@Override
	public void onRequestPermissionsResult(@RequestCode int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (grantResults.length == 2
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED
				&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
			if (requestCode == RESET_POCKET_CODE) {
				new ResetPocketCodeAsync(this, ASSET_FILE, ASSET_FILE_UNZIPPED).execute();
			}
		}
	}

	public void resetPocketCode(View view) {
		if (!checkPermissions()) {
			requestPermissions(RESET_POCKET_CODE);
		} else {
			new ResetPocketCodeAsync(this, ASSET_FILE, ASSET_FILE_UNZIPPED).execute();
		}
	}
}
