/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package org.tzuchi.us.nca.mtc;

public final class R {
    public static final class array {
        public static final int update_freq_options=0x7f050000;
        public static final int update_freq_values=0x7f050001;
    }
    public static final class attr {
        /** <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int buttonBarButtonStyle=0x7f010001;
        /** <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static final int buttonBarStyle=0x7f010000;
    }
    public static final class color {
        public static final int black_overlay=0x7f060000;
        public static final int event_background=0x7f060001;
        public static final int event_lines=0x7f060002;
        public static final int event_margin=0x7f060003;
        public static final int event_text=0x7f060004;
    }
    public static final class dimen {
        public static final int event_margin=0x7f070000;
    }
    public static final class drawable {
        public static final int events=0x7f020000;
        public static final int ic_launcher=0x7f020001;
        public static final int notification=0x7f020002;
        public static final int prefs=0x7f020003;
        public static final int remove_event=0x7f020004;
        public static final int update=0x7f020005;
    }
    public static final class id {
        public static final int addEventTitle=0x7f0a0002;
        public static final int cancelButton=0x7f0a000f;
        public static final int check_box_auto_inquiry=0x7f0a000d;
        public static final int eventBeginTime=0x7f0a0004;
        public static final int eventEndTime=0x7f0a0005;
        public static final int eventName=0x7f0a0003;
        public static final int eventPlace=0x7f0a0006;
        public static final int eventsEditText=0x7f0a000b;
        public static final int eventsListView=0x7f0a000c;
        public static final int fullscreen_content=0x7f0a0000;
        public static final int fullscreen_content_controls=0x7f0a0001;
        public static final int no=0x7f0a0008;
        public static final int okButton=0x7f0a0010;
        public static final int rowBeginTime=0x7f0a000a;
        public static final int rowName=0x7f0a0009;
        public static final int spinner_inquiry_freq=0x7f0a000e;
        public static final int yes=0x7f0a0007;
    }
    public static final class layout {
        public static final int activity_mtcmain=0x7f030000;
        public static final int add_event=0x7f030001;
        public static final int event=0x7f030002;
        public static final int eventlist=0x7f030003;
        public static final int preferences=0x7f030004;
    }
    public static final class string {
        public static final int add_event_title=0x7f08000c;
        public static final int app_name=0x7f080000;
        public static final int auto_inquiry_prompt=0x7f080009;
        public static final int dummy_button=0x7f080001;
        public static final int events_feed=0x7f080003;
        public static final int inquiry_freq_prompt=0x7f08000a;
        public static final int menu_preferences=0x7f080008;
        public static final int menu_refresh=0x7f080007;
        public static final int menu_registered_events=0x7f080006;
        public static final int no=0x7f08000e;
        public static final int preferences=0x7f080005;
        public static final int registered_events=0x7f080004;
        public static final int unregister=0x7f08000b;
        public static final int welcome_text=0x7f080002;
        public static final int yes=0x7f08000d;
    }
    public static final class style {
        /** 
        Base application theme, dependent on API level. This theme is replaced
        by AppBaseTheme from res/values-vXX/styles.xml on newer devices.
    

            Theme customizations available in newer API levels can go in
            res/values-vXX/styles.xml, while customizations related to
            backward-compatibility can go here.
        

        Base application theme for API 11+. This theme completely replaces
        AppBaseTheme from res/values/styles.xml on API 11+ devices.
    
 API 11 theme customizations can go here. 

        Base application theme for API 14+. This theme completely replaces
        AppBaseTheme from BOTH res/values/styles.xml and
        res/values-v11/styles.xml on API 14+ devices.
    
 API 14 theme customizations can go here. 
         */
        public static final int AppBaseTheme=0x7f090000;
        /**  Application theme. 
 All customizations that are NOT specific to a particular API-level can go here. 
         */
        public static final int AppTheme=0x7f090001;
        public static final int ButtonBar=0x7f090003;
        public static final int ButtonBarButton=0x7f090004;
        public static final int FullscreenActionBarStyle=0x7f090005;
        public static final int FullscreenTheme=0x7f090002;
    }
    public static final class xml {
        public static final int userpreferences=0x7f040000;
    }
    public static final class styleable {
        /** 
         Declare custom theme attributes that allow changing which styles are
         used for button bars depending on the API level.
         ?android:attr/buttonBarStyle is new as of API 11 so this is
         necessary to support previous API levels.
    
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #ButtonBarContainerTheme_buttonBarButtonStyle org.tzuchi.us.nca.mtc:buttonBarButtonStyle}</code></td><td></td></tr>
           <tr><td><code>{@link #ButtonBarContainerTheme_buttonBarStyle org.tzuchi.us.nca.mtc:buttonBarStyle}</code></td><td></td></tr>
           </table>
           @see #ButtonBarContainerTheme_buttonBarButtonStyle
           @see #ButtonBarContainerTheme_buttonBarStyle
         */
        public static final int[] ButtonBarContainerTheme = {
            0x7f010000, 0x7f010001
        };
        /**
          <p>This symbol is the offset where the {@link org.tzuchi.us.nca.mtc.R.attr#buttonBarButtonStyle}
          attribute's value can be found in the {@link #ButtonBarContainerTheme} array.


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          @attr name android:buttonBarButtonStyle
        */
        public static final int ButtonBarContainerTheme_buttonBarButtonStyle = 1;
        /**
          <p>This symbol is the offset where the {@link org.tzuchi.us.nca.mtc.R.attr#buttonBarStyle}
          attribute's value can be found in the {@link #ButtonBarContainerTheme} array.


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          @attr name android:buttonBarStyle
        */
        public static final int ButtonBarContainerTheme_buttonBarStyle = 0;
    };
}