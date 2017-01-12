package pranav.apps.amazing.rxjava;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import butterknife.Bind;
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

    @Bind(R.id.button)Button valid_button_indicator;
    @Bind(R.id.email_view)EditText email;
    @Bind(R.id.phone_view)EditText number;
    @Bind(R.id.username_view)EditText username;


    //private DisposableSubscriber<Boolean> disposableObserver = null;

    //Whenever a observable subscribes to a observer through a scheduler(concurrency) then this whole package is called a subscription
    //A subscription means an observable is tied to an observer

    private Subscription _subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        observablesMethod1();
    }

    private void observablesMethod1() {
        /* RULES
        *This method will make the submit button lighten up only when email section contains pg
        * password field contains more than 8 characters
        * number field contains a number between 1 and 100
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
        Observable<Boolean> email_change_obervable = RxHelper.getTextWatcherObservable(email)
                .debounce(500,TimeUnit.MILLISECONDS)
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
                            return false;
                        }
                        return true;
                    }
                });
        _subscription = Observable.combineLatest(email_change_obervable,username_change_observable, number_change_observable, new Func3<Boolean, Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean t1, Boolean t2, Boolean t3) {
                return t1&&t2&&t3;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isValid) {
                if(isValid){
                    valid_button_indicator.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    valid_button_indicator.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        _subscription.unsubscribe();
    }
}
