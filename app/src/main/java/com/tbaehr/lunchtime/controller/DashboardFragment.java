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
package com.tbaehr.lunchtime.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbaehr.lunchtime.presenter.DashboardPresenter;
import com.tbaehr.lunchtime.view.DashboardViewContainer;
import com.tbaehr.lunchtime.view.IDashboardViewContainer;

/**
 * Created by timo.baehr@gmail.com on 25.11.16.
 */
public class DashboardFragment extends BaseFragment<IDashboardViewContainer, DashboardPresenter> {

    private DashboardViewContainer viewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewContainer = new DashboardViewContainer(getContext(), inflater, container);
        return viewContainer.getRootView();
    }

    @Override
    public IDashboardViewContainer getViewLayer() {
        return viewContainer;
    }

    @Override
    public Class<? extends DashboardPresenter> getTypeClazz() {
        return DashboardPresenter.class;
    }

    @Override
    public DashboardPresenter create() {
        return new DashboardPresenter(this);
    }

    public void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }
}
