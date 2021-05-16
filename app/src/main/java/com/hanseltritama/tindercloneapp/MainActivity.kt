package com.hanseltritama.tindercloneapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hanseltritama.tindercloneapp.adapter.CardAdapter
import com.hanseltritama.tindercloneapp.data.Cards
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CardAdapter
    private var i = 0

    private lateinit var mAuth: FirebaseAuth

    private var userSex: String? = null

    private var oppositeUserSex: String? = null

    private lateinit var listView: ListView
    private lateinit var cardList: List<Cards>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        checkSex()

        cardList = ArrayList<Cards>()

        adapter = CardAdapter(this, R.layout.item, cardList)

        val flingContainer: SwipeFlingAdapterView? = frame

        flingContainer?.adapter = adapter
        flingContainer?.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!")
                (cardList as ArrayList<Cards>).removeAt(0)
                adapter.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(this@MainActivity, "Left!", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                Toast.makeText(this@MainActivity, "Right!", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Ask for more data here
//                al.add("XML $i")
//                arrayAdapter!!.notifyDataSetChanged()
//                Log.d("LIST", "notified")
//                i++
            }

            override fun onScroll(scrollProgressPercent: Float) {
            }
        })

        logout_button.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginRegistrationActivity::class.java)
            startActivity(intent)
            finish()
            return@setOnClickListener
        }
    }

    private fun checkSex() {
        // Male DB
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val maleDb: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("Male")
        maleDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key.equals(user?.uid)) {
                    userSex = "Male"
                    oppositeUserSex = "Female"
                    getOppositeSexUser()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // Female DB
        val femaleDb: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("Female")
        femaleDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key.equals(user?.uid)) {
                    userSex = "Female"
                    oppositeUserSex = "Male"
                    getOppositeSexUser()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getOppositeSexUser() {
        val oppositeSexDb: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(oppositeUserSex ?: "")
        oppositeSexDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    val item = Cards(snapshot.key, snapshot.child("name").value.toString())
                    (cardList as ArrayList<Cards>).add(item)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}