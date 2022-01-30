package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.views.History.MCQHistory
import com.digitalandroidweb.androidregisterandlogin.views.Offers
import com.digitalandroidweb.androidregisterandlogin.views.PaymentHistory
import com.digitalandroidweb.androidregisterandlogin.views.ContactUs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onResume() {
        super.onResume()
        Log.d(DashboardActivity::class.simpleName, "onResume: ")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(DashboardActivity::class.simpleName, "onCreate: ")
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = getString(R.string.dashboard)
        setSupportActionBar(toolbar)
        loadHomePage()
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            Log.d(DashboardActivity::class.simpleName, "onCreate: Fab Clicked")
            //sessionManager!!.logout()
            // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //       .setAction("Action", null).show();
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    fun loadHomePage() {
        Log.d(DashboardActivity::class.simpleName, "loadHomePage: ")
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, MCQTypeList())
                .commit()
    }

    override fun onBackPressed() {
        Log.d(DashboardActivity::class.simpleName, "onBackPressed: ")
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(DashboardActivity::class.simpleName, "onCreateOptionsMenu: ")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(DashboardActivity::class.simpleName, "onOptionsItemSelected: ")
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
//        return if (id == R.id.action_settings) {
//            true
//        } else
        super.onOptionsItemSelected(item)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(DashboardActivity::class.simpleName, "onNavigationItemSelected: ")
        // Handle navigation view item clicks here.
        val id = item.itemId
        val fragmentManager = supportFragmentManager
        if (id == R.id.home) {
            supportActionBar?.title = getString(R.string.dashboard)
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, MCQTypeList())
                    .commit()
        } else if (id == R.id.trainings) {
            supportActionBar?.title = getString(R.string.payment_history)
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, PaymentHistory())
                    .commit()
        } else if (id == R.id.documents) {
            supportActionBar?.title = getString(R.string.our_offers)
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, Offers())
                    .commit()
        } else if (id == R.id.contacts) {
            supportActionBar?.title = getString(R.string.contacts)
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, ContactUs())
                    .commit()
        }else if (id == R.id.profile) {
            supportActionBar?.title = getString(R.string.mcq_history)
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, MCQHistory())
                    .commit()
        }else if (id == R.id.close) {
            showAppCloseDialog()
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showAppCloseDialog() {
        Log.d(DashboardActivity::class.simpleName, "showAppCloseDialog: ")
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                    finish()
                    val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton(getString(R.string.no), null)
                .show()
    }
}