package com.example.gdcrry.layoutadapterdemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gdcrry.layoutadapterdemo.layout_adapter.XmlLayoutAdapter;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XmlLayoutAdapter.config(command -> new Thread(command).start(), Throwable::printStackTrace);
        ViewGroup root = findViewById(R.id.root_view_group);
        findViewById(R.id.btn_1).setOnClickListener(v -> XmlLayoutAdapter.of(getResources())
                .apply(R.layout.activity_main_scene_1)
                .to(root)
                .animate()
                .execute());
        findViewById(R.id.btn_2).setOnClickListener(v -> XmlLayoutAdapter.of(getResources())
                .apply(R.layout.activity_main_scene_2)
                .to(root)
                .animate(300)
                .execute(true));
        findViewById(R.id.btn_3).setOnClickListener(v -> XmlLayoutAdapter.of(getResources())
                .apply(R.layout.activity_main_scene_3)
                .to(root)
                .animate(700)
                .execute(true));
        findViewById(R.id.btn_4).setOnClickListener(v -> XmlLayoutAdapter.of(getResources())
                .apply(R.layout.activity_main)
                .to(root)
                .animate(new TransitionSet()
                        .addTransition(new Fade())
                        .addTransition(new ChangeBounds())
                        .setOrdering(TransitionSet.ORDERING_TOGETHER)
                        .setDuration(1000)
                ).execute(true, () -> Toast.makeText(this, "Transition done!", Toast.LENGTH_SHORT).show()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmlLayoutAdapter.release();
    }
}
