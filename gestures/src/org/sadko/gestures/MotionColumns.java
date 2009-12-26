/*
  * Copyright (C) 2007 The Android Open Source Project
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
 
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
 
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
 
  */
package org.sadko.gestures;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MotionColumns implements BaseColumns {
    // This class cannot be instantiated
	
	public static final String AUTHORITY = "org.sadko.gestures.content";
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/motions");

    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.sadko.gestures.motion";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.sadko.gestures.motion";

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "modified DESC";
    /**
     * The title of the note
     * <P>Type: TEXT</P>
     */
    public static final String NAME = "name";
    public static final String TIME="time";
    /**
     * The note itself
     * <P>Type: TEXT</P>
     */
    //public static final String PATH = "path";
    public static final String [][] MATRIX=new String[3][3];

    /**
     * The timestamp for when the note was created
     * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
     */
    /**
     * The timestamp for when the note was last modified
     * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
     */
    public static final String MODIFIED_DATE = "modified";
    static{
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				MATRIX[i][j]="A"+i+""+j;
	}
}
