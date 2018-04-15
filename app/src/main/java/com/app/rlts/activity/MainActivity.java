package com.app.rlts.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.app.rlts.R;
import com.estimote.internal_plugins_api.cloud.CloudCredentials;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudCredentials cloudCredentials =
                new EstimoteCloudCredentials(getString(R.string.app_id), getString(R.string.app_token));

        final TextView tv = (TextView) findViewById(R.id.change_view);

        // create the Proximity Observer
        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), (EstimoteCloudCredentials) cloudCredentials)
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                //tv.setText(R.string.proximity_observer_error);
                                //Toast.makeText(MainActivity.this, R.string.proximity_observer_error, Toast.LENGTH_LONG);
                                Log.e("app", "proximity observer error" + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();

        // create proximity zone objects
        ProximityZone allBeacons = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("uuid", "B9407F30-F5F8-466E-AFF9-25556B57FE6D")
                .inFarRange()
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        tv.setText(R.string.welcome);
                        //Toast.makeText(MainActivity.this, getString(R.string.welcome), Toast.LENGTH_LONG);
                        Log.d("app", "Welcome!");
                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        tv.setText(R.string.bye);
                        //Toast.makeText(MainActivity.this, R.string.bye, Toast.LENGTH_LONG);
                        Log.d("app", "Bye bye!");
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(allBeacons);

        ProximityZone major2780 = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("major", "2780")
                .inNearRange()
                .withOnChangeAction(new Function1<List<? extends ProximityAttachment>, Unit>() {
                    @Override
                    public Unit invoke(List<? extends ProximityAttachment> attachments) {
                        List<String> location = new ArrayList<>();
                        for (ProximityAttachment attachment : attachments) {
                            location.add(attachment.getPayload().get("minor"));
                        }
                        Log.d("app", "Nearby location: " + location);
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(major2780);

        // request requirements permission
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                //tv.setText(R.string.reqs_fulfilled);
                                //Toast.makeText(MainActivity.this, R.string.reqs_fulfilled, Toast.LENGTH_LONG);
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                //tv.setText(R.string.reqs_missing);
                                //Toast.makeText(MainActivity.this, R.string.reqs_missing, Toast.LENGTH_LONG);
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                //tv.setText(R.string.reqs_error);
                                //Toast.makeText(MainActivity.this, R.string.reqs_error, Toast.LENGTH_LONG);
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }
}
