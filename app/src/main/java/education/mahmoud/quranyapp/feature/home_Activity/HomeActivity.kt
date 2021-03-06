package education.mahmoud.quranyapp.feature.home_Activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import education.mahmoud.quranyapp.R
import education.mahmoud.quranyapp.datalayer.Repository
import education.mahmoud.quranyapp.feature.ayahs_search.ShowSearchResults
import education.mahmoud.quranyapp.feature.bookmark_fragment.BookmarkFragment
import education.mahmoud.quranyapp.feature.download.DownloadActivity
import education.mahmoud.quranyapp.feature.feedback_activity.FeedbackActivity
import education.mahmoud.quranyapp.feature.gotoscreen.GoToSurah
import education.mahmoud.quranyapp.feature.listening_activity.ListenFragment
import education.mahmoud.quranyapp.feature.read_log.ReadLogFragment
import education.mahmoud.quranyapp.feature.scores.ScoreActivity
import education.mahmoud.quranyapp.feature.setting.SettingActivity
import education.mahmoud.quranyapp.feature.showSuraAyas.ShowAyahsActivity
import education.mahmoud.quranyapp.feature.show_sura_list.SuraListFragment
import education.mahmoud.quranyapp.feature.show_tafseer.TafseerDetails
import education.mahmoud.quranyapp.feature.splash.Splash
import education.mahmoud.quranyapp.feature.test_quran.TestFragment
import education.mahmoud.quranyapp.utils.Constants
import education.mahmoud.quranyapp.utils.log
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class HomeActivity : AppCompatActivity() {

    private var isPermissionAllowed: Boolean = false
    private val repository: Repository by inject()
    var ahays = 0

    private var currentID = 0
    private val readFragment by lazy { SuraListFragment() }
    private val tafseerFragment by lazy { TafseerDetails() }
    private val listenFragment by lazy { ListenFragment() }
    private val readLogFragment by lazy { ReadLogFragment() }
    private val testFragment by lazy { TestFragment() }
    private val bookmarkFragment by lazy { BookmarkFragment() }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        handleNavSelection(item.itemId)

        true
    }

    private fun handleNavSelection(itemId: Int) {
        if (itemId == currentID) return
        when (itemId) {
            R.id.navigation_read -> {
                openRead()
            }
            R.id.navigation_tafseer -> {
                openTafseer()

            }
            R.id.navigationListen -> {
                openListen()

            }
            R.id.navigation_test -> {
                openTest()

            }
            R.id.navigation_bookmarks -> {
                openBookmark()

            }
        }
        currentID = itemId

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate: start app")
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        goToSplash()
        ahays = repository.totlaAyahs

        toolbar?.let {
            setSupportActionBar(it)
        }

        listOf<String>("١٠", "١١", "١٢", "١٣").forEach {
            "ch $it ".log()
        }
    }

    fun afterSplash() {
        supportFragmentManager.popBackStackImmediate()
        openRead()
        // checkLastReadAndDisplayDialoge()
    }

    private fun determineToOpenOrNotSplash() {
        Log.d(TAG, "determineToOpenOrNotSplash:  n $ahays")
        if (ahays == 0) {
            Log.d(TAG, "determineToOpenOrNotSplash: ok  $ahays")
            goToSplash()
        }
    }

    private fun checkLastReadAndDisplayDialoge() {
        val last = repository.latestRead
        Log.d(TAG, "checkLastReadAndDisplayDialoge: $last")
        if (last >= 0) {
            displayDialoge(last)
            Log.d(TAG, "checkLastReadAndDisplayDialoge: @@ ")
        }
    }

    private fun displayDialoge(last: Int) {
        Log.d(TAG, "displayDialoge: ")
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.last_read_dialoge, null)
        val button = view.findViewById<Button>(R.id.btnOpenPage)
        button.setOnClickListener {
            openPage(last)
            dialog.dismiss()
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun openPage(last: Int) {
        gotoSuraa(last)
    }

    override fun onResume() {
        super.onResume()
        /* // used to update UI
         val id: Int = navigation.selectedItemId
         // reopen fragment
         navigation.selectedItemId = id*/
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openRead() {
        val a = supportFragmentManager.beginTransaction()
        a.replace(homeContainer.id, readFragment).commit()
    }

    private fun openTafseer() {
        val a = supportFragmentManager.beginTransaction()
        a.replace(homeContainer.id, tafseerFragment).commit()
    }

    private fun openListen() {
        val a = supportFragmentManager.beginTransaction()
        a.replace(homeContainer.id, listenFragment).commit()
    }

    private fun openTest() {
        val a = supportFragmentManager.beginTransaction()
        a.replace(homeContainer.id, testFragment).commit()
    }

    private fun openBookmark() {
        val a = supportFragmentManager.beginTransaction()
        a.replace(homeContainer.id, bookmarkFragment).commit()
    }

    fun acquirePermission() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        EasyPermissions.requestPermissions(PermissionRequest.Builder(this, RC_STORAGE, *perms).build())
    }

    fun goToSplash() {
        Log.d(TAG, "goToSplash:")

        supportFragmentManager.beginTransaction()
                .add(R.id.mainContainer, Splash())
                .addToBackStack(null)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionAllowed = true
            repository.permissionState = true
            listenFragment.downloadAyahs()
        } else if (requestCode == RC_STORAGE) {
            showMessage(getString(R.string.down_permission))
            repository.permissionState = false
            // user refuse to take permission
            openRead()
        }
    }

    private fun openSearch() {
        val openAcivity = Intent(this, ShowSearchResults::class.java)
        startActivity(openAcivity)
    }

    private fun gotoLastRead() {
        val index = repository.latestRead
        if (index == -1) {
            Toast.makeText(this, "You Have no saved recitation", Toast.LENGTH_SHORT).show()
            return
        }
        gotoSuraa(index)
    }

    private fun gotoSuraa(index: Int) {
        val openAcivity = Intent(this, ShowAyahsActivity::class.java)
        openAcivity.putExtra(Constants.LAST_INDEX, index)
        startActivity(openAcivity)
    }

    private fun openGoToSura() {
        val a = supportFragmentManager.beginTransaction()
        val goToSurah = GoToSurah()
        goToSurah.show(a, null)
    }

    private fun openSetting() {
        val openAcivity = Intent(this, SettingActivity::class.java)
        startActivity(openAcivity)
    }

    private fun gotoFeedback() {
        val openAcivity = Intent(this, FeedbackActivity::class.java)
        startActivity(openAcivity)
    }

    private fun gotoScore() {
        val openAcivity = Intent(this, ScoreActivity::class.java)
        startActivity(openAcivity)
    }

    private fun gotoDownload() {
        val openAcivity = Intent(this, DownloadActivity::class.java)
        startActivity(openAcivity)
    }

    private fun goToReadLog() {
        val transaction = supportFragmentManager.beginTransaction()
        val logFragment = ReadLogFragment()
        transaction.replace(homeContainer.id, logFragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionJump -> openGoToSura()
            R.id.actionSearch -> openSearch()
            R.id.actionSetting -> openSetting()
            R.id.actionGoToLastRead -> gotoLastRead()
            R.id.actionReadLog -> goToReadLog()
            R.id.actionScore -> gotoScore()
            R.id.actionDownload -> gotoDownload()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val RC_STORAGE = 1001
        private const val TAG = "HomeActivity"
    }
}