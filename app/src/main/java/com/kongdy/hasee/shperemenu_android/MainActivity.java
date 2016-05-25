package com.kongdy.hasee.shperemenu_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kongdy.hasee.shperemenu_android.view.CircleImageView;
import com.kongdy.hasee.shperemenu_android.view.SphereMenu;


public class MainActivity extends AppCompatActivity {

    private SphereMenu m_sphere_m;
    private SphereMenu.onSphereMenuItemClickListener listener;
    private CircleImageView civ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_sphere_m = (SphereMenu) findViewById(R.id.m_sphere_m);
        civ = (CircleImageView) findViewById(R.id.home_btn);
        civ.setMyImageRes(R.mipmap.m_7);
        listener = new SphereMenu.onSphereMenuItemClickListener() {
            @Override
            public void onClick(View v, int pos) {
                m_sphere_m.closeMenu();
                Log.v("main_click","pos:"+pos);
                Toast.makeText(getApplicationContext(),"click pos:"+pos,Toast.LENGTH_SHORT).show();
            }
        };
        m_sphere_m.addOnSphereMenuClickListener(listener);


    }
}
