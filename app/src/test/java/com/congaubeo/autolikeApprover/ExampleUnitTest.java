package com.congaubeo.autolikeApprover;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    String sam1 = "SD TK 0011000412973 +150,000VND luc 26-12-2020 09:46:12. SD 222,772,047VND. Ref 292017.261220.094610.ALIKEHQ5PT6A";
    String sam2 = "SD TK 0011000412973 -2,005,500VND luc 26-12-2020 10:07:01. SD 217,908,847VND. Ref MBVCB.911539506.048292.TRUONG THANH NAM chuyen tien.CT tu 0011000412973 TR...";
    String sam3 = "SD TK 0011000412973 +150,000VND luc 26-12-2020 09:46:12. SD 222,772,047VND. Ref 292017.261220.094610.ALIKEHQ5PT6A";
    String sam4 = "SD TK 0011000412973 +1,000,000VND luc 26-12-2020 23:00:25. SD 229,563,347VND. Ref 793836.261220.230026. MTXMZKE-261220-22:59:59 793836";

    @Test
    public void addition_isCorrect() {
        // Split
//        Bill bill = new Bill(sam1, new Date().getTime());
//        assertTrue(bill.isValid());
//        bill = new Bill(sam2, new Date().getTime());
//        assertFalse(bill.isValid());
//        bill = new Bill(sam3, new Date().getTime());
//        assertTrue(bill.isValid());

        Bill bill = new Bill(sam4, new Date().getTime());
        assertTrue(bill.isMT());
        System.out.println(bill);
    }

    @Test
    public void testTimeConvert() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        System.out.println(simpleDateFormat.format(date));
    }
}