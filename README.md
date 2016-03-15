# AndFix-Bad-Practices
AndFix应用到项目的一个小栗子。个人技术水平只能写写这些没有技术的Demo了，请大家不要吐槽~

# How To Work
1. 应用启动的时候，在 onCreate() 方法中获取友盟的在线参数来判断当前的应用版本是否有补丁需要下载，有则通过ThinDonloadManager来下载到SD下并且通过使用AndFix来加载到应用中。
2. 使用极光推送消息到该应用的版本需要下载补丁，如果应用收到了消息后，应用判断当前的版本是否需要下载补丁。如果应用没有收到消息的通知，则下次启动App的时候，获取友盟在线参数来判断是否需要下载补丁。

# Part Code

通过ThinDownloadManager下载补丁包，下载成功后使用AndFix加载补丁包的方法：

    public void downloadAndLoad(Context context, final PatchBean bean, String downloadUrl) {
        if (mLocalPreferencesHelper == null) {
            mLocalPreferencesHelper = new LocalPreferencesHelper(context, SPConst.SP_NAME);
        }
        Uri downloadUri = Uri.parse(downloadUrl);
        Uri destinationUri = Uri.parse(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + bean.url);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        // add patch at runtime
                        try {
                            // .apatch file path
                            String patchFileString = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + bean.url;
                            BaseApplication.mPatchManager.addPatch(patchFileString);
                            Log.d(TAG, "apatch:" + patchFileString + " added.");

                            //复制且加载补丁成功后，删除下载的补丁
                            File f = new File(patchFileString);
                            if (f.exists()) {
                                boolean result = new File(patchFileString).delete();
                                if (!result)
                                    Log.e(TAG, patchFileString + " delete fail");
                            }
	//                            mLocalPreferencesHelper.saveOrUpdate(SPConst.IsHavePathDownLoad, false);
                        } catch (IOException e) {
                            Log.e(TAG, "", e);
                        } catch (Throwable throwable) {

                        }
                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                        //下载失败的时候，标注标记位，等下次重新打开应用的时候重新下载
	//                        mLocalPreferencesHelper.saveOrUpdate(SPConst.IsHavePathDownLoad, true);
                        Log.e(TAG, "onDownloadFailed");

                    }

                    @Override
                    public void onProgress(int id, long totalBytes, int progress) {
                        Log.e(TAG, "progress:" + progress);
                    }
                });
        mDownloadManager = new ThinDownloadManager(THREAD_COUNT);
        mDownloadManager.add(downloadRequest);
    }

判断是否有补丁包需要下载的方法：

        public void comparePath(Context context, PatchBean RemoteBean) throws Exception {
        String pathInfo = mLocalPreferencesHelper.getString(SPConst.PATH_INFO);
        final PatchBean localBean = GsonUtils.getInstance().parseIfNull(PatchBean.class, pathInfo);
        //远程的应用版本跟当前应用的版本比较
        if (BaseApplication.VERSION_NAME.equals(RemoteBean.app_v)) {
            //远程的应用版本跟本地保存的应用版本一样，但补丁不一样，则需要下载重新
            /**
             *第一种情况：当本地记录的Bean为空的时候（刚安装的时候可能为空）并且远程的Bean的path_v不为空的时候需要下载补丁。
             * 第二种情况：当本地记录的path_v和远程Bean的path_v不一样的时候需要下载补丁。
             */
            if (localBean == null && !TextUtils.isEmpty(RemoteBean.path_v)
                    || localBean.app_v.equals(RemoteBean.app_v) &&
                    !localBean.path_v.equals(RemoteBean.path_v)) {
                downloadAndLoad(context, RemoteBean,
                        SPConst.URL_PREFIX + RemoteBean.url);
                String json = GsonUtils.getInstance().parse(RemoteBean);
                mLocalPreferencesHelper.saveOrUpdate(SPConst.PATH_INFO, json);
            } /*else {
                mLocalPreferencesHelper.saveOrUpdate(SPConst.IsHavePathDownLoad, false);
            }*/
        }
    }

# Platform Config
* 友盟在线参数
![](http://7xrnko.com1.z0.glb.clouddn.com/umneg_online.png)

* 极光推送自定义消息
![](http://7xrnko.com1.z0.glb.clouddn.com/jpush.png)

	
# Note
- 如果你在使用AndFix的时候遇到关于so问题的报错，可以看一下这个的，希望它能帮助你解决问题。
[https://github.com/zhonghanwen/AndFix-Ndk-Build-ADT](https://github.com/zhonghanwen/AndFix-Ndk-Build-ADT)
 
# Thanks
* [AndFix](https://github.com/alibaba/AndFix)
* [ThinDownloadManager](https://github.com/smanikandan14/ThinDownloadManager)
* JPush
* Umeng

# License

    Copyright 2016 zhonghanwen
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.