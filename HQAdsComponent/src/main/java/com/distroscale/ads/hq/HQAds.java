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
//    private String adTag = "https://bs.serving-sys.com/Serving?cn=display&amp;c=23&amp;pl=VAST&amp;pli=22065989&amp;PluID=0&amp;pos=1254&amp;ord=7,249,131,318,107,444,220&amp;cim=1";
//    private String adTag = "https://serve.eyeviewads.com/vpaid/3045ae9f.xml?vjs=1&amp;so=tr:102000;vr:128436055;rsf:16,21,25,20;asf:17,3&amp;iid=a1d09b27d4ebef6eb8c3466a64a00b5a&amp;evuid=eb51be75a2bb2cd9ed8ed7de641b8fc1&amp;aw=mr%3DWcqykgAI94cKfuTOAAA5IvMNhYw3MLEbEp_klA%26bam%3D9535840976430%26bc%3DUSD%26ssp%3DADEX%26pscr%3D10000%26bcm%3D4000%26brid%3DWcqiAwAC77-9WQoBb01NB--_ve-_vQ%26br_didt%3Dcookie%26br_pdid%3Deb51be75a2bb2cd9ed8ed7de641b8fc1%26bld%3D13323%26br_pd%3Dclutchpoints.com%26glid%3D9016921%26sf%3D17%2C3%26br_os%3Dandroid%26xpubid%3Dcom.facebook.katana%26brdcr%3D0%26fid%3D27004%26vw-sc%3Dnull%26vcr-sc%3D0.571%26usf-sc%3D1.5824877716656889%26bpmode%3DEV_VPAID_JS%26br_dv%3D4";
//    private String adTag = "https://bs.serving-sys.com/Serving?cn=display&amp;c=23&amp;pl=VAST&amp;pli=21833538&amp;PluID=0&amp;pos=8510&amp;ord=4909727784201107456&amp;cim=1&amp;pcp=$$5522770491847021604$$&amp;pcp2=4432863%7C%7C2454551&amp;ebappid=$$$$";
//    private String adTag = "https://serve.eyeviewads.com/vpaid/b1bd5375.xml?vjs=1&amp;so=tr:98337;vr:129074437;rsf:25,20,21,16;asf:3,6,13,17,10&amp;iid=cf1f61ead63871e5bc1d59dc65b7f803&amp;evuid=9514e68617706e81c3cf60e96dcbc9dd&amp;aw=mr%3DWcrWrAACdNgKJAbRAA5kjdjTr5FxGDvF6MnyVg%26bam%3D3766156395000%26bc%3DUSD%26ssp%3DADEX%26pscr%3D10000%26bcm%3D2500%26pmpid%3D583%26pbdlid%3D500413515899603150%26brid%3DWe-_ve-_vV4ABu-_ve-_vQoBTlDvv70J77-9Yw%26br_didt%3Dcookie%26br_pdid%3D9514e68617706e81c3cf60e96dcbc9dd%26bld%3D13323%26br_pd%3Dwfsb.com%26glid%3D9003264%26sf%3D3%2C6%2C13%2C17%2C10%26br_os%3Dandroid%26xpubid%3D7959%26brdcr%3D0%26fid%3D25275%26vw-sc%3Dnull%26vcr-sc%3D0.0%26usf-sc%3D1.0%26bpmode%3DEV_VPAID_JS%26br_dv%3D4";
    private String adTag = "https://googleads.g.doubleclick.net/pagead/ads?client=ca-video-pub-3851171199385532&slotname=9292816986&ad_type=standardvideo&description_url=http%3A%2F%2Flatimes.com&videoad_start_delay=0";
//    private String adTag = "https://googleads.g.doubleclick.net/pagead/ads?client=ca-video-pub-3851171199385532&amp;slotname=9292816986&amp;ad_type=standardvideo&amp;description_url=http%3A%2F%2Flatimes.com&amp;videoad_start_delay=0";

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
                startEndAd();
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
                        startEndAd();
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
                            case CONTENT_PAUSE_REQUESTED:
                                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                                // ad is played.
//                                mIsAdDisplayed = true;
//                                mVideoPlayer.pause();
                                mIAdsEvents.onAdSlotStarted(null);
                                break;
                            case CONTENT_RESUME_REQUESTED:
                                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                                // and you should start playing your content.
//                                mIsAdDisplayed = false;
//                                mVideoPlayer.play();

                                mIAdsEvents.onAdSlotEnded(null);
                                break;
                            case ALL_ADS_COMPLETED:
                                if (mAdsManager != null) {
                                    mAdsManager.destroy();
                                    mAdsManager = null;
                                }
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

//        if (mIAdsEvents != null) {
//            mIAdsEvents.onAdSlotStarted(null);
//            mIAdsEvents.onAdSlotEnded(null);
//        }
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

    private void startEndAd() {
        if (mIAdsEvents != null) {
            mIAdsEvents.onAdSlotStarted(null);
            mIAdsEvents.onAdSlotEnded(null);
        }
    }
}
