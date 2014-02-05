// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.hawkbrowser.webviewtest;

import com.hawkbrowser.webkit.ChromeInitializer;

import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

public class ShellApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        ChromeInitializer.initialize(getApplicationContext());
    }
}
