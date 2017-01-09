/**
 * The MIT License (MIT) Copyright (c) 2017 Timo Bähr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or s
 * ubstantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tbaehr.lunchtime.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.tbaehr.lunchtime.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class HelpViewContainer implements IHelpViewContainer {

    private View rootView;

    @BindView(R.id.webview)
    WebView webView;

    public HelpViewContainer(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.content_help, container, false);
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void showWebContent(String webContent) {
        webView.loadData(webContent, "text/html", "UTF-8");
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
