import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author HyungTae Lim
 * @since 2016. 12. 8.
 */
public class ReactiveSample {

    public static void main(String[] args) {
        Iterable<Integer> iter = Arrays.asList(1,2,3,4,5);

        Publisher<Integer> publisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                Iterator<Integer> it = iter.iterator();

                subscriber.onSubscribe(new Subscription() {  // Back pressure
                    @Override
                    public void request(long n) {
                        while (n-- > 0) {
                            if (it.hasNext()) {
                                subscriber.onNext(it.next());
                            } else {
                                subscriber.onComplete();
                                break;
                            }
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };


        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            final long COUNT = 2;
            int cnt = 0;
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                subscription = s;
                subscription.request(COUNT);
            }

            @Override
            public void onNext(Integer integer) {
                cnt++;
                System.out.println("onNext: " + integer);
                if(cnt == COUNT) {
                    cnt = 0;
                    subscription.request(COUNT);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        publisher.subscribe(subscriber);
    }
}
