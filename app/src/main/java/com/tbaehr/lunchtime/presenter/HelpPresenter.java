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
package com.tbaehr.lunchtime.presenter;

import com.propaneapps.tomorrow.presenter.BasePresenter;
import com.tbaehr.lunchtime.BuildConfig;
import com.tbaehr.lunchtime.LunchtimeApplication;
import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.view.HelpViewContainer;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class HelpPresenter extends BasePresenter<HelpViewContainer> {

    public HelpPresenter() {
        // ;
    }

    @Override
    public void bindView(HelpViewContainer view) {
        super.bindView(view);
        String versionName = LunchtimeApplication.getContext().getString(R.string.help_about_version, BuildConfig.VERSION_NAME);
        getView().setVersionName(versionName);
    }
}
