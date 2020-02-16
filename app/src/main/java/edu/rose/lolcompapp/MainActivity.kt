package edu.rose.lolcompapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_info_page.*

class MainActivity : AppCompatActivity(),
    LoginFragment.OnLoginButtonPressedListener,
    InfoPageFragment.OnTeamSelectedListener {

    private var auth = FirebaseAuth.getInstance()
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initializeListeners()

    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    fun initializeListeners() {
        authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser

            if (user != null) {
                val playerInfoRef = FirebaseFirestore
                    .getInstance()
                    .collection("users")

                playerInfoRef
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener {
                        if (!it.exists()) {
                            playerInfoRef.document(user.uid).set(User(user.uid))
                        }
                    }
                Log.d(Constants.TAG, user.email.toString())
                switchToInfoPage(user.uid, user.email!!)
            } else {
                switchToLoginFragment()
            }
        }
    }

    fun switchToInfoPage(uid: String, email: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, InfoPageFragment.newInstance(this, uid, email))
        ft.commit()
    }

    fun switchToTeamFragment(team: Team, teamRef: DocumentReference) {
//        val playerInfoRef = FirebaseFirestore
//            .getInstance()
//            .collection("users")
//        var users_User: ArrayList<String>? = arrayListOf()
//        for (user in team.users) {
//            playerInfoRef.whereEqualTo("uid", user).get().addOnSuccessListener {
//                for(snp in it){
//                    users_User!!.add(User.fromSnapshot(snp).uid)
//                }
//            }
//        }


        val fragment = TeamPageFragment.newInstance(
            auth.currentUser!!.uid!!,
            teamRef,
            arrayListOf()
        )
        val ft = supportFragmentManager.beginTransaction()
        ft.addToBackStack("team")
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()
    }

    fun switchToLoginFragment() {

        val fragment = LoginFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_home -> {
                switchToInfoPage(auth.currentUser!!.uid, auth.currentUser!!.email!!)
                true
            }
            R.id.action_logout -> {
                auth.signOut()
                switchToLoginFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onLoginButtonPressed() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder().setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build()

        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    override fun onTeamSelected(team: Team, teamRef: DocumentReference) {
        switchToTeamFragment(team, teamRef)
    }


}
