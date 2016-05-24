package com.kongdy.hasee.shperemenu_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kongdy.hasee.shperemenu_android.view.SphereMenu;


public class MainActivity extends AppCompatActivity {

    private SphereMenu m_sphere_m;
    private SphereMenu.onSphereMenuItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_sphere_m = (SphereMenu) findViewById(R.id.m_sphere_m);
        listener = new SphereMenu.onSphereMenuItemClickListener() {
            @Override
            public void onClick(View v, int pos) {
                Log.v("main_click","pos:"+pos);
                Toast.makeText(getApplicationContext(),"click pos:"+pos,Toast.LENGTH_SHORT).show();
            }
        };
        m_sphere_m.addOnSphereMenuClickListener(listener);

    }
}
