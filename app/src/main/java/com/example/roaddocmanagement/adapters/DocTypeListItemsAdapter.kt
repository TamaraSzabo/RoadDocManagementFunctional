package com.example.roaddocmanagement.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.roaddocmanagement.R
import com.example.roaddocmanagement.activities.DocListActivity
import com.example.roaddocmanagement.models.DocType
import androidx.recyclerview.widget.LinearLayoutManager


open class DocTypeListItemsAdapter(private val context: Context, private var list: ArrayList<DocType>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // Inflate the item view which is designed in the XML layout file
        val view = LayoutInflater.from(context).inflate(R.layout.item_doc_type, parent, false)

        // Calculate the width and height dynamically to use the whole screen
        val displayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        //val height = displayMetrics.heightPixels

        val layoutParams = LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adapterPosition = holder.adapterPosition
        val model = list[adapterPosition]

        if (holder is MyViewHolder) {
            val itemView = holder.itemView

            val tvAddDocTypeList = itemView.findViewById<TextView>(R.id.tv_add_doc_type_list)
            val llDocTypeItem = itemView.findViewById<LinearLayout>(R.id.ll_doc_type_item)
            val cvAddDocTypeName = itemView.findViewById<CardView>(R.id.cv_add_doc_type_name)
            val ibCloseListName = itemView.findViewById<ImageButton>(R.id.ib_close_list_name)
            val ibDoneListName = itemView.findViewById<ImageButton>(R.id.ib_done_list_name)
            val etDocTypeName = itemView.findViewById<EditText>(R.id.et_doc_type_name)
            val tvDocTypeTitle = itemView.findViewById<TextView>(R.id.tv_doc_type_title)

            if (adapterPosition == list.size - 1) {
                tvAddDocTypeList.visibility = View.VISIBLE
                llDocTypeItem.visibility = View.GONE
            } else {
                tvAddDocTypeList.visibility = View.GONE
                llDocTypeItem.visibility = View.VISIBLE
            }

            //Add a click event for showing the view for adding the task list name. And also set the task list title name
            tvDocTypeTitle.text = model.title

            tvAddDocTypeList.setOnClickListener {
                tvAddDocTypeList.visibility = View.GONE
                cvAddDocTypeName.visibility = View.VISIBLE
            }

            //Add a click event for hiding the view for adding the task list name
            ibCloseListName.setOnClickListener {
                tvAddDocTypeList.visibility = View.VISIBLE
                cvAddDocTypeName.visibility = View.GONE
            }

            //Add a click event for passing the task list name to the base activity function. To create a task list
            ibDoneListName.setOnClickListener {
                val listName = etDocTypeName.text.toString()

                if (listName.isNotEmpty()) {
                    if (context is DocListActivity) {
                        context.createDocTypeList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter Doc Type Name.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

           // val itemView2 = holder.itemView
            val ibEditListName = itemView.findViewById<ImageButton>(R.id.ib_edit_list_name)
            val etEditDocTypeName = itemView.findViewById<EditText>(R.id.et_edit_doc_type_name)
            val llTitleView = itemView.findViewById<LinearLayout>(R.id.ll_title_view)
            val cvEditDocTypeName = itemView.findViewById<CardView>(R.id.cv_edit_doc_type_name)

            ibEditListName.setOnClickListener {
                etEditDocTypeName.setText(model.title)
                llTitleView.visibility = View.GONE
                cvEditDocTypeName.visibility = View.VISIBLE
            }

            val ibCloseEditableView =
                itemView.findViewById<ImageButton>(R.id.ib_close_editable_view)
            ibCloseEditableView.setOnClickListener {
                llTitleView.visibility = View.VISIBLE
                cvEditDocTypeName.visibility = View.GONE
            }

            val ibDoneEditListName =
                itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name)

            ibDoneEditListName.setOnClickListener {
                val docTypeListName = etEditDocTypeName.text.toString()

                if (docTypeListName.isNotEmpty()) {
                    if (context is DocListActivity) {
                        context.updateDocTypeList(adapterPosition, docTypeListName, model)
                    }
                } else {
                    Toast.makeText(
                        context, "Please Enter a Doc Type Name (Personal/Auto)", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val ibDeleteList = itemView.findViewById<ImageButton>(R.id.ib_delete_list)
            ibDeleteList.setOnClickListener {
                alertDialogForDeleteDocTypeList(adapterPosition, model.title)
            }

            //Add a click event for adding a document in the doc type list
            val tvAddDoc = itemView.findViewById<TextView>(R.id.tv_add_doc)
            val cvAddDoc = itemView.findViewById<CardView>(R.id.cv_add_doc)
            val ibSelectPhoto = itemView.findViewById<ImageButton>(R.id.ib_select_photo)
            tvAddDoc.setOnClickListener {
                tvAddDoc.visibility = View.GONE
                cvAddDoc.visibility = View.VISIBLE
                ibSelectPhoto.visibility = View.VISIBLE

                //Add a click event for closing the view for card add in the task list
                val ibCloseDocName = itemView.findViewById<ImageButton>(R.id.ib_close_doc_name)
                ibCloseDocName.setOnClickListener {
                    tvAddDoc.visibility = View.VISIBLE
                    cvAddDoc.visibility = View.GONE
                    ibSelectPhoto.visibility = View.GONE
                }

                val ibDoneDocName = itemView.findViewById<ImageButton>(R.id.ib_done_doc_name)
                val etDocName = itemView.findViewById<EditText>(R.id.et_doc_name)

                ibSelectPhoto.setOnClickListener {
                    val docName = etDocName.text.toString()
                    if (this.context is DocListActivity) {
                        if (docName.isNotEmpty()) {
                            this.context.selectImageFromGallery(adapterPosition, docName)

                        }
                    }
                }

                //Add a click event for adding a card in the task list
                ibDoneDocName.setOnClickListener {
                    val docName = etDocName.text.toString()
                    if (docName.isNotEmpty()) {
                        if (context is DocListActivity) {
                            val imageUrl = context.getImageURL()

                            context.addDocumentToDocTypeList(adapterPosition, docName, imageUrl)
                        }
                    } else {
                        Toast.makeText(context, "Please Enter Document Details", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            //Load the cards list in the recyclerView
            val rvDocList = itemView.findViewById<RecyclerView>(R.id.rv_doc_list)
            rvDocList.layoutManager = LinearLayoutManager(context)
            rvDocList.setHasFixedSize(true)

            val adapter = DocumentListItemsAdapter(context, model.documents)
            rvDocList.adapter = adapter

            adapter.setOnClickListener(
                object : DocumentListItemsAdapter.OnClickListener{
                    override fun onClick(documentPosition: Int) {
                        if (context is DocListActivity){
                            context.documentDetails(adapterPosition, documentPosition)
                        }
                    }
                }
            )
        }
    }

    private fun alertDialogForDeleteDocTypeList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss() //Dialog will be dismissed

            if (context is DocListActivity) {
                context.deleteDocTypeList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss() //Dialog will be dismissed
        }
        //Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        //Set other dialog properties
        alertDialog.setCancelable(false) //will not allow user to cancel after the alert dialogue si shown
        alertDialog.show() //show the dialog to UI
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function to get density pixel from pixel
     */
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}