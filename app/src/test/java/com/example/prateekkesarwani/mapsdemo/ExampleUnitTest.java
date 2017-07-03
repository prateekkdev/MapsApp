package com.example.prateekkesarwani.mapsdemo;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    int value = 0;

    @Test
    public void performExponentialBackoff() throws Exception {


        for (; value < 10; ) {

            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {

                    value++;

                    final boolean result;
                    if (value == 5 || value == 6 || value == 7) {
                        result = false;
                    } else {
                        result = true;
                    }

                    if (result) {
                        e.onComplete();
                    } else {
                        e.onError(new Throwable());
                    }
                }
            })
                    .doOnError(throwable -> Thread.sleep(1000))
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            tagTime("next", value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            tagTime("error", value);
                        }

                        @Override
                        public void onComplete() {
                            tagTime("next", value);
                        }
                    });
        }
    }

    long startTime = System.currentTimeMillis();

    public void tagTime(String str, int value) {
        System.out.println(str + " for: " + value + " at time " + (System.currentTimeMillis() - startTime) / 1000 + "s");
    }
}