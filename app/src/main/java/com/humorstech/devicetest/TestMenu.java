package com.humorstech.devicetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.humorstech.devicetest.services.DeviceTestApi;

import java.util.Objects;

/**
 * TestMenu activity handles the device test UI, updates button colors based on test status,
 * and submits test data using the DeviceTestApi service.
 */
public class TestMenu extends AppCompatActivity {

    private static final String TAG = "TestMenu";

    private Button btnI2C, btnAHT20, btnBMP180, btnUVLight, btnAGS,
            btnAcetone, btnH2, btnAllGases, btnFullSystemCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_test_menu);

            // Apply window insets to the main view
            View mainView = findViewById(R.id.main);
            if (mainView != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            } else {
                Log.e(TAG, "Main view (R.id.main) is null.");
            }
            init();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Initialization error", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Initializes view variables and sets up the button behaviors.
     */
    private void init() {
        initViews();
        setupButtons();
    }

    /**
     * Retrieves all required view references.
     */
    private void initViews() {
        btnI2C = findViewById(R.id.btnI2C);
        btnAHT20 = findViewById(R.id.btnAHT20);
        btnBMP180 = findViewById(R.id.btnBMP180);
        btnUVLight = findViewById(R.id.btnUVLight);
        btnAGS = findViewById(R.id.btnAGS);
        btnAcetone = findViewById(R.id.btnAcetone);
        btnH2 = findViewById(R.id.btnH2);
        btnAllGases = findViewById(R.id.btnAllGases);
        btnFullSystemCheck = findViewById(R.id.btnFullSystemCheck);
    }

    /**
     * Reads test values from SharedPreferences, updates the UI accordingly, and sets up the submit button.
     */
    private void setupButtons() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(TestData.TITLE, Context.MODE_PRIVATE);
            if (sharedPreferences == null) {
                Log.e(TAG, "SharedPreferences is null.");
                return;
            }

            // Retrieve test statuses and tester name
            String testerName = sharedPreferences.getString(TestData.TEST_DONE_BY, "ngttull");
            String I2C_TEST = sharedPreferences.getString(TestData.I2C_TEST, "pass");
            String AHT20_TEST = sharedPreferences.getString(TestData.AHT20_TEST, "pass");
            String BMP180_TEST = sharedPreferences.getString(TestData.BMP180_TEST, "pass");
            String UVLight_TEST = sharedPreferences.getString(TestData.UVLight_TEST, "pass");
            String AGS_TEST = sharedPreferences.getString(TestData.AGS_TEST, "pass");
            String Acetone_TEST = sharedPreferences.getString(TestData.Acetone_TEST, "pass");
            String H2_TEST = sharedPreferences.getString(TestData.H2_TEST, "pass");
            String ALL_GASSES_TEST = sharedPreferences.getString(TestData.ALL_GASSES_TEST, "pass");
            String FULL_SYSTEM_CHECK = sharedPreferences.getString(TestData.FULL_SYSTEM_CHECK, "fail");

            // Update button colors based on test statuses
            updateButtonColor(btnI2C, I2C_TEST);
            updateButtonColor(btnAHT20, AHT20_TEST);
            updateButtonColor(btnBMP180, BMP180_TEST);
            updateButtonColor(btnUVLight, UVLight_TEST);
            updateButtonColor(btnAGS, AGS_TEST);
            updateButtonColor(btnAcetone, Acetone_TEST);
            updateButtonColor(btnH2, H2_TEST);
            updateButtonColor(btnAllGases, ALL_GASSES_TEST);
            updateButtonColor(btnFullSystemCheck, FULL_SYSTEM_CHECK);

            // Configure the submit test button click listener
            setupSubmitTest(testerName,
                    I2C_TEST,
                    AHT20_TEST,
                    BMP180_TEST,
                    UVLight_TEST,
                    AGS_TEST,
                    Acetone_TEST,
                    H2_TEST,
                    ALL_GASSES_TEST,
                    FULL_SYSTEM_CHECK);
        } catch (Exception e) {
            Log.e(TAG, "Error in setupButtons: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the background color of a button based on its test status.
     *
     * @param button The button to update.
     * @param status The test status ("pass", "fail", or "NOT_DONE").
     */
    private void updateButtonColor(Button button, String status) {
        if (button == null) {
            Log.w(TAG, "Button is null while updating color for status: " + status);
            return;
        }
        if ("pass".equalsIgnoreCase(status)) {
            button.setBackgroundColor(Color.GREEN);
        } else if ("fail".equalsIgnoreCase(status)) {
            button.setBackgroundColor(Color.RED);
        }
        // Leave default color for "NOT_DONE" or other statuses.
    }

    /**
     * Configures the submit test button. If all tests are completed, the API call is initiated.
     *
     * @param testerName       The name of the tester.
     * @param I2C_TEST         Status for I2C_TEST.
     * @param AHT20_TEST       Status for AHT20_TEST.
     * @param BMP180_TEST      Status for BMP180_TEST.
     * @param UVLight_TEST     Status for UVLight_TEST.
     * @param AGS_TEST         Status for AGS_TEST.
     * @param Acetone_TEST     Status for Acetone_TEST.
     * @param H2_TEST          Status for H2_TEST.
     * @param ALL_GASSES_TEST  Status for ALL_GASSES_TEST.
     * @param FULL_SYSTEM_CHECK Status for FULL_SYSTEM_CHECK.
     */
    private void setupSubmitTest(String testerName,
                                 String I2C_TEST,
                                 String AHT20_TEST,
                                 String BMP180_TEST,
                                 String UVLight_TEST,
                                 String AGS_TEST,
                                 String Acetone_TEST,
                                 String H2_TEST,
                                 String ALL_GASSES_TEST,
                                 String FULL_SYSTEM_CHECK) {
        Button btnSubmitTestButton = findViewById(R.id.btnSubmitTest);
        if (btnSubmitTestButton == null) {
            Log.e(TAG, "Submit Test button (R.id.btnSubmitTest) not found.");
            return;
        }
        btnSubmitTestButton.setOnClickListener(v -> {
            // Validate tester name and that all tests are completed
            if (testerName == null || testerName.trim().isEmpty()) {
                Toast.makeText(TestMenu.this, "Tester name invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("NOT_DONE".equalsIgnoreCase(I2C_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete I2C_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(AHT20_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete AHT20_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(BMP180_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete BMP180_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(UVLight_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete UVLight_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(AGS_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete AGS_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(Acetone_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete Acetone_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(H2_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete H2_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(ALL_GASSES_TEST)) {
                Toast.makeText(TestMenu.this, "Please complete ALL_GASSES_TEST", Toast.LENGTH_SHORT).show();
                return;
            } else if ("NOT_DONE".equalsIgnoreCase(FULL_SYSTEM_CHECK)) {
                Toast.makeText(TestMenu.this, "Please complete FULL_SYSTEM_CHECK", Toast.LENGTH_SHORT).show();
                return;
            }

            // All tests completed, initiate API call using DeviceTestApi
            DeviceTestApi.insertDeviceTest(new DeviceTestApi.ApiCallback() {
                                               @Override
                                               public void onSuccess(String response) {
                                                   Log.d(TAG, "API call succeeded: " + response);
                                                   runOnUiThread(() ->
                                                           Toast.makeText(TestMenu.this, "Test submitted successfully", Toast.LENGTH_SHORT).show()
                                                   );
                                               }

                                               @Override
                                               public void onFailure(Throwable error) {
                                                   Log.e(TAG, "API call failed", error);
                                                   runOnUiThread(() ->
                                                           Toast.makeText(TestMenu.this, "Failed to submit test", Toast.LENGTH_SHORT).show()
                                                   );
                                               }
                                           },
                    "insert",              // action
                    "HW-5678",             // hardware_id
                    I2C_TEST,              // i2c
                    AHT20_TEST,            // aht_20
                    BMP180_TEST,           // bmp_180
                    UVLight_TEST,          // uv_light
                    AGS_TEST,              // ags
                    Acetone_TEST,          // acetone
                    H2_TEST,               // h2
                    ALL_GASSES_TEST,       // all_gases
                    FULL_SYSTEM_CHECK,     // full_system_check
                    testerName             // test_done_by
            );
        });
    }
}
