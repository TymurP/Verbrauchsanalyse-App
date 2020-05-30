package com.zinn.verbrauchanalyse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.zinn.verbrauchanalyse.pojos.DataPoint;
import com.zinn.verbrauchanalyse.ui.constants.Constants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.zinn.verbrauchanalyse.ui.constants.Constants.ACTIVITY_GET_FILE;
import static com.zinn.verbrauchanalyse.ui.constants.Constants.DATA_POINTS;
import static com.zinn.verbrauchanalyse.ui.constants.Constants.filePath;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    final static int WRITE_EXTERNAL_PERMISSION = 1;
    final static int READ_EXTERNAL_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    , WRITE_EXTERNAL_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    , READ_EXTERNAL_PERMISSION);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_GET_FILE && resultCode == RESULT_OK) {
            loadFile(data.getData());
        }
    }

    private void loadFile(final Uri path) {
        if (Objects.equals(path.getPath(), "")) return;
        try {
            final InputStream inputStream = getContentResolver().openInputStream(path);
            assert inputStream != null;
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            final List<DataPoint> dataPoints = new ArrayList<>();
            int index = 1;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("Datum")) {
                    line = line.replace(",", ".");
                    final List<String> params = Arrays.asList(line.split(";"));
                    dataPoints.add(new DataPoint()
                            .setIndex(index)
                            .setDateWithFormat(params.get(0))
                            .setElectricityMeter(Double.parseDouble(params.get(1).length() > 6 ? params.get(1).substring(0, 6) : params.get(1)))
                            .setGasMeter(Double.parseDouble(params.get(2).length() > 6 ? params.get(2).substring(0, 6) : params.get(2)))
                            .setWaterMeter(Double.parseDouble(params.get(3).length() > 6 ? params.get(3).substring(0, 6) : params.get(3)))
                            .setPowerConsumption(Double.parseDouble(params.get(4).length() > 6 ? params.get(4).substring(0, 6) : params.get(4)))
                            .setGasConsumption(Double.parseDouble(params.get(5).length() > 6 ? params.get(5).substring(0, 6) : params.get(5)))
                            .setWaterConsumption(Double.parseDouble(params.get(6).length() > 6 ? params.get(6).substring(0, 6) : params.get(6))));
                    index++;
                }
            }
            DATA_POINTS = dataPoints;
            inputStream.close();
            reader.close();
            Toast.makeText(this, "Datei wurde eingelesen", Toast.LENGTH_LONG).show();
            filePath = path;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error while reading file!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadFile(View view) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("*/*");
        startActivityForResult(intent, Constants.ACTIVITY_GET_FILE);
    }
}
