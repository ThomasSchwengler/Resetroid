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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class ZipFile {
	private static final int BUFFER_SIZE = 4096;

	private ZipFile() {
	}

	public static void unzip(InputStream inputStream, String targetLocation) throws IOException {
		createDirectoryIfNecessary(targetLocation);

		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		ZipEntry zipEntry;
		try {
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String name = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					createDirectoryIfNecessary(targetLocation + name);
				} else {
					FileOutputStream fileOutputStream = new FileOutputStream(targetLocation + name);
					try {
						byte[] buf = new byte[BUFFER_SIZE];
						int len;
						while ((len = zipInputStream.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, len);
						}
					} finally {
						fileOutputStream.flush();
						fileOutputStream.close();
					}
				}
			}
		} finally {
			inputStream.close();
		}
	}

	private static void createDirectoryIfNecessary(String directory) {
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
