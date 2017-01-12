package pranav.apps.amazing.rxjava;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import butterknife.Bind;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btn_demo_form_valid)TextView valid_button_indicator;
    @Bind(R.id.demo_combl_email)EditText email;
    @Bind(R.id.demo_combl_num)EditText number;
    @Bind(R.id.demo_combl_password)EditText password;


    //private DisposableSubscriber<Boolean> disposableObserver = null;

    //Whenever a observable subscribes to a observer through a scheduler(concurrency) then this whole package is called a subscription
    //A subscription means an observable is tied to an observer

    private Subscription _subscription;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        observablesMethod1();




        /*
                .map(new Function<EditText,Boolean >() {
                    @Override
                    public Boolean apply(EditText editText) throws Exception {
                        if(editText.getText().toString().contains("234")){
                            return true;
                        }
                        return false;
                    }
                });
        password_change_observable
                .debounce(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<EditText, Boolean>() {
                    @Override
                    public Boolean apply(EditText editText) throws Exception {
                        if(editText.getText().toString().contains("@")){
                            return true;
                        }
                        return false;
                    }
                });
*/



    }

    private void observablesMethod1() {
        /* RULES
        *This method will make the submit button lighten up only when email section contains pg
        * password field contains more than 8 characters
        * number field contains a number between 1 and 100
        * */



        /*
        *  operators like debounce , map are used on observables and not on observers
        *
        *  Map is used to transfer the event emitted in one form to other
        *  so finally the observer is able to see the events in the transformed form by map.
        *  In this case as our observable is on boolean i.e our observable if finally going to emit an boolean event
        *  so when we apply map operator on this it is automatically will include this Func1 which converts out String which was coming
        *  from RxHelper class to a Boolean (autocomplete of Func1 showed me this) as finally our observable has to emit an Boolean
        */

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
                        if(s.length()>=1 && s.length()<=100){
                            return true;
                        }
                        return false;
                    }
                });
        Observable<Boolean> password_change_observable = RxHelper.getTextWatcherObservable(password)
                .debounce(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if(s.length()<8){
                            return false;
                        }
                        return true;
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        _subscription.unsubscribe();
    }
}
