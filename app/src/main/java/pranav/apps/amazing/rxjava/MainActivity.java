package pranav.apps.amazing.rxjava;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;


import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;


public class MainActivity extends AppCompatActivity {


/*
    @BindView(R2.id.button) Button valid_button_indicator;
    @BindView(R2.id.email_view) EditText email;
    @BindView(R2.id.phone_view) EditText number;
    @BindView(R2.id.username_view) EditText username;
*/
    private static String TAG = "MainActivity";

    Button valid_button_indicator;
    EditText email,number,username;



    //private DisposableSubscriber<Boolean> disposableObserver = null;

    //Whenever a observable subscribes to a observer through a scheduler(concurrency)
    // then this whole package is called a subscription
    //A subscription means an observable is tied to an observer

    private Subscription _subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);

        valid_button_indicator = (Button)findViewById(R.id.button);
        email=(EditText)findViewById(R.id.email_view);
        number=(EditText)findViewById(R.id.phone_view);
        username=(EditText)findViewById(R.id.username_view);

        observablesMethod1();
    }

    private void observablesMethod1() {
        /* RULES
        *This method will make the submit button lighten up only when email section contains pg
        * password field contains more than 8 characters
        * username contains "Pranav" (without quotes)
        * */

        // Debounce is coming in very handy here.
        // What I had understood before is that if I use debounce, it will emit event after the give
        // time period regardless of other events.
        // But now I am realizing that this is not the case.
        // Let's say debounce interval is 200 milliseconds. Once an event is emitted, RxJava clock starts
        // ticking. Once 200 ms is up, debounce operator will emit that event.
        // One more event comes to debounce and it will start the clock for 200 ms. If another event comes
        // in 100 ms, debounce operator will reset the clock and start to count 200 ms again.
        // So let's say if you continue emitting events at 199 ms intervals, this debounce operator
        // will never emit any event.

        // Also, debounce by default goes on Scheduler thread, so it is important to add observeOn
        // and observe it on main thread.


        /*
        *  operators like debounce , map are used on observables and not on observers
        *
        *  Map is used to transfer the event emitted in one form to other
        *  so finally the observer is able to see the events in the transformed form by map.
        *  In this case as our observable is on boolean i.e our observable if finally going to emit an boolean event
        *  so when we apply map operator on this it is automatically will include this Func1 which converts out String which was coming
        *  from RxHelper class to a Boolean (autocomplete of Func1 showed me this) as finally our observable has to emit an Boolean
        */

        //Func1 is the mapper function
        Observable<Boolean> email_change_observable = RxHelper.getTextWatcherObservable(email)
                .debounce(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if(s.contains("pg")){
                            return true;
                        }
                        return false;
                    }
                });
        /*
        * combineLatest takes the emitters from all three observables and process them through Func3 to "combine the observables
        * into single observable which is then subscribed to an observer which is Action1 and it receives those events emitted by
        * observable and it performs action accordingly to that
        * */
        Observable<Boolean> number_change_observable = RxHelper.getTextWatcherObservable(number)
                .debounce(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if(s.length()==10){
                            return true;
                        }
                        return false;
                    }
                });

        Observable<Boolean> username_change_observable = RxHelper.getTextWatcherObservable(username)
                .debounce(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if(s.contains("Pranav")){
                            return true;
                        }
                        return false;
                    }
                });
        _subscription = Observable.combineLatest(email_change_observable,username_change_observable, number_change_observable, new Func3<Boolean, Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean t1, Boolean t2, Boolean t3) {
                return t1&&t2&&t3;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isValid) {
                valid_button_indicator.setEnabled(isValid);
                /*if(isValid){
                    valid_button_indicator.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    valid_button_indicator.setBackgroundColor(getResources().getColor(R.color.gray));
                }*/
            }
        });
    }

    // No validations. Just testing, if we are getting the data or not
    private void setupObservables() {

        /* Here observables are just made no mapping, no debounce etc
        * they will simply emit events in form of strings
        * */
        Observable<String> emailObservable = RxHelper.getTextWatcherObservable(email);
        Observable<String> usernameObservable = RxHelper.getTextWatcherObservable(username);
        Observable<String> phoneObservable = RxHelper.getTextWatcherObservable(number);

        // combineLatest -> It will start emitting once all the observables start emitting
        // If you add email (no event emitted). After that if you add username, still there won't
        // be any event emitted. Once you start adding phone, it will start emitting the events
        // Now, even if you remove the phone number and edit email, it will keep emitting events.
        // So, until all the observables start emitting events, combineLatest will not emit any events.
        _subscription = Observable.combineLatest(emailObservable, usernameObservable,
                /*Note that here in Func3 we have 3 strings which are events emitted from observable strings
                * and we are here combining three strings into a boolean observable
                *which is subscribed to an action Action1 which gives instruction to submit button
                * */
                phoneObservable, new Func3<String, String, String, Boolean>() {
                    @Override
                    public Boolean call(String email, String username, String phone) {
                        Log.i(TAG, "email: " + email + ", username: " + username + ", phone: " + phone);
                        return false;
                    }
                }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                Log.i(TAG, "submit button enabled: " + aBoolean);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        _subscription.unsubscribe();
    }
}
