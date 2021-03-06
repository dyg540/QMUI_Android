/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qmuiteam.qmui.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.view.LayoutInflaterCompat;

import com.qmuiteam.qmui.skin.QMUISkinLayoutInflaterFactory;
import com.qmuiteam.qmui.skin.QMUISkinManager;

public class QMUIBaseDialog extends AppCompatDialog {
    boolean cancelable = true;
    private boolean canceledOnTouchOutside = true;
    private boolean canceledOnTouchOutsideSet;
    private QMUISkinManager mSkinManager = null;

    public QMUIBaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void setSkinManager(@Nullable QMUISkinManager skinManager) {
        if(mSkinManager != null){
            mSkinManager.unRegister(this);
        }
        mSkinManager = skinManager;
        if(isShowing() && skinManager != null){
            mSkinManager.register(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSkinManager != null) {
            mSkinManager.register(this);
        }
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        LayoutInflater layoutInflater = super.getLayoutInflater();
        LayoutInflater.Factory2 factory2 = layoutInflater.getFactory2();
        if(factory2 instanceof QMUISkinLayoutInflaterFactory){
            LayoutInflaterCompat.setFactory2(layoutInflater,
                    ((QMUISkinLayoutInflaterFactory)factory2).cloneForLayoutInflaterIfNeeded(layoutInflater));
        }
        return layoutInflater;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSkinManager != null) {
            mSkinManager.unRegister(this);
        }
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (this.cancelable != cancelable) {
            this.cancelable = cancelable;
            onSetCancelable(cancelable);
        }
    }

    protected void onSetCancelable(boolean cancelable) {

    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !cancelable) {
            cancelable = true;
        }
        canceledOnTouchOutside = cancel;
        canceledOnTouchOutsideSet = true;
    }

    protected boolean shouldWindowCloseOnTouchOutside() {
        if (!canceledOnTouchOutsideSet) {
            TypedArray a =
                    getContext()
                            .obtainStyledAttributes(new int[]{android.R.attr.windowCloseOnTouchOutside});
            canceledOnTouchOutside = a.getBoolean(0, true);
            a.recycle();
            canceledOnTouchOutsideSet = true;
        }
        return canceledOnTouchOutside;
    }

    @Override
    public void dismiss() {
        Context context = getContext();
        if(context instanceof ContextWrapper){
            context = ((ContextWrapper)context).getBaseContext();
        }
        if(context instanceof Activity){
            Activity activity = (Activity) context;
            if(activity.isDestroyed() || activity.isFinishing()){
                return;
            }
            super.dismiss();
        }else{
            try{
                super.dismiss();
            }catch (Throwable ignore){
            }
        }
    }
}
