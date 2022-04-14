package com.example.multi_login

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginbtn.setOnClickListener {
            loginUser()
            //gotoAdmin()
        }

    }

    fun gotoAdmin(){
        var intent = Intent(this, Admin::class.java)
        startActivity(intent)
    }

    private fun loginUser() {
        val Password: String = loginpassword?.text.toString()
        val Email: String = loginEmail?.text.toString()


        when {
            TextUtils.isEmpty(Email) -> Toast.makeText(this, "Enter Email or Username", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(Password) -> Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show()


            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Authenticating...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {


                        val uid = FirebaseAuth.getInstance().currentUser!!.uid

                        val rootRef = FirebaseDatabase.getInstance().reference
                        val uidRef = rootRef.child("Users").child(uid)
                        val spin: String = userchioce.selectedItem.toString()
                        val valueEventListener: ValueEventListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                when {
                                    dataSnapshot.child("users").getValue(String::class.java) == "Admin" -> {
                                        if (spin.equals("Admin")){
                                            val intent = Intent(this@MainActivity,Admin::class.java)
                                            startActivity(intent)}
                                        else{
                                            Toast.makeText(this@MainActivity, "Select Your User-Type", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }

                                    dataSnapshot.child("users").getValue(String::class.java) == "Lecturer" -> {
                                        if (spin.equals("Lecturer")) {
                                            val intent = Intent(this@MainActivity, Lecturer::class.java)
                                            startActivity(intent)
                                        }else{
                                            Toast.makeText(this@MainActivity, "Select Your User-Type", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                    dataSnapshot.child("users").getValue(String::class.java) == "Student" -> {
                                        if (spin.equals("Student")) {
                                            val intent = Intent(this@MainActivity, Student::class.java)
                                            startActivity(intent)
                                        }else{
                                            Toast.makeText(this@MainActivity, "Select Your User-Type", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(ContentValues.TAG, databaseError.message)
                            }
                        }
                        uidRef.addListenerForSingleValueEvent(valueEventListener)
                        progressDialog.dismiss()
                    } else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "not registered $message", Toast.LENGTH_LONG)
                            .show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }

        }



    }
}