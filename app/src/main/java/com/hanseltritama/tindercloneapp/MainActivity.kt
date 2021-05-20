package com.hanseltritama.tindercloneapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

    private lateinit var usersDb: DatabaseReference

    private var currentId: String? = null

    private lateinit var cardList: List<Cards>

    private var suvCount: Int = 0
    private var sedanCount: Int = 0
    private var jeepCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usersDb = FirebaseDatabase.getInstance().reference.child("Users")

        mAuth = FirebaseAuth.getInstance()
        currentId = mAuth.currentUser?.uid

        checkSex()

        cardList = ArrayList<Cards>()

        adapter = CardAdapter(this, R.layout.item, cardList)

        setupFlingSwipeListener()

        logout_button.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginRegistrationActivity::class.java)
            startActivity(intent)
            finish()
            return@setOnClickListener
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayResult() {
        frame.visibility = View.GONE
        result_text.text = "SUV: $suvCount, Sedan: $sedanCount, Jeep: $jeepCount"
    }

    private fun setupFlingSwipeListener() {
        val flingContainer: SwipeFlingAdapterView? = frame

        flingContainer?.adapter = adapter
        flingContainer?.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!")
                (cardList as ArrayList<Cards>).removeAt(0)
                adapter.notifyDataSetChanged()
                if (cardList.isEmpty()) {
                    displayResult()
                }
            }

            override fun onLeftCardExit(dataObject: Any) {
                val cardObj: Cards = dataObject as Cards
                val userId: String? = cardObj.userId
                usersDb.child(oppositeUserSex ?: "")
                    .child(userId ?: "")
                    .child("connections")
                    .child("nope")
                    .child(currentId ?: "")
                    .setValue(true)
                Toast.makeText(this@MainActivity, "Nope!", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                val cardObj: Cards = dataObject as Cards
                val userId: String? = cardObj.userId
                val carModel: String? = cardObj.model
                usersDb.child(oppositeUserSex ?: "")
                    .child(userId ?: "")
                    .child("connections")
                    .child("yup")
                    .child(currentId ?: "")
                    .setValue(true)
                when (carModel) {
                    "SUV" -> suvCount++
                    "sedan" -> sedanCount++
                    "jeep" -> jeepCount++
                }
                Toast.makeText(this@MainActivity, "Yup!", Toast.LENGTH_SHORT).show()
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
                if (snapshot.exists()
                    && !snapshot.child("connections").child("nope").hasChild(currentId.toString())
                    && !snapshot.child("connections").child("yup").hasChild(currentId.toString())) {

                    val item = Cards(
                        snapshot.key,
                        snapshot.child("name").value.toString(),
                        snapshot.child("imageUrl").value.toString(),
                        snapshot.child("model").value.toString()
                    )
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