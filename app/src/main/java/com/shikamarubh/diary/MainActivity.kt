package com.shikamarubh.diary

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shikamarubh.diary.model.Post
import com.shikamarubh.diary.ui.theme.DiaryTheme
import com.shikamarubh.diary.viewmodel.PostViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var db : FirebaseFirestore
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        database = Firebase.database
        db = Firebase.firestore

        setContent {
            DiaryTheme {
                val openDialog = remember { mutableStateOf(false) }
                val logining = remember { mutableStateOf(true) }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (logining.value) {
                        loginScreen(auth,logining)
                    } else {
                        val postViewModel = viewModel<PostViewModel>()
                        postViewModel.refresh()
                        mainScreen(openDialog,logining,auth,database,db,postViewModel)
                    }
                }
            }
        }
    }

//    fun login(email: String, pass: String) {
//        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this) {
//            task ->
//            if (task.isSuccessful)
//                Log.d("DEBUG", "Logging successful")
//            else
//                Log.d("DEBUG", "Logging fail")
//        }
//    }

//    fun createNewUser(email: String, pass: String) {
//        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this) {
//                task ->
//            if (task.isSuccessful)
//                Log.d("DEBUG", "Create new user successful")
//            else
//                Log.d("DEBUG", "Create new user fail")
//        }
//    }

//    fun resetPassword(email: String) {
//        auth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
//                task ->
//            if (task.isSuccessful)
//                Log.d("DEBUG", "Send email reset successful")
//            else
//                Log.d("DEBUG", "Send email reset fail")
//        }
//    }

//    fun signOut() {
//        auth.signOut()
//    }

//    fun postDataToRealtimeDB(data: String) {
//        // Write a message to the database
//        val myRef = database.getReference("message")
//        myRef.setValue(data).addOnCompleteListener(this) {
//                task ->
//            if (task.isSuccessful)
//                Log.d("DEBUG", "Post $data successful")
//            else
//                Log.d("DEBUG", "Post $data fail")
//        }
//    }

//    fun readDataFromRealtimeDB() {
//        val myRef = database.getReference("message")
//        // Read from the database
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.getValue<String>()
//                Log.d("DEBUG", "Read $value successful")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w("DEBUG", "Failed to read value.", error.toException())
//            }
//        })
//    }

//    fun postDataToFirestore() {
//        // Create a new user with a first and last name
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 1815
//        )
//
//        // Add a new document with a generated ID
//        db.collection("users")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d("DEBUG", "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("DEBUG", "Error adding document", e)
//            }
//    }
//
//    fun addPostData(data: Post) {
//        val myRefRoot = database.reference
//        myRefRoot.child("posts").push().setValue(data)
//            .addOnCompleteListener(this) {
//                    task ->
//                if (task.isSuccessful)
//                    Log.d("DEBUG", "Add post: author ${data.title} content ${data.content} successful")
//                else
//                    Log.d("DEBUG", "Add data fail")
//            }
//    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun mainScreen(openDialog : MutableState<Boolean>, logining: MutableState<Boolean>, auth: FirebaseAuth, database: FirebaseDatabase, db : FirebaseFirestore, postViewModel: PostViewModel) {
    if (openDialog.value)
        addRecord(openDialog,auth,database,postViewModel)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Journal App",
                        color = Color(0xffffffff)
                        )
                        },
                backgroundColor = Color(0xff795549),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Menu,
                            "menu",
                            tint = Color(0xffffffff))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        logining.value = true
                    }) {
                        Icon(Icons.Filled.MoreVert,
                            "menu",
                            tint = Color(0xffffffff))
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = { 
            FloatingActionButton(
                onClick = { openDialog.value = true },
                backgroundColor = Color(0xff91a5ae)) {
                Icon(imageVector = Icons.Filled.Edit,
                    contentDescription = "write",
                    tint = Color(0xfff6faf9))
            } 
        },
        content = {
            diaryList(postViewModel)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTimestamp(time : String) : String {
    val duration = Duration.between(LocalDateTime.parse(time,DateTimeFormatter.ISO_DATE_TIME),LocalDateTime.now())
    var timestamp = ""
    if (duration.toDays() > 6)
        timestamp = LocalDateTime.parse(time,DateTimeFormatter.ISO_DATE_TIME).format(
            DateTimeFormatter.ofPattern("LLLL dd"))
    else if (duration.toDays() > 0)
        timestamp = duration.toDays().toString() + " days ago"
    else if (duration.toHours() > 0)
        timestamp = duration.toHours().toString() + " hours ago"
    else if (duration.toMinutes() > 0)
        timestamp = duration.toMinutes().toString() + " minutes ago"
    else if (duration.seconds > 0)
        timestamp = duration.seconds.toString() + " seconds ago"
    else if (duration.toMillis() > 0)
        timestamp = "Just now"
    return timestamp
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun diaryList(postViewModel: PostViewModel) {
    LazyColumn {
        items(postViewModel.postList.value) {
            item ->
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start=2.dp,end=2.dp)
                ) {
                    Text(
                        text = getTimestamp(item.created_at!!),
                        color = Color(0xff6e6e6e),
                        modifier = Modifier.padding(10.dp))
                    Card(
                        modifier = Modifier.padding(5.dp),
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = Color(LocalContext.current.resources.getIntArray(R.array.rowColors)[item.color!!]),
                        elevation = 3.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start) {
                            Text(
                                text = LocalDateTime.parse(item.created_at,DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofPattern("hh:mm\na")),
                                modifier = Modifier.padding(start=20.dp,top=15.dp,bottom=15.dp,end=20.dp),
                                color = Color(0xff858679)
                                )
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(top = 15.dp, bottom = 15.dp)
                                ) {
                                Text(text = item.title!!,
                                    color = Color(0xff665d58),
                                    fontWeight = FontWeight.Bold)
                                Text(text = item.content!!)
                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun dialogTextField(text : MutableState<String>, label : String) {
    OutlinedTextField(value = text.value,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        label = { Text(text = label,
            color = Color(0xFF000000),
            fontWeight = FontWeight.Black)},
        onValueChange = {
            text.value = it },
        singleLine = true,
        textStyle = TextStyle(color = Color.Black),
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun addRecord(openDialog: MutableState<Boolean>,auth: FirebaseAuth, database: FirebaseDatabase,postViewModel: PostViewModel) {
    val title = remember { mutableStateOf("") }
    val content = remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        title = { Text(
            text = "Thêm nhật ký mới",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            )},
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                dialogTextField(text = title, label = "Nhập tiêu đề")
                dialogTextField(text = content, label = "Nhập nội dung")
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    modifier = Modifier.wrapContentWidth(),
                    onClick = {
                        openDialog.value = false
                        val userId = auth.currentUser?.uid
                        val myRef = database.getReference("$userId/posts")
                        val id = myRef.push().key
                        val lastColor = if (postViewModel.postList.value.isEmpty()) 0 else postViewModel.postList.value.first().color!!
                        val data = Post(id,title.value,content.value, (lastColor+1)%5,LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        myRef.child("$id").setValue(data)
                            .addOnCompleteListener {
                                    task ->
                                if (task.isSuccessful)
                                    Log.d("DEBUG", "Add post: author ${data.title} content ${data.content} created at ${data.created_at} successful")
                                else
                                    Log.d("DEBUG", "Add data fail")
                            }
                    },
                ) {
                    Text("Add", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(
                    modifier = Modifier.wrapContentWidth(),
                    onClick = { openDialog.value = false }
                ) {
                    Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun loginScreen(auth: FirebaseAuth,logining: MutableState<Boolean>) {
    val email = remember {
        mutableStateOf("")
    }
    val pass = remember {
        mutableStateOf("")
    }
    val pass2 = remember {
        mutableStateOf("")
    }
    val registering = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Icon(
            imageVector = Icons.Filled.Book,
            tint = Color(0xff7e3b07),
            contentDescription = "logo",
            modifier = Modifier
                .width(150.dp)
                .height(150.dp))
        Text(
            text = "Journal App",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 100.dp))
        loginTextField(text = email, label = "Email", placeholder = "Nhập email",Icons.Filled.Email,false)
        loginTextField(text = pass, label = "Mật khẩu", placeholder = "Nhập mật khẩu",Icons.Filled.Lock)
        if (registering.value) {
            loginTextField(text = pass2, label = "Nhập lại mật khẩu", placeholder = "Nhập lại mật khẩu",Icons.Filled.Lock)
            Button(
                onClick = {
                      if (pass.value == pass2.value) {
                          auth.createUserWithEmailAndPassword(email.value,pass.value).addOnCompleteListener {
                                  task ->
                              if (task.isSuccessful) {
                                  logining.value = false
                                  Log.d("DEBUG", "Create new user successful")
                              }
                              else
                                  Log.d("DEBUG", "Create new user fail")
                          }
                      }
                },
                content = { Text(
                    text = "Đăng ký",
                    fontWeight = FontWeight.Bold
                ) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 100.dp, end = 100.dp, top = 10.dp, bottom = 10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffec7b01)))
            TextButton(
                onClick = { registering.value = false },
                content = { Text(text = "Đăng nhập") },
                modifier = Modifier.padding(10.dp))
        } else {
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email.value,pass.value).addOnCompleteListener {
                            task ->
                        if (task.isSuccessful) {
                            logining.value = false
                            Log.d("DEBUG", "Logging successful")
                        }
                        else
                            Log.d("DEBUG", "Logging fail")
                    }
                },
                content = { Text(
                    text = "Đăng nhập",
                    fontWeight = FontWeight.Bold
                ) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 100.dp, end = 100.dp, top = 10.dp, bottom = 10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffec7b01)))
            TextButton(
                onClick = { registering.value = true },
                content = { Text(text = "Đăng ký") },
                modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun loginTextField(text: MutableState<String>, label: String, placeholder: String, icon: ImageVector, hideText: Boolean = true) {
    TextField(
        modifier = Modifier.padding(bottom = 10.dp),
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        visualTransformation = if (hideText) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = { Icon(imageVector = icon, contentDescription = "email") },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        )
    )
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DiaryTheme {
        Greeting("Android")
    }
}