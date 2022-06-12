package com.shikamarubh.diary.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.shikamarubh.diary.model.Post

class PostViewModel : ViewModel() {
    fun refresh() {
        ref.removeEventListener(listener)
        ref = database.getReference("${auth.currentUser?.uid}/posts")
        listener = ref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var data : MutableList<Post> = mutableListOf()
                for (child in snapshot.children) {
                    data.add(child.getValue<Post>()!!)
                }
                data.sortByDescending { it.created_at }
                postList.value = data
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("DEBUG", "Failed to read value.", error.toException())
            }
        })
    }

    private var database: FirebaseDatabase = Firebase.database
    private var auth: FirebaseAuth = Firebase.auth
    private var ref : DatabaseReference = database.getReference("${auth.currentUser?.uid}/posts")
    var postList : MutableState<List<Post>> = mutableStateOf(emptyList())

    private var listener =
        ref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var data : MutableList<Post> = mutableListOf()
                for (child in snapshot.children) {
                    data.add(child.getValue<Post>()!!)
                }
                data.sortByDescending { it.created_at }
                postList.value = data
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("DEBUG", "Failed to read value.", error.toException())
            }
        })
}