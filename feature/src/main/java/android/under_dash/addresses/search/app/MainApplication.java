/*
 * Copyright (c) 2011-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package android.under_dash.addresses.search.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

public class MainApplication extends Application {
	private static final String TAG = "MainApplication";

	private static MainApplication appContext;
	private static Handler sUiHandler = null;
	private static Handler sBackgroundHandler = null;



	public static MainApplication get() {
		return appContext;
	}



    @Override
	public void onCreate() {
		super.onCreate();
		MainApplication.appContext = this;
		Log.d(TAG, "onCreate() called");
	}



	@Override
	public void onTerminate() {
		super.onTerminate();
	}


	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Log.d(TAG, "onTrimMemory() called with: level = [" + level + "]");
	}

	public static MainApplication getAppContext() {
		return appContext;
	}


	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	synchronized public static Handler getUiHandler(){
		if (sUiHandler==null){
			sUiHandler = new Handler(Looper.getMainLooper());
		}
		return sUiHandler;
	}

	synchronized public static Handler getBackgroundHandler(){
		if(sBackgroundHandler == null){
			HandlerThread thread = new HandlerThread("background-thread", Process.THREAD_PRIORITY_BACKGROUND);
			thread.start();
			sBackgroundHandler = new Handler(thread.getLooper());
		}
		return sBackgroundHandler;
	}

}
