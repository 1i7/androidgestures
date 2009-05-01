package org.sadko.gestures;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ActivityColumns implements BaseColumns{
    public static final String PACK="package";
    public static final String MOTION_ID="MOTION_ID";
    public static final String ACTIVITY="activity";
    public static final String AUTHORITY = "org.sadko.gestures.content";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tasks");
    

}
