package com.example.roaddocmanagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.roaddocmanagement.R
import com.example.roaddocmanagement.databinding.ActivityDocumentDetailsBinding
import com.example.roaddocmanagement.models.Board
import com.example.roaddocmanagement.utils.Constants

@Suppress("DEPRECATION")
class DocumentDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocumentDetailsBinding
    private lateinit var mBoardDetails: Board
    private var mDocTypeListPosition = -1
    private var mDocumentPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupActionBar()

        //Set the card name in the EditText for editing
        binding.etNameCardDetails.setText(mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)
    }

    //Inflate the menu file
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate menu to use in the action bar
        menuInflater.inflate(R.menu.menu_delete_document, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.action_delete_document -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

            actionBar.title =
                mBoardDetails.docTypeList[mDocTypeListPosition].documents[mDocumentPosition].name
        }
        binding.toolbarCardDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!! as Board
        }
        if (intent.hasExtra(Constants.DOC_TYPE_LIST_ITEM_POSITION)) {
            mDocTypeListPosition = intent.getIntExtra(Constants.DOC_TYPE_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.DOCUMENT_LIST_ITEM_POSITION)) {
            mDocumentPosition = intent.getIntExtra(Constants.DOCUMENT_LIST_ITEM_POSITION, -1)
        }
    }
}