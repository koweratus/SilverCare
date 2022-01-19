package com.example.silvercare.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.silvercare.R
import com.example.silvercare.model.Caretaker
import com.example.silvercare.model.User
import com.example.silvercare.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotificationAdapter(
    private val mContext: Context,
    private val mNotification: ArrayList<com.example.silvercare.model.Notification>,
    private val mActivity: Activity
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName: TextView = itemView.findViewById(R.id.username_notification)
        var text: TextView = itemView.findViewById(R.id.comment_notification)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notifcation = mNotification[position]

        if(notifcation.getText().equals("started following you"))
        {
            holder.text.text = "started following you"
        }else if(notifcation.getText() == mContext.getString(R.string.pill_schedule_reminder)){
            holder.text.text = mContext.getString(R.string.pill_schedule_reminder)

        }else if(notifcation.getText().contains("commented:")){
            holder.text.text = notifcation.getText()
                .replace("commented:","commented : ")

        }else{
            holder.text.text = notifcation.getText()
        }


        retrieveUser(holder.userName)

        holder.itemView.setOnClickListener{view ->
            if (notifcation.getIsPost()){
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("postId", notifcation.getPostId())
                editor.apply()
              //  view.findNavController().navigate(R.id.action_navigation_profile_to_postDetailsFragment)
            }else{
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileId", notifcation.getUserId())
                editor.apply()
               // view.findNavController().navigate(R.id.action_navigation_notifications_to_navigation_profile)
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }


    private fun retrieveUser(userName: TextView) = CoroutineScope(Dispatchers.IO).launch {
        try {

            val sharedPreferences =
                mActivity.getSharedPreferences(Constants.SILVERCARE_PREFERENCES, Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString(Constants.USER_DETAILS, "")
            val user = gson.fromJson(json, User ::class.java)

            val querySnapshot = Firebase.firestore.collection(Constants.CARETAKERS).get().await()
            val sb = StringBuilder()
            for(document in querySnapshot.documents) {
                val person = document.toObject<Caretaker>()
                sb.append("$person\n")

            }
            withContext(Dispatchers.Main) {
                if (sb.toString().split("=", ",")[11] == FirebaseAuth.getInstance().currentUser!!.uid) {
                            userName.text = sb.toString().split("=", ",")[3]
                }
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(mActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }


    }



}