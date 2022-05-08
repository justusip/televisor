package me.lorddoge.televisor.background;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import me.lorddoge.televisor.ui.ActLeanback;

public class ShortcutService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent e) {
        Log.w("ass", "Keycode: " + e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                Intent intent = new Intent(this, ActLeanback.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return super.onKeyEvent(e);
        }
        return super.onKeyEvent(e);
    }
}
