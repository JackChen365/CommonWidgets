package com.cz.widgets.sample.zoomlayout.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_zoom_dataebase_sample.*
import kotlin.concurrent.thread


@SampleSourceCode
@RefRegister(title= R.string.zoom_database,desc = R.string.zoom_database_desc,category = R.string.zoom,priority = 1)
class DatabaseSampleActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    companion object{
        private const val LOADER_ID=100
        /**
         * Be careful.We should have a Authority in this URI. It must be your package name.
         * Or it will cause a problem.
         * Caused by: java.lang.SecurityException: Failed to find provider xxx for user 0; expected to find a valid ContentProvider for this authority
         */
        private val SAMPLE_URI= Uri.parse("content://com.cz.widgets.sample/sample_text")
    }
    private var sqLiteDatabase:SQLiteDatabase?=null
    private var loaderManager:LoaderManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_dataebase_sample)

        thread {
            checkDatabaseData()
        }
        tableLayout.post {
            loaderManager= LoaderManager.getInstance(this)
            loaderManager?.initLoader(LOADER_ID,null,this)
        }
    }

    private fun checkDatabaseData() {
        val sampleDatabaseHelper = SampleDatabaseHelper(applicationContext)
        val writableDatabase = sampleDatabaseHelper.writableDatabase
        sqLiteDatabase = writableDatabase
        //We delete all the data in this database table.
        var queryCursor: Cursor? = null
        try {
            queryCursor = writableDatabase.query(
                SampleDatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
            if (null == queryCursor || !queryCursor.moveToFirst()) {
                //Add a bunch of test data. Never mind if we do all the work in main thread. It's just a test.
                writableDatabase.beginTransaction()
                val dataProvider = DataManager.getDataProvider(this)
                val columnCount=10
                for (i in 0 until 1000) {
                    val start = DataProvider.RANDOM.nextInt(dataProvider.wordList.size-columnCount)
                    val wordList = dataProvider.getWordList(start, columnCount)
                    writableDatabase.execSQL(
                        "insert into " + SampleDatabaseHelper.TABLE_NAME + "(log1,log2,log3,log4,log5,log6,log7,log8,log9,log10) values(?,?,?,?,?,?,?,?,?,?)",
                        wordList.toTypedArray()
                    )
                }
                writableDatabase.setTransactionSuccessful()
                writableDatabase.endTransaction()
            }
            contentResolver.notifyChange(SAMPLE_URI,null)
        } finally {
            queryCursor?.close()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return object :SimpleCursorLoader(applicationContext){
            private val mObserver = ForceLoadContentObserver()

            override fun loadInBackground(): Cursor? {
                val cursor = sqLiteDatabase?.query(SampleDatabaseHelper.TABLE_NAME, arrayOf("_id,log1,log2,log3,log4,log5,log6,log7,log8,log9,log10"), null,null, null, null, null);
                if(null!=cursor){
                    cursor.registerContentObserver(mObserver)
                    cursor.setNotificationUri(contentResolver, SAMPLE_URI)
                }
                return cursor
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if(null!=cursor){
            val databaseAdapter = SimpleDatabaseAdapter(this, cursor)
            tableLayout.setAdapter(databaseAdapter)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

}
