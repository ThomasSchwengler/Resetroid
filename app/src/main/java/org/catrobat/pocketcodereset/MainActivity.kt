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

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RESET_POCKET_CODE = 0

        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val defaultRoot = Environment.getExternalStorageDirectory().absolutePath + "/Pocket Code"
        val projects = arrayOf(
                PocketCodeProject("8946.catrobat", "/Rover Steuerung mit Nachrichten/"),
                PocketCodeProject("53609.catrobat", "/Trio Swerve v3/"),
                PocketCodeProject("52992.catrobat", "/koordinatenspiel/")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun checkPermissions(): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun requestPermissions(requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.size == 2
                && requestCode == RESET_POCKET_CODE
                && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            ResetPocketCodeAsync(this).execute(*projects)
        }
    }

    private fun resetPocketCode() {
        if (!checkPermissions()) {
            requestPermissions(RESET_POCKET_CODE)
        } else {
            ResetPocketCodeAsync(this).execute(*projects)
        }
    }

    fun onResetStarted() {
        findViewById<Button>(R.id.reset_pocket_code_button).isEnabled = false
    }

    fun onResetFinished() {
        findViewById<Button>(R.id.reset_pocket_code_button).isEnabled = true
    }

    @Suppress("UNUSED_PARAMETER")
    fun showPasswordPrompt(view: View) {
        val dialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton(R.string.password_prompt_positive_button, { _: DialogInterface, _: Int -> })
                .setTitle(R.string.password_prompt_title)
                .setView(R.layout.dialog_prompt)
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                { _: View ->
                    val passwordInput = dialog.findViewById<TextInputEditText>(R.id.prompt_password)
                            ?: throw IllegalArgumentException("Invalid Layout")
                    if (passwordInput.text?.toString() != "2010") {
                        passwordInput.error = getString(R.string.input_error_message)
                    } else {
                        dialog.dismiss()
                        resetPocketCode()
                    }
                }
        )
    }


}
