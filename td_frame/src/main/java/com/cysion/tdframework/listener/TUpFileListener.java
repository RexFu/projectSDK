/**
 * Copyright 2015 ZhangQu Li
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cysion.tdframework.listener;

import android.os.Handler;
import android.os.Message;

import com.cysion.tdframework.ex_okhttp.FlieUpHander;
import com.cysion.tdframework.ex_okhttp.ProgressModel;

/**
 * 请求体回调实现类，用于UI层回调
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 22:34
 */
public abstract class TUpFileListener implements FileUpListener {
    private boolean isFirst = false;

    //处理UI层的Handler子类
    private static class UIHandler extends FlieUpHander {
        public UIHandler(TUpFileListener aTUpFileListener) {
            super(aTUpFileListener);
        }

        @Override
        public void start(TUpFileListener aTUpFileListener, long currentBytes, long contentLength, boolean done) {
            if (aTUpFileListener !=null) {
                aTUpFileListener.onUIStart(currentBytes, contentLength, done);
            }
        }

        @Override
        public void progress(TUpFileListener aTUpFileListener, long currentBytes, long contentLength, boolean done) {
            if (aTUpFileListener !=null){
                aTUpFileListener.onUIProgress(currentBytes, contentLength, done);
            }
        }

        @Override
        public void finish(TUpFileListener aTUpFileListener, long currentBytes, long contentLength, boolean done) {
            if (aTUpFileListener !=null){
                aTUpFileListener.onUIFinish(currentBytes, contentLength,done);
            }
        }
    }

    //主线程Handler
    private final Handler mHandler = new UIHandler(this);

    @Override
    public void onProgress(long bytesWrite, long contentLength, boolean done) {
        //如果是第一次，发送消息
        if (!isFirst) {
            isFirst = true;
            Message start = Message.obtain();
            start.obj = new ProgressModel(bytesWrite, contentLength, done);
            start.what = FlieUpHander.START;
            mHandler.sendMessage(start);
        }

        //通过Handler发送进度消息
        Message message = Message.obtain();
        message.obj = new ProgressModel(bytesWrite, contentLength, done);
        message.what = FlieUpHander.UPDATE;
        mHandler.sendMessage(message);

        if(done) {
            Message finish = Message.obtain();
            finish.obj = new ProgressModel(bytesWrite, contentLength, done);
            finish.what = FlieUpHander.FINISH;
            mHandler.sendMessage(finish);
        }
    }

    /**
     * UI层回调抽象方法
     *
     * @param currentBytes    当前的字节长度
     * @param contentLength 总字节长度
     * @param done          是否写入完成
     */
    public abstract void onUIProgress(long currentBytes, long contentLength, boolean done);

    /**
     * UI层开始请求回调方法
     * @param currentBytes 当前的字节长度
     * @param contentLength 总字节长度
     * @param done 是否写入完成
     */
    public void onUIStart(long currentBytes, long contentLength, boolean done) {

    }

    /**
     * UI层结束请求回调方法
     * @param currentBytes 当前的字节长度
     * @param contentLength 总字节长度
     * @param done 是否写入完成
     */
    public void onUIFinish(long currentBytes, long contentLength, boolean done) {

    }
}
