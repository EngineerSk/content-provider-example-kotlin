package com.engineersk.contentproviderexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.engineersk.contentproviderexample.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mListView: ListView
//    private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasReadContactPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        if (hasReadContactPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE_READ_CONTACTS
            )
        }

        Log.d(TAG, "onCreate: checkSelfPermission returned $hasReadContactPermission")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        mListView = findViewById(R.id.listView)
        binding.fab.setOnClickListener { view ->
            Log.d(TAG, "onCreate: fabOnclick starts:")
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                )

                val contacts = ArrayList<String>()
                cursor?.use {
                    while (it.moveToNext()) {
                        contacts.add(
                            it.getString(
                                Integer.parseInt(
                                    it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                                        .toString()
                                )
                            )
                        )
                    }
                }
                val adapter = ArrayAdapter(this, R.layout.contact_detail, R.id.name, contacts)
                mListView.adapter = adapter
            } else {
                Snackbar.make(
                    view, "Please, Grant Permission for contacts to be displayed",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Grant Access") {
                    Log.d(TAG, "onCreate: Snackbar starts...")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MainActivity,
                            Manifest.permission.READ_CONTACTS
                        )
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE_READ_CONTACTS
                        )
                    } else {
                        Log.d(TAG, "onCreate: Snackbar launching settings...")
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", this.packageName, null)
                        Log.d(TAG, "onCreate: Snackbar Onclick: URI is n$uri")
                        intent.data = uri
                        startActivity(intent)
                    }
                    Toast.makeText(it.context, "Action is clicked", Toast.LENGTH_SHORT).show()
                }.show()
            }
            Log.d(TAG, "onCreate: fab onclick ends")
        }

        Log.d(TAG, "onCreate: ends")
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        Log.d(TAG, "onRequestPermissionsResult: starts")
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            REQUEST_CODE_READ_CONTACTS -> {
//                if (grantResults.isNotEmpty()
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                ) {
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted...")
////                    true
//                } else {
//                    Log.d(TAG, "onRequestPermissionsResult: permission denied...")
////                    false
//                }
//            }
//        }
//
//        Log.d(TAG, "onRequestPermissionsResult: ends")
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}