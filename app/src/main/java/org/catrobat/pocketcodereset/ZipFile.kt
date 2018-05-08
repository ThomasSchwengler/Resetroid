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

package org.catrobat.pocketcodereset

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipInputStream

internal object ZipFile {
    private const val BUFFER_SIZE = 4096

    @Throws(IOException::class)
    fun unzip(inputStream: InputStream, targetLocation: String) {
        createDirectory(targetLocation)

        ZipInputStream(inputStream).use { zipInputStream ->
            while (true) {
                val zipEntry = zipInputStream.nextEntry ?: break
                if (zipEntry.isDirectory) {
                    createDirectory(targetLocation + zipEntry.name)
                } else {
                    val fileOutputStream = FileOutputStream(targetLocation + zipEntry.name)
                    try {
                        val buf = ByteArray(BUFFER_SIZE)
                        while (true) {
                            val len = zipInputStream.read(buf)
                            if (len < 0) break
                            fileOutputStream.write(buf, 0, len)
                        }
                    } finally {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createDirectory(directory: String) {
        val file = File(directory)
        if (file.isFile) {
            throw IOException("File exists at directory position: $directory")
        }
        if (!file.exists()) {
            file.mkdirs()
        }
    }
}
