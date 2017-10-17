/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.distroscale.ads.hq;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.amazon.ads.IAds;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

/**
 * Some of the media player might be handling Ads internally thus we need a pass through module.
 */
public class HQAds implements IAds {

    /**
     * Name used for implementation creator registration to Module Manager.
     */
    final static String IMPL_CREATOR_NAME = HQAds.class.getSimpleName();

    /**
     * Store pass through data in here.
     */
    private Bundle extra;

    /**
     * Ad event interface.
     */
    private IAdsEvents mIAdsEvents;

    ///////////////////////////////////////////////////

    public static final String TAG = HQAds.class.getSimpleName();
    private String adTag = "https://bs.serving-sys.com/Serving?cn=display&amp;c=23&amp;pl=VAST&amp;pli=22065989&amp;PluID=0&amp;pos=1254&amp;ord=7,249,131,318,107,444,220&amp;cim=1";

    // Factory class for creating SDK objects.
    private ImaSdkFactory mSdkFactory;

    // The AdsLoader instance exposes the requestAds method.
    private AdsLoader mAdsLoader;

    // AdsManager exposes methods to control ad playback and listen to ad events.
    private AdsManager mAdsManager;

    private FrameLayout adsView;

    ////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Context context, FrameLayout frameLayout, Bundle extra) {

        this.extra = extra;

        adsView = frameLayout;

        // Create an AdsLoader.
        mSdkFactory = ImaSdkFactory.getInstance();
        mAdsLoader = mSdkFactory.createAdsLoader(context);
        // Add listeners for when ads are loaded and for errors.
        mAdsLoader.addAdErrorListener(new AdErrorEvent.AdErrorListener() {
            @Override
            public void onAdError(AdErrorEvent adErrorEvent) {
                Log.d(TAG, "ad error from adsloader");
            }
        });
        mAdsLoader.addAdsLoadedListener(new AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.
                mAdsManager = adsManagerLoadedEvent.getAdsManager();

                // Attach event and error event listeners.
                mAdsManager.addAdErrorListener(new AdErrorEvent.AdErrorListener() {
                    @Override
                    public void onAdError(AdErrorEvent adErrorEvent) {
                        Log.d(TAG, "ad error from adsmanager");
                    }
                });
                mAdsManager.addAdEventListener(new AdEvent.AdEventListener() {
                    @Override
                    public void onAdEvent(AdEvent adEvent) {
                        Log.i(TAG, "Event: " + adEvent.getType());

                        // These are the suggested event types to handle. For full list of all ad event
                        // types, see the documentation for AdEvent.AdEventType.
                        switch (adEvent.getType()) {
                            case LOADED:
                                // AdEventType.LOADED will be fired when ads are ready to be played.
                                // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                                // ad rules playlists, as the SDK will automatically start executing the
                                // playlist.
                                mAdsManager.start();
                                break;
//                            case CONTENT_PAUSE_REQUESTED:
//                                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
//                                // ad is played.
//                                mIsAdDisplayed = true;
//                                mVideoPlayer.pause();
//                                break;
//                            case CONTENT_RESUME_REQUESTED:
//                                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
//                                // and you should start playing your content.
//                                mIsAdDisplayed = false;
//                                mVideoPlayer.play();
//                                break;
//                            case ALL_ADS_COMPLETED:
//                                if (mAdsManager != null) {
//                                    mAdsManager.destroy();
//                                    mAdsManager = null;
//                                }
//                                break;
                            default:
                                break;
                        }
                    }
                });
                mAdsManager.init();
            }
        });
        requestAds(adTag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showPreRollAd() {

        if (mIAdsEvents != null) {
            mIAdsEvents.onAdSlotStarted(null);
            mIAdsEvents.onAdSlotEnded(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIAdsEvents(IAdsEvents iAdsEvents) {

        mIAdsEvents = iAdsEvents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentVideoPosition(double position) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPostRollAvailable() {

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActivityState(ActivityState activityState) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlayerState(PlayerState playerState) {

    }

    /**
     * Set pass through data.
     *
     * @param extra Pass through bundle.
     */
    public void setExtra(Bundle extra) {

        this.extra = extra;
    }

    /**
     * Get pass through data.
     *
     * @return Pass through bundle.
     */
    public Bundle getExtra() {

        return this.extra;
    }

    private void requestAds(String adTagUrl) {
        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer(adsView);

        // Create the ads request.
        AdsRequest request = mSdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setAdDisplayContainer(adDisplayContainer);

//        request.setContentProgressProvider(new ContentProgressProvider() {
//            @Override
//            public VideoProgressUpdate getContentProgress() {
//                if (mIsAdDisplayed || mVideoPlayer == null || mVideoPlayer.getDuration() <= 0) {
//                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
//                }
//                return new VideoProgressUpdate(mVideoPlayer.getCurrentPosition(),
//                        mVideoPlayer.getDuration());
//            }
//        });

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);
    }
}
