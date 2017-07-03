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


    @Test
    public void performExponentialBackoff() throws Exception {


        for (int input = 1; input <= 10; input++) {
            final boolean result;
            final int value = input;
            if (input == 5 || input == 6 || input == 7) {
                result = false;
            } else {
                result = true;
            }

            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    if (result) {
                        e.onComplete();
                    } else {
                        e.onError(new Throwable());
                    }
                }
            }).subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Boolean aBoolean) {
                    System.out.println("Next for: " + value + "");
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("Error for: " + value + "");
                }

                @Override
                public void onComplete() {
                    System.out.println("Complete for: " + value + "");
                }
            });
        }
    }
}