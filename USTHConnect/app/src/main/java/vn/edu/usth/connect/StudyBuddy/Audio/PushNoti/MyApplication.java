package vn.edu.usth.connect.StudyBuddy.Audio.PushNoti;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;

import org.linphone.core.*;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "call_notification_id";
    private Core core;

    @Override
    public void onCreate() {
        super.onCreate();

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");

        core = factory.createCore(null, null, this);
        core.addListener(new CoreListenerStub());
        core.start();
        createChannelNoti();
    }

    public Core getLinphoneCore() {
        return core;
    }

    private void createChannelNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CallNotification",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}

