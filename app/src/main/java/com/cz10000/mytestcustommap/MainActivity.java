package com.cz10000.mytestcustommap;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private AMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap() ;
        String path = Environment.getExternalStorageDirectory()+"/data/style" ;
        map.setCustomMapStylePath(path);
//        map.setCustomMapStyleID("058bcb2d7d83d92361fd3f0929e2cd4c");
        map.setMapCustomEnable(true);

    }


}
