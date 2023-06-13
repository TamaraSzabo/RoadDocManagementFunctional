package com.example.roaddocmanagement.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roaddocmanagement.R
import com.example.roaddocmanagement.adapters.DocTypeListItemsAdapter
import com.example.roaddocmanagement.databinding.ActivityDocListBinding
import com.example.roaddocmanagement.firebase.FirestoreClass
import com.example.roaddocmanagement.models.Board
import com.example.roaddocmanagement.models.DocType
import com.example.roaddocmanagement.models.Document
import com.example.roaddocmanagement.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Suppress("DEPRECATION")
class DocListActivity : BaseActivity() {
    private lateinit var binding: ActivityDocListBinding
    //private lateinit var recyclerView: RecyclerView
    private lateinit var mBoardDetails: Board

    private var mSelectedPosition: Int = -1
    private var mSelectedDocName: String = ""
    private var mSelectedImageFileUriDoc: Uri? = null
    private var mDocImageURL: String = ""
    private var mDocImageURL2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    mSelectedImageFileUriDoc = data.data
                    uploadDocImage() // Call the uploadDocImage() function here
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to select the image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Creating an adapter and layout manager for the RecycleView
/*
     private fun getPhotoList(): List<Doc>{ //PhotoItem
         val photoList = mutableListOf<Doc>()

         return photoList
     }

     private inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
         private val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)

         fun bind(doc: Doc){
             Glide.with(itemView)
                 .load(doc.photoUrl)
                 .into(photoImageView)
         }
     }
*/

//     private inner class PhotoAdapter(private val photoList: List<Doc>) : RecyclerView.Adapter<PhotoViewHolder>(){
//         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
//             val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_photo,parent,false)
//             return PhotoViewHolder(itemView)
//         }
//
//         override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
//             val photoItem = photoList[position]
//             holder.bind(photoItem)
//         }
//
//         override fun getItemCount(): Int {
//             return photoList.size
//         }
//     }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarDocListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        binding.toolbarDocListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: Board) {
        mBoardDetails = board

        hideProgressDialog()
        setupActionBar()

        val addTaskList = DocType(resources.getString(R.string.action_add_document))
        board.docTypeList.add(addTaskList)

        val rvDocList = findViewById<RecyclerView>(R.id.rv_doc_list)
        rvDocList.layoutManager =
            LinearLayoutManager(this@DocListActivity, LinearLayoutManager.HORIZONTAL, false)
        rvDocList.setHasFixedSize(true)

        val adapter = DocTypeListItemsAdapter(this, board.docTypeList)
        rvDocList.adapter = adapter

    }

    fun addUpdateDocListSuccess() {
        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@DocListActivity, mBoardDetails.documentId)
    }

    fun createDocTypeList(docTypeListName: String) {
        val docType = DocType(docTypeListName, FirestoreClass().getCurrentUserId())

        mBoardDetails.docTypeList.add(0, docType)
        mBoardDetails.docTypeList.removeAt(mBoardDetails.docTypeList.size - 1) //removing the last entry of the DocTypeList

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateDocTypeList(this@DocListActivity, mBoardDetails)
        //TODO: modify back it it doesn't work
    }

    fun updateDocTypeList(position: Int, listName: String, model: DocType) {
        val docType = DocType(listName,model.createdBy)


        mBoardDetails.docTypeList[position] = docType
        mBoardDetails.docTypeList.removeAt(mBoardDetails.docTypeList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateDocTypeList(this@DocListActivity, mBoardDetails)
    }

    fun deleteDocTypeList(position: Int) {
        mBoardDetails.docTypeList.removeAt(position)
        mBoardDetails.docTypeList.removeAt(mBoardDetails.docTypeList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateDocTypeList(this@DocListActivity, mBoardDetails)
    }

    /*override fun onrequestpermissionsresult(
        requestcode: int,
        permissions: array<out string>,
        grantresults: intarray
    ) {
        super.onrequestpermissionsresult(requestcode, permissions, grantresults)
        if (requestcode == constants.read_storage_permission_code) {
            if (grantresults.isnotempty()
                && grantresults[0] == packagemanager.permission_granted
            ) {
                constants.showimagechooser(this)
            }
        } else {
            toast.maketext(
                this,
                "oops, you just denied the permission for the storage. ",
                toast.length_long
            ).show()
        }
    }*/

//TODO: When the add image button is clicked , it's not working
    private fun uploadDocImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "DOC_IMAGE" + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this, mSelectedImageFileUriDoc)
        )
        sRef.putFile(mSelectedImageFileUriDoc!!).addOnSuccessListener { taskSnapshot ->
            Log.i(
                "Doc Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mDocImageURL = uri.toString()
                mDocImageURL2 = mDocImageURL

                // Create a document with name, image URL, and other details
                if (mSelectedPosition != -1 && mSelectedDocName.isNotEmpty()) {
                    addDocumentToDocTypeList(mSelectedPosition, mSelectedDocName, mDocImageURL)
                }

                mSelectedPosition = -1 // Reset the position
                mSelectedDocName = "" // Reset the docName

                // Clear the selected image URI
                mSelectedImageFileUriDoc = null

                // Hide the progress dialog
                hideProgressDialog()

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

    fun getImageURL(): String {
            return mDocImageURL2
    }

    fun selectImageFromGallery(position: Int, docName: String) {
        // Save the position and docName to use when the image selection is complete
        mSelectedPosition = position
        mSelectedDocName = docName

        // Request the READ_STORAGE_PERMISSION_CODE permission if not granted already
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Constants.showImageChooser(this)
            uploadDocImage()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    fun addDocumentToDocTypeList(position: Int, docName: String, photo: String) {
        // Remove the last item
        mBoardDetails.docTypeList.removeAt(mBoardDetails.docTypeList.size - 1)
        val photoURL = getImageURL()

        val document = Document(docName, FirestoreClass().getCurrentUserId(), photoURL)

        val docsList = mBoardDetails.docTypeList[position].documents
        docsList.add(document)

        val docType = DocType(
            mBoardDetails.docTypeList[position].title,
            mBoardDetails.docTypeList[position].createdBy,
            docsList
        )

        mBoardDetails.docTypeList[position] = docType

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateDocTypeList(this@DocListActivity, mBoardDetails)
    }

}