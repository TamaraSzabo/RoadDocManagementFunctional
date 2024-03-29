package com.example.roaddocmanagement.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.roaddocmanagement.R
import com.example.roaddocmanagement.databinding.ActivityCreateBoardBinding
import com.example.roaddocmanagement.firebase.FirestoreClass
import com.example.roaddocmanagement.models.Board
import com.example.roaddocmanagement.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

@Suppress("DEPRECATION")
class CreateBoardActivity : BaseActivity() {
    lateinit var storageReference: StorageReference
    //?
    var firestoreClass: FirestoreClass? = null
    var mSelectedImageFileUri: Uri? = null
    private lateinit var binding: ActivityCreateBoardBinding
    private lateinit var mUserName: String
    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding.ivBoardImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnCreate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    fun createBoard() {
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())
        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this@CreateBoardActivity, board)
    }

    fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "BOARD_IMAGE" + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this, mSelectedImageFileUri)
        )
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.i(
                "Board Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mBoardImageURL = uri.toString()

                createBoard()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this,
                exception.message,
                Toast.LENGTH_LONG
            ).show()
            hideProgressDialog()
        }

    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(
                this,
                "Oops, you just denied the permission for the storage. ",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding.ivBoardImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}