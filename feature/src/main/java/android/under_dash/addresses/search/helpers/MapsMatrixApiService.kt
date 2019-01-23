package android.under_dash.addresses.search.helpers

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.text.format.DateUtils
import android.under_dash.addresses.search.app.SharedPrefManager
import android.under_dash.addresses.search.models.objectBox.Address
import android.under_dash.addresses.search.models.objectBox.AddressMap
import android.under_dash.addresses.search.models.matrixMapApi.DistanceResponse
import android.under_dash.addresses.search.utils.RestUtil
import android.util.Log

import org.apache.commons.collections4.ListUtils

import java.io.IOException
import java.util.ArrayList
import java.util.HashMap

class MapsMatrixApiService
    private constructor(context: Context, private val mStartPointAddresses: List<Address>, private val mDestinationAddresses: List<Address>, private val mOnDone: Runnable?)
    : AsyncTask<String, String, String>() {
        private var mCount: Int = 0
        private var mTotal: Int = 0
        @Suppress("DEPRECATION")
        private var mDialog: ProgressDialog = ProgressDialog(context)
        private var mLastCallToApi: Long = 0

    init {
        mLastCallToApi = 0
        mCount = 0
        mTotal = 0
    }

    override fun onPreExecute() {
        mDialog.setTitle("Importing Data into Secure DataBase")
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)
        mDialog.setIcon(android.R.drawable.ic_dialog_info)
        mDialog.show()
    }

    override fun onProgressUpdate(vararg values: String) {
        Log.e(TAG, "inonProgressUpdate values[0] = " + values[0])
        mDialog.setMessage("Please wait... " + values[0])
        super.onProgressUpdate(*values)
    }

    override fun doInBackground(vararg params: String): String {
        if (mStartPointAddresses.isEmpty() || mDestinationAddresses.isEmpty()) {
            Log.e(TAG, "##### ONE LIST IS EMPTY ##### aborting job")
            return ""
        }
        Log.i(TAG, "doInBackground: mStartPointAddresses.size() = " + mStartPointAddresses.size + " mDestinationAddresses.size() = " + mDestinationAddresses.size)
        startJob(mStartPointAddresses, mDestinationAddresses)
        return ""
    }

    override fun onPostExecute(data: String) {
        Log.i(TAG, "onPostExecute: ")
        if (mDialog.isShowing) {
            mDialog.dismiss()
        }
        mOnDone?.run()
    }

    private fun startJob(startPointList: List<Address>, destinationList: List<Address>) {
        Log.d(TAG, "in getDistanceInfoAndAddInDb startPointList.size = " + startPointList.size + " destinationList.size = " + destinationList.size)
        mCount = 0
        //The google api will exsept a maximum of 100 elements
        val targetSize = 10
        val startPointListOfList = ArrayList(ListUtils.partition(startPointList, targetSize))
        val destinationListOfList = ArrayList(ListUtils.partition(destinationList, targetSize))
        val originDestinationListOfList = ArrayList(destinationListOfList)

        mTotal = startPointListOfList.size * destinationListOfList.size
        publishProgress("Done with list partition")
        getDistanceInfoRecursion(startPointListOfList, destinationListOfList, originDestinationListOfList)

    }

    private fun getDistanceInfoRecursion(startPointListOfList: MutableList<List<Address>>,
                                         destinationListOfList: MutableList<List<Address>>,
                                         originDestinationListOfList: List<List<Address>>) {

        Log.d(TAG, "in getDistanceInfoRecursion sCount = " + mCount + " startPointListOfList.size = " + startPointListOfList.size
                + " destinationListOfList.size = " + destinationListOfList.size
                + " originDestinationListOfList.size = " + originDestinationListOfList.size)
        if (startPointListOfList.size == 0) {
            Log.d(TAG, "in getDistanceInfoRecursion END")
            return
        }

        if (destinationListOfList.size > 0) {
            mCount++
            val startPointList = startPointListOfList[0]
            val destinationList = destinationListOfList[0]
            destinationListOfList.removeAt(0)
            checkIfIntervalNeeded()
            getDistanceInfoAndAddInDb(startPointList, destinationList)
            getDistanceInfoRecursion(startPointListOfList, destinationListOfList, originDestinationListOfList)

        } else {
            startPointListOfList.removeAt(0)
            val newDestinationListOfList = ArrayList(originDestinationListOfList)
            getDistanceInfoRecursion(startPointListOfList, newDestinationListOfList, originDestinationListOfList)
        }
        publishProgress(mCount.toString() + " Done out of " + mTotal)
    }

    private fun checkIfIntervalNeeded() {
        if (mLastCallToApi > 0) {
            val lastCallInterval = mLastCallToApi - System.currentTimeMillis()
            val interval = MIN_API_CALL_INTERVAL - lastCallInterval
            Log.e(TAG, "in checkIfIntervalNeeded interval = $interval lastCallInterval = $lastCallInterval")
            if (interval > 0) {
                try {
                    Thread.sleep(interval)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
        mLastCallToApi = System.currentTimeMillis()
    }

    private fun getFormatDistanceInfo(startPointList: List<Address>): String {
        val destination = StringBuilder()
        for (address in startPointList) {
            destination.append(address.address).append("|")
        }
        return destination.toString()
    }


    private fun getDistanceInfoAndAddInDb(startPointList: List<Address>, destinationList: List<Address>) {
        val startPoint = getFormatDistanceInfo(startPointList)
        val destination = getFormatDistanceInfo(destinationList)
        Log.i(TAG, "in getDistanceInfoAndAddInDb: startPointList.size = " + startPointList.size + " destinationList.size = " + destinationList.size)
        val mapQuery = HashMap<String, String>()
        //mapQuery.put("units", "imperial"); metric is default
        mapQuery["origins"] = startPoint
        mapQuery["destinations"] = destination//+ "|" + cities[1] + "|" + cities[2]
        mapQuery["mode"] = "walking"
        mapQuery["key"] = SharedPrefManager.get().googleApiKey
        //        mapQuery.put("destinations[1]", cities[1]);
        //        mapQuery.put("destinations[2]", cities[2]);
        val client = RestUtil.getInstance().retrofit.create<DistanceApiClient>(DistanceApiClient::class.java)

        try {
            val body = client.getDistanceInfo(mapQuery).execute().body()
            Log.d(TAG, "onResponse call = " + if (body == null) "null" else body.status)
            if (body != null) {
                addRespondToDb(body, startPointList, destinationList)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    private fun addRespondToDb(body: DistanceResponse, startPointList: List<Address>, destinationList: List<Address>) {
        val rows = body.rows
        Log.d(TAG, "addRespondToDb: rows = " + rows.size + " " + body.destinationAddresses.size + " " + body.originAddresses.size)
        for (i in rows.indices) {
            val originAddress = startPointList[i]
            val elements = rows[i].elements
            Log.d(TAG, "addRespondToDb: elements = " + elements.size)
            for (y in elements.indices) {
                val element = elements[y]
                val destinationAddress = destinationList[y]
                if (destinationAddress != null) {
                    val distance = element.distance.value
                    val duration = element.duration.value
                    val distanceText = element.duration.text
                    val durationText = element.distance.text
                    AddressMap.add(distance, duration, distanceText, durationText, originAddress, destinationAddress)
                    Log.d(TAG, "getStartLatLong = " + destinationAddress + " Distance = " + element.duration.value +
                            " Duration = " + element.distance.value + " (address != null) = " + (destinationAddress != null))
                }
            }
        }
    }

    companion object {

        private val TAG = MapsMatrixApiService::class.java.simpleName
        private val MIN_API_CALL_INTERVAL = DateUtils.SECOND_IN_MILLIS * 10//A google restriction for sending a request one after the other
//        Call to this job will look like this
//        MapsMatrixApiService.build(context, startPointAddresses, destinationAddresses , Runnable {
//
//        }).execute()
        @JvmStatic
        fun build(context: Context, startPointAddressNameId: List<Address>, destinationAddressNameId: List<Address>, onDone: Runnable): MapsMatrixApiService {
            return MapsMatrixApiService(context, startPointAddressNameId, destinationAddressNameId, onDone)
        }
    }


}