package com.kongdy.hasee.shperemenu_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.kongdy.hasee.shperemenu_android.view.SphereMenu;


public class MainActivity extends AppCompatActivity {

    private SphereMenu m_sphere_m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_sphere_m = (SphereMenu) findViewById(R.id.m_sphere_m);
        ImageView iv1 = new ImageView(getApplicationContext());
        ImageView iv2 = new ImageView(getApplicationContext());
        ImageView iv3 = new ImageView(getApplicationContext());
        ImageView iv4 = new ImageView(getApplicationContext());

        iv1.setImageResource(R.mipmap.m_1);
        iv2.setImageResource(R.mipmap.m_2);
        iv3.setImageResource(R.mipmap.m_3);
        iv4.setImageResource(R.mipmap.m_4);

        m_sphere_m.addView(iv1);
        m_sphere_m.addView(iv2);
        m_sphere_m.addView(iv3);
        m_sphere_m.addView(iv4);

    }
}
