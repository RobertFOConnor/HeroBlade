package ie.ul.postgrad.socialanxietyapp;

/**
 * Created by Robert on 04-Sep-17.
 */

public class TextScroller {

    private String message;

    public void scroll() {
        final String message = "testing12345";


        Thread scroller = new Thread(new Runnable() {
            int count = 0;

            @Override
            public void run() {
                try {
                    while (count < message.length()) {
                        String temp = message.substring(0, count);
                        count++;
                        System.out.println(temp);
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        scroller.start();
    }
}
