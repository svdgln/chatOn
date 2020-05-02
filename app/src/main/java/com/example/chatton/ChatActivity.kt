package com.example.chatton

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*

@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {

    private lateinit var MessengerUserName: String
    private lateinit var MessengerReceiverID: String
    private lateinit var MessageSenderID: String
    private lateinit var auth: FirebaseAuth
    private lateinit var UserRef: DatabaseReference
    private lateinit var RootRef: DatabaseReference
    private lateinit var GroupRef: DatabaseReference
    private lateinit var MessegeRef: DatabaseReference
    private lateinit var UserProfileImage: StorageReference
    var sendMessage = ArrayList<String>()
    var receivedMessage = ArrayList<String>()
    var Allmessages = ArrayList<String>()
    lateinit var userMessageList: RecyclerView
    lateinit var file_buton: Button
    lateinit var image_buton: Button
    var messagList = ArrayList<Messages>()
    lateinit var messageAdapter: MessageAdapter
    var checker:String = ""
    var myUrl:String = ""
    private lateinit var fileUri:Uri
    private lateinit var uploadTask: UploadTask
    private lateinit var progressBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        MessengerUserName = intent.extras?.get("name").toString()
        MessengerReceiverID = intent.extras?.get("uid").toString()
        setTitle(MessengerUserName)

        val buton = findViewById<Button>(R.id.buton)
        val edit_text = findViewById<EditText>(R.id.edit_text)
        auth = FirebaseAuth.getInstance()
        MessageSenderID = auth.currentUser!!.uid
        UserRef = FirebaseDatabase.getInstance().reference.child("Users")
        UserProfileImage = FirebaseStorage.getInstance().reference.child("Image Files")
        RootRef = FirebaseDatabase.getInstance().reference
        MessegeRef =
            FirebaseDatabase.getInstance().reference.child("Messages").child(MessageSenderID)
                .child(MessengerReceiverID)

        messageAdapter = MessageAdapter(messagList)
        userMessageList = findViewById<RecyclerView>(R.id.chats_list)
        userMessageList.layoutManager = LinearLayoutManager(this)
        userMessageList.adapter = messageAdapter

        progressBar = ProgressDialog(this)
        file_buton = findViewById(R.id.file_buton)
        file_buton.setOnClickListener(View.OnClickListener {
            var options = arrayOf<CharSequence>("Image" , "PDF Files" , "Ms Word Files")

            val alert = AlertDialog.Builder(this)
            alert.setTitle("Select the File")
            alert.setCancelable(false);
            alert.setIcon(R.drawable.ic_child_care_black_24dp);

            alert.setItems(options,DialogInterface.OnClickListener { dialogInterface, i ->
                if (i == 0) {
                    checker = "image"
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setType("image/*")
                    startActivityForResult(Intent.createChooser(intent, "select image"),438)
                }
                if (i == 1) {
                    checker = "pdf"
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setType("application/pdf")
                    startActivityForResult(Intent.createChooser(intent, "select pdf file"),438)
                }
                if (i == 2) {
                    checker = "docx"
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setType("application/msword")
                    startActivityForResult(Intent.createChooser(intent, "select ms word file"),438)
                }
            })

            alert.show()

        })


        buton.setOnClickListener {
            sendMessege()
        }

    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==438 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            progressBar.setTitle("Sending Image")
            progressBar.setMessage("We are sending image..")
            progressBar.setCanceledOnTouchOutside(false)
            progressBar.show()
            fileUri = data.data!!
            if (!checker.equals("image")) {

                val storageReference:StorageReference= FirebaseStorage.getInstance().reference.child("Document Files")

                val messegeSenderRef = "Messages/" + MessageSenderID + "/" + MessengerReceiverID
                val messegeReceiverRef = "Messages/" + MessengerReceiverID + "/" + MessageSenderID

                val userMessageKeyRef: DatabaseReference =
                    RootRef.child("Messages").child(MessageSenderID).child(MessengerReceiverID).push()
                val messagePushID: String = userMessageKeyRef.key.toString()

                val filepath:StorageReference = storageReference.child(messagePushID )
                filepath.putFile(fileUri).addOnCompleteListener(OnCompleteListener {task ->
                    if (task.isSuccessful) {

                        var messageTextBody: HashMap<String, String> = HashMap<String, String>()
                        messageTextBody.put("message", "https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/Document%20Files%2F" +
                                messagePushID.toString()+ "?alt=media&" )
                        messageTextBody.put("name", fileUri.lastPathSegment!!)
                        messageTextBody.put("type", checker)
                        messageTextBody.put("from", MessageSenderID)
                        messageTextBody.put("to",MessengerReceiverID)
                        messageTextBody.put("messageID",messagePushID)

                        var messageBodyDetail: HashMap<String, Any> = HashMap<String, Any>()
                        messageBodyDetail.put(messegeSenderRef + "/" + messagePushID, messageTextBody)
                        messageBodyDetail.put(messegeReceiverRef + "/" + messagePushID, messageTextBody)

                        RootRef.updateChildren(messageBodyDetail)
                        progressBar.dismiss()


                    }
                }).addOnFailureListener(OnFailureListener {
                    progressBar.dismiss()
                    Toast.makeText(this, "Nothing Selected, Error", Toast.LENGTH_SHORT).show();
                }).addOnProgressListener { OnProgressListener<UploadTask.TaskSnapshot> {
                    val p:Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                    progressBar.setMessage(p.toString() + " % Uploading...")
                } }

            }
            else if (checker.equals("image")) {
                val storageReference:StorageReference= FirebaseStorage.getInstance().reference.child("Image Files")

                val messegeSenderRef = "Messages/" + MessageSenderID + "/" + MessengerReceiverID
                val messegeReceiverRef = "Messages/" + MessengerReceiverID + "/" + MessageSenderID

                val userMessageKeyRef: DatabaseReference =
                    RootRef.child("Messages").child(MessageSenderID).child(MessengerReceiverID).push()
                val messagePushID: String = userMessageKeyRef.key.toString()

                val filepath:StorageReference = storageReference.child(messagePushID )
                uploadTask = filepath.putFile(fileUri)
                uploadTask.continueWith { task ->
                    if (!task.isSuccessful){
                        throw task.exception!!
                    }
                    return@continueWith filepath.downloadUrl
                }.addOnCompleteListener(OnCompleteListener {task ->
                    if (task.isSuccessful) {
                        val downloadUri: Task<Uri>? = task.result
                       // myUrl = downloadUri.toString()

                        myUrl = "https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/Image%20Files%2F" +
                                messagePushID.toString()+ "?alt=media&"


                        var messageTextBody: HashMap<String, String> = HashMap<String, String>()
                        messageTextBody.put("message", myUrl )
                        messageTextBody.put("name", fileUri.lastPathSegment!!)
                        messageTextBody.put("type", checker)
                        messageTextBody.put("from", MessageSenderID)
                        messageTextBody.put("to",MessengerReceiverID)
                        messageTextBody.put("messageID",messagePushID)

                        var messageBodyDetail: HashMap<String, Any> = HashMap<String, Any>()
                        messageBodyDetail.put(messegeSenderRef + "/" + messagePushID, messageTextBody)
                        messageBodyDetail.put(messegeReceiverRef + "/" + messagePushID, messageTextBody)

                        RootRef.updateChildren(messageBodyDetail)
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    progressBar.dismiss()
                                    Toast.makeText(applicationContext, "Message sent", Toast.LENGTH_LONG).show()
                                }else{
                                    progressBar.dismiss()
                                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                                }
                                //edit_text.setText("")

                            })
                            edit_text.setText("")

                    }
                })

            }
            else{
                progressBar.dismiss()
                Toast.makeText(this, "Nothing Selected, Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun sendMessege() {
        val mesegeText: String = edit_text.text.toString()

        if (TextUtils.isEmpty(mesegeText)) {
            Toast.makeText(applicationContext, "Please write a messege", Toast.LENGTH_LONG).show()
        } else run {
            val messegeSenderRef = "Messages/" + MessageSenderID + "/" + MessengerReceiverID
            val messegeReceiverRef = "Messages/" + MessengerReceiverID + "/" + MessageSenderID

            val userMessageKeyRef: DatabaseReference =
                RootRef.child("Messages").child(MessageSenderID).child(MessengerReceiverID).push()
            val messagePushID: String = userMessageKeyRef.key.toString()

            var messageTextBody: HashMap<String, String> = HashMap<String, String>()
            messageTextBody.put("message", mesegeText)
            messageTextBody.put("type", "text")
            messageTextBody.put("from", MessageSenderID)
            messageTextBody.put("to",MessengerReceiverID)
            messageTextBody.put("messageID",messagePushID)

            var messageBodyDetail: HashMap<String, Any> = HashMap<String, Any>()
            messageBodyDetail.put(messegeSenderRef + "/" + messagePushID, messageTextBody)
            messageBodyDetail.put(messegeReceiverRef + "/" + messagePushID, messageTextBody)

            RootRef.updateChildren(messageBodyDetail)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Message sent", Toast.LENGTH_LONG).show()
                    }

                })

        }
        edit_text.setText("")


    }

    override fun onStart() {
        super.onStart()
        MessegeRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(datasnapshot: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(datasnapshot: DataSnapshot, p1: String?) {
                if (datasnapshot.exists()) {
                    // DisplayMessage(datasnapshot)
                }
            }

            override fun onChildAdded(datasnapshot: DataSnapshot, p1: String?) {
                RetrieveAndDisplayGroup(userMessageList , this@ChatActivity)

            }

            override fun onChildRemoved(datasnapshot: DataSnapshot) {
                RetrieveAndDisplayGroup(userMessageList , this@ChatActivity)
            }
        })
    }

    private fun RetrieveAndDisplayGroup(chatList: RecyclerView, context: Context) {
        MessegeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentuserID:String = auth.currentUser!!.uid
                val iterator = dataSnapshot.children.iterator()
                messagList.clear()
                while (iterator.hasNext()) {
                    val i =0
                    iterator.next().key?.let {
                            val from = dataSnapshot.child(it).child("from").getValue().toString()
                            val message =
                                dataSnapshot.child(it).child("message").getValue().toString()
                            val type = dataSnapshot.child(it).child("type").getValue().toString()
                            val to = dataSnapshot.child(it).child("to").getValue().toString()
                            val messageID = dataSnapshot.child(it).child("messageID").getValue().toString()
                            messagList.add(Messages(from, message, type , to,messageID))

                    }
                }

                messageAdapter.notifyDataSetChanged()

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}


    data class Messages(val from: String, val message: String, val type: String , val to:String , val messageID:String) {

    }

    class MessageAdapter(val userMessageList: ArrayList<Messages>) :
        RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

        private lateinit var auth: FirebaseAuth
        private lateinit var UserRef: DatabaseReference

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var receiver_message = itemView.findViewById<TextView>(R.id.receiver_message_text)
            var sender_message = itemView.findViewById<TextView>(R.id.sender_message_text)
            var receiver_image:ImageView = itemView.findViewById(R.id.receiver_message_picture)
            var sender_image:ImageView = itemView.findViewById(R.id.sender_message_picture)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent?.context)
                .inflate(R.layout.custom_messages, parent, false)
            auth = FirebaseAuth.getInstance()
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return userMessageList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val messageSenderID: String = auth.currentUser!!.uid
            val message: Messages = userMessageList.get(position)
            val fromUserID = message.from
            val fromUserMessageType = message.type

            UserRef = FirebaseDatabase.getInstance().reference.child("Users").child(fromUserID)
            UserRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //for image
                }

            })
            if (fromUserMessageType.equals("text")) {
                holder.receiver_message.visibility = View.INVISIBLE
                holder.receiver_image.visibility = View.INVISIBLE
                holder.sender_image.visibility = View.INVISIBLE

                if (fromUserID.equals(messageSenderID)) {
                    holder.sender_message.setBackgroundResource(R.drawable.sender_messages)
                    holder.sender_message.setTextColor(Color.BLACK)
                    holder.sender_message.setText(message.message)
                } else {
                    holder.sender_message.visibility = View.INVISIBLE
                    holder.receiver_message.visibility = View.VISIBLE

                    holder.receiver_message.setBackgroundResource(R.drawable.receiver_messages)
                    holder.receiver_message.setTextColor(Color.BLACK)
                    holder.receiver_message.setText(message.message)
                }
            } else if (fromUserMessageType.equals("image")) {
                holder.receiver_message.visibility = View.INVISIBLE
                holder.sender_message.visibility = View.INVISIBLE

                if (fromUserID.equals(messageSenderID)) {
                    holder.sender_image.visibility = View.VISIBLE
                    Picasso.get().load(message.message).into(holder.sender_image)
                } else {
                    holder.receiver_image.visibility = View.VISIBLE
                    Picasso.get().load(message.message).into(holder.receiver_image)
                }
            }
            else if (fromUserMessageType.equals("pdf") || fromUserMessageType.equals("docx")) {
                holder.receiver_message.visibility = View.INVISIBLE
                holder.sender_message.visibility = View.INVISIBLE

                if (fromUserID.equals(messageSenderID)) {
                    holder.sender_image.visibility = View.VISIBLE
                    Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/file.png?alt=media&token=8b6b5ff9-f2ac-47b3-8cfd-370991e398b4")
                        .into(holder.sender_image)
                } else {
                    holder.receiver_image.visibility = View.VISIBLE
                    Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/file.png?alt=media&token=8b6b5ff9-f2ac-47b3-8cfd-370991e398b4")
                        .into(holder.receiver_image)
                }
            }

            if (fromUserID.equals(messageSenderID)) {
               holder.itemView.setOnClickListener(View.OnClickListener {
                    if (userMessageList.get(position).type.equals("text")) {
                        var options = arrayOf<CharSequence>(
                            "Delete For Me",
                            "Cancel",
                            "Delete For Everyone"
                        )

                        val alert2 = AlertDialog.Builder(holder.itemView.context)
                        alert2.setTitle("Delete Message")
                        alert2.setCancelable(false);
                        alert2.setIcon(R.drawable.ic_child_care_black_24dp);

                        alert2.setItems(
                            options,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                if (i == 0) {
                                    deleteSendMessages(position,holder)
                                } else if (i == 2) {
                                    deleteMessagesForEveyone(position,holder)
                                }
                            })

                        alert2.show()

                    }

                     else if (userMessageList.get(position).type.equals("pdf") || userMessageList.get(position).type.equals("docx") ) {
                       var options = arrayOf<CharSequence>(
                           "Delete For Me",
                           "Download and View This Document",
                           "Cancel",
                           "Delete For Everyone"
                       )

                       val alert1 = AlertDialog.Builder(holder.itemView.context)
                       alert1.setTitle("Delete Message")
                       alert1.setCancelable(false);
                       alert1.setIcon(R.drawable.ic_child_care_black_24dp);

                       alert1.setItems(
                           options,
                           DialogInterface.OnClickListener { dialogInterface, i ->
                               if (i == 0) {
                                   deleteSendMessages(position,holder)
                               } else if (i == 1) {
                                   val intent = Intent(
                                       Intent.ACTION_VIEW,
                                       Uri.parse(userMessageList.get(position).message)
                                   )
                                   holder.itemView.context.startActivity(intent)
                               } else if (i == 3) {
                                   deleteMessagesForEveyone(position,holder)

                               }
                           })

                       alert1.show()

                   }

                   else if (userMessageList.get(position).type.equals("image")) {
                       var options = arrayOf<CharSequence>(
                           "Delete For Me",
                           "Show Picture",
                           "Cancel",
                           "Delete For Everyone"
                       )

                       val alert3 = AlertDialog.Builder(holder.itemView.context)
                       alert3.setTitle("Delete Message")
                       alert3.setCancelable(false);
                       alert3.setIcon(R.drawable.ic_child_care_black_24dp);

                       alert3.setItems(
                           options,
                           DialogInterface.OnClickListener { dialogInterface, i ->
                               if (i == 0) {
                                   deleteSendMessages(position,holder)

                               }
                               else if (i == 1){
                                   val intent = Intent(holder.itemView.context, ImageViewActivity::class.java)
                                   intent.putExtra("url",userMessageList.get(position).message)
                                   holder.itemView.context.startActivity(intent)
                               }
                               else if (i == 3) {
                                   deleteMessagesForEveyone(position,holder)

                               }
                           })

                       alert3.show()
                    }
                })
            }

            else{

                holder.itemView.setOnClickListener(View.OnClickListener {
                    if (userMessageList.get(position).type.equals("text")) {
                        var options = arrayOf<CharSequence>(
                            "Delete For Me",
                            "Cancel"
                        )

                        val alert2 = AlertDialog.Builder(holder.itemView.context)
                        alert2.setTitle("Delete Message")
                        alert2.setCancelable(false);
                        alert2.setIcon(R.drawable.ic_child_care_black_24dp);

                        alert2.setItems(
                            options,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                if (i == 0) {
                                    deleteReceivedMessages(position,holder)

                                }
                            })

                        alert2.show()

                    }

                    else if (userMessageList.get(position).type.equals("pdf") || userMessageList.get(position).type.equals("docx") ) {
                        var options = arrayOf<CharSequence>(
                            "Delete For Me",
                            "Download and View This Document",
                            "Cancel"
                        )

                        val alert1 = AlertDialog.Builder(holder.itemView.context)
                        alert1.setTitle("Delete Message")
                        alert1.setCancelable(false);
                        alert1.setIcon(R.drawable.ic_child_care_black_24dp);

                        alert1.setItems(
                            options,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                if (i == 0) {
                                    deleteReceivedMessages(position,holder)

                                } else if (i == 1) {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(userMessageList.get(position).message)
                                    )
                                    holder.itemView.context.startActivity(intent)
                                }
                            })

                        alert1.show()

                    }

                    else if (userMessageList.get(position).type.equals("image")) {
                        var options = arrayOf<CharSequence>(
                            "Delete For Me",
                            "Show Picture",
                            "Cancel"
                        )

                        val alert3 = AlertDialog.Builder(holder.itemView.context)
                        alert3.setTitle("Delete Message")
                        alert3.setCancelable(false);
                        alert3.setIcon(R.drawable.ic_child_care_black_24dp);

                        alert3.setItems(
                            options,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                if (i == 0) {
                                    deleteReceivedMessages(position,holder)

                                }
                                else if (i == 1){
                                    val intent = Intent(holder.itemView.context, ImageViewActivity::class.java)
                                    intent.putExtra("url",userMessageList.get(position).message)
                                    holder.itemView.context.startActivity(intent)
                                }

                            })

                        alert3.show()
                    }
                })
            }
        }

        fun deleteSendMessages( position:Int ,  holder:ViewHolder){
            val rootRef:DatabaseReference= FirebaseDatabase.getInstance().reference
            rootRef.child("Messages").child(userMessageList.get(position).from)
                .child(userMessageList.get(position).to)
                .child(userMessageList.get(position).messageID)
                .removeValue().addOnCompleteListener(OnCompleteListener {task ->
                    if (task.isSuccessful){
                        Toast.makeText(holder.itemView.context, "Deleted Successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_LONG).show()
                    }
                })

        }

        fun deleteReceivedMessages( position:Int ,  holder:ViewHolder){
            val rootRef:DatabaseReference= FirebaseDatabase.getInstance().reference
            rootRef.child("Messages").child(userMessageList.get(position).to)
                .child(userMessageList.get(position).from)
                .child(userMessageList.get(position).messageID)
                .removeValue().addOnCompleteListener(OnCompleteListener {task ->
                    if (task.isSuccessful){
                        Toast.makeText(holder.itemView.context, "Deleted Successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_LONG).show()
                    }
                })

        }

        fun deleteMessagesForEveyone( position:Int ,  holder:ViewHolder){
            val rootRef:DatabaseReference= FirebaseDatabase.getInstance().reference
            rootRef.child("Messages").child(userMessageList.get(position).to)
                .child(userMessageList.get(position).from)
                .child(userMessageList.get(position).messageID)
                .removeValue().addOnCompleteListener(OnCompleteListener {task ->
                    if (task.isSuccessful){
                        rootRef.child("Messages").child(userMessageList.get(position).from)
                            .child(userMessageList.get(position).to)
                            .child(userMessageList.get(position).messageID)
                            .removeValue().addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful){
                                    Toast.makeText(holder.itemView.context, "Deleted Successfully", Toast.LENGTH_LONG).show()
                                }
                            })

                    }
                    else{
                        Toast.makeText(holder.itemView.context, "Error", Toast.LENGTH_LONG).show()
                    }
                })

        }
    }

}





