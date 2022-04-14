package com.example.multi_login

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_admin.*

class Admin : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        registerBtn.setOnClickListener {
            createAccount()
        }

    }
    private fun createAccount() {
        val password = regpassword.text.toString()
        val username = regusername.text.toString()
        val email = emailreg.text.toString()
        val lastname = lastName.text.toString()
        val firstname = firstName.text.toString()
        val spin: String = userchoicereg.selectedItem.toString()
        val users = spin


        when {
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Username is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(lastname) -> Toast.makeText(this, "surname is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(firstname) -> Toast.makeText(this, "firstname is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(users) -> Toast.makeText(this, "User Type is required.", Toast.LENGTH_LONG).show()

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Registering")
                progressDialog.setMessage("wait for registration to complete")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            saveuserinfo()
                            progressDialog.dismiss()
                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "not registered $message", Toast.LENGTH_LONG)
                                .show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }

                    }
            }
        }
    }

    fun saveuserinfo() {

        val Users = userchoicereg.selectedItem.toString()
        val Firstname = firstName.text.toString()
        val Lastname = lastName.text.toString()
        val Username = regusername.text.toString()
        val Email = emailreg.text.toString()



        val progressDialog = ProgressDialog(this)



        database = FirebaseDatabase.getInstance().getReference("Users")
        val user = User(Firstname, Lastname, Username, Email, Users)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        database.child(uid).setValue(user).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                firstName.text.clear()
                lastName.text.clear()
                regusername.text.clear()
                regpassword.text.clear()
                emailreg.text.clear()
                progressDialog.dismiss()
                Toast.makeText(this, "Account has been created", Toast.LENGTH_LONG).show()


            } else {
                val message = task.exception!!.toString()
                Toast.makeText(this, "not registered $message", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }
        }
    }
}