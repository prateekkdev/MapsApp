package com.example.prateekkesarwani.mapsdemo;

import android.location.Location;

import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by prateek.kesarwani on 08/08/17.
 */

public class MoreTest {

    @Test
    public void MD5() {
        String plaintext = "your text here";

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16); // Now we need to zero pad it if you actually want the full 32 chars. while(hashtext.length() < 32 ){ hashtext = "0"+hashtext; }

            System.out.println(hashtext);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PublishSubject<Location> lastLocationSubject;

    @Test
    public void testLocationObservable() {

        lastLocationSubject = PublishSubject.create();

        Observable.just(true)
                .observeOn(Schedulers.newThread())
                .repeat()
                .delay(10, TimeUnit.SECONDS)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        System.out.println("Printing");
                    }
                });

        getLocationChangeObservable().subscribe();
    }

    /**
     * Subscriber is called in background thread(In computation or our newThread? Need to check)
     *
     * @return
     */
    public Observable<Location> getLocationChangeObservable() {
        return lastLocationSubject;
    }

    @Test
    public void rxtestCombinationOperators() {

        System.out.println("Prateek, rxstart");
        Observable observable1 = Observable.just(1, 2, 3);

        Observable observable2 = Observable.just(10, 20, 30);

        System.out.println("combine latest:-");
        Observable.combineLatest(observable1, observable2, new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) throws Exception {
                return (int) o * (int) o2;

            }
        }).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println("Prateek, rx: " + (int) o);
            }
        });

        System.out.println("zip:-");

        Observable.zip(observable1, observable2, new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) throws Exception {
                return (int) o * (int) o2;

            }
        }).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println("Prateek, rx: " + (int) o);
            }
        });

        System.out.println("concat:- here one after other, like git rebase");
        Observable.concat(observable1, observable2).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println("Prateek, rx: " + (int) o);
            }
        });

        System.out.println("merge:- here one inbetween other, like git merge");
        Observable.merge(observable1, observable2).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println("Prateek, rx: " + (int) o);
            }
        });

        System.out.println("reduce:- multiple are reduced to single");
        observable1.reduce(new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) throws Exception {
                return (int) o + (int) o2;
            }
        }).subscribe();


        observable1.skipWhile(new Predicate() {
            @Override
            public boolean test(Object o) throws Exception {
                return (int) o != 2;
            }
        }).subscribe(getPrintInterface(""));

        observable1.skipUntil(new Observable() {
            @Override
            protected void subscribeActual(Observer observer) {

            }
        });
    }

    @Test
    public void rxtest2Subjects() {

        PublishSubject subject = PublishSubject.create();

        System.out.println();
        subject.filter(new Predicate() {
            @Override
            public boolean test(Object o) throws Exception {
                if ((int) o % 2 == 0) {
                    return true;
                }
                return false;
            }
        }).subscribe(getPrintInterface("div2"));

        subject.onNext(18);
        subject.onNext(21);
        subject.onNext(25);
        subject.onNext(27);

        System.out.println();
        Disposable d2 = subject.filter(new Predicate() {
            @Override
            public boolean test(Object o) throws Exception {
                if ((int) o % 3 == 0) {
                    return true;
                }
                return false;
            }
        }).subscribe(getPrintInterface("div3"));

        subject.onNext(6);
        subject.onNext(8);
        subject.onNext(9);
        subject.onNext(10);
        subject.onNext(20);

        compositeDisposable.add(d2);


    }

    Consumer getPrintInterface(final String str) {

        return new Consumer() {

            @Override
            public void accept(Object o) throws Exception {
                System.out.println("Prateek, rx, " + str + ": " + o);
            }
        };
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Test
    public void uselessTest() {

    }

    @Mo
}
