package com.example.silvercare.view.activities.ui.notifications

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.silvercare.R
import com.example.silvercare.adapter.NotificationAdapter
import com.example.silvercare.databinding.FragmentNotificationsBinding
import com.example.silvercare.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private var recyclerView: RecyclerView? = null
    private var notificationList : List<com.example.silvercare.model.Notification>? = null
    private var notificationAdapter: NotificationAdapter? =null
    private val viewModel by activityViewModels<HomeViewModel>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.rvNotifications
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(requireContext(),notificationList as ArrayList<com.example.silvercare.model.Notification>,requireActivity())
        recyclerView!!.adapter = notificationAdapter

        readNotifications()

        return root
    }

    private fun readNotifications() {
        val notiRef = FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        var notificationCount = 0
        notiRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (notificationList as ArrayList<com.example.silvercare.model.Notification>).clear()
                    for (snapshots in snapshot.children){
                        val notification = snapshots.getValue(com.example.silvercare.model.Notification::class.java)
                        (notificationList as ArrayList<com.example.silvercare.model.Notification>).add(notification!!)
                    }
                    Collections.reverse(notificationList)
                    notificationAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}