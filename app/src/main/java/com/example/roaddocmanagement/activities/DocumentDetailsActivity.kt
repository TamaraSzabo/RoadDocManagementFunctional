package com.example.roaddocmanagement.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.roaddocmanagement.R
import com.example.roaddocmanagement.databinding.ActivityDocumentDetailsBinding
import com.example.roaddocmanagement.firebase.FirestoreClass
import com.example.roaddocmanagement.models.Board
import com.example.roaddocmanagement.models.DocType
import com.example.roaddocmanagement.models.Document
import com.example.roaddocmanagement.utils.Constants
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class DocumentDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityDocumentDetailsBinding
    private lateinit var mBoardDetails: Board
    private var mDocTypeListPosition = -1
    private var mDocumentPosition = -1
    private var mSelectedExpirationDateMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupActionBar()

        //Set the card name in the EditText for editing
        binding.etNameDocumentDetails.setText(mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].name)
        binding.etNameDocumentDetails.setSelection(binding.etNameDocumentDetails.text.toString().length)

        val imageUrl =
            mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].image
        if (imageUrl.isNotEmpty()) {
            Glide
                .with(this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(binding.ivDocumentImage)
        }

        binding.btnUpdateDocumentDetails.setOnClickListener {
            if (binding.etNameDocumentDetails.text.toString().isNotEmpty()) {
                updateDocumentDetails()
            } else
                Toast.makeText(
                    this@DocumentDetailsActivity,
                    "Enter a document name.", Toast.LENGTH_SHORT
                ).show()
        }

        mSelectedExpirationDateMilliSeconds =
            mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].expirationDate

        if (mSelectedExpirationDateMilliSeconds > 0) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedExpirationDateMilliSeconds))
            binding.tvSelectExpirationDate.text = selectedDate
        }

        binding.tvSelectExpirationDate.setOnClickListener {
            showDataPicker()
        }
    }

    //Inflate the menu file
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate menu to use in the action bar
        menuInflater.inflate(R.menu.menu_delete_document, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_delete_document -> {
                alertDialogForDeleteCard(mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarDocumentDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

            actionBar.title =
                mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].name
            //actionBar.title = mBoardDetails.name
        }
        binding.toolbarDocumentDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.DOC_TYPE_LIST_ITEM_POSITION)) {
            mDocTypeListPosition = intent.getIntExtra(Constants.DOC_TYPE_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.DOCUMENT_LIST_ITEM_POSITION)) {
            mDocumentPosition = intent.getIntExtra(Constants.DOCUMENT_LIST_ITEM_POSITION, -1)
        }
    }

    //A function to get the result of add or updating the task list.
    fun addUpdateDocListSuccess() {

        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateDocumentDetails() {
        val document = Document(
            binding.etNameDocumentDetails.text.toString(),
            mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].createdBy,
            mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].image,
            mSelectedExpirationDateMilliSeconds
        )

        mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition] = document
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateDocTypeList(this@DocumentDetailsActivity, mBoardDetails)
    }

    private fun alertDialogForDeleteCard(documentName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                documentName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteDocument()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun deleteDocument() {
        val documentsList: ArrayList<Document> =
            mBoardDetails.docTypeList[mDocTypeListPosition].documents

        documentsList.removeAt(mDocumentPosition)

        val docTypeList: ArrayList<DocType> = mBoardDetails.docTypeList
        docTypeList.removeAt(docTypeList.size - 1)

        docTypeList[mDocTypeListPosition].documents = documentsList
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateDocTypeList(this@DocumentDetailsActivity, mBoardDetails)

    }

    /**
     * The function to show the DatePicker Dialog and select the due date.
     */
    private fun showDataPicker() {

        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                // Selected date it set to the TextView to make it visible to user.
                binding.tvSelectExpirationDate.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)

                mSelectedExpirationDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }

}