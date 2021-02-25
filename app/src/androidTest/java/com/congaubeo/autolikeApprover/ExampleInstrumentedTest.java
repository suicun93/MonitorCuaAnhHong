package com.congaubeo.autolikeApprover;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    static final DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    String sam1 = "SD TK 0011000412973 +150,000VND luc 26-12-2020 09:46:12. SD 222,772,047VND. Ref 292017.261220.094610.ALIKEHQ5PT6A";
    String sam2 = "SD TK 0011000412973 -2,005,500VND luc 26-12-2020 10:07:01. SD 217,908,847VND. Ref MBVCB.911539506.048292.TRUONG THANH NAM chuyen tien.CT tu 0011000412973 TR...";
    String sam3 = "SD TK 0011000412973 +150,000VND luc 26-12-2020 09:46:12. SD 222,772,047VND. Ref 292017.261220.094610.ALIKEHQ5PT6A";
    String sam4 = "SD TK 0021000382699 +65,000VND luc 16-01-2021 19:12:19. SD 65,463VND. Ref MBVCB.945798001.TRUONG THANH NAM chuyen tien.CT tu 0011000412973 TRUONG THANH NAM ...";
    String sam5 = "SD TK 0011000412973  +150,000VND luc 17-01-2021 11:48:17. SD 19,186,512VND. Ref 249080.170121.114817.ALIKEHQ5P400";
    String sam6 = "SD TK 0011000412973  +2,000,000VND luc 16-02-2021 23:25:08. SD 78,978,301VND. Ref 402937.160221.232508.ALIKEHQ5BD50 FT21048196388350";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.congaubeo.autolikeApprover", appContext.getPackageName());
//        Bill bill = new Bill("SD TK 0021000382699 +65,000VND luc 16-01-2021 19:12:19. SD 65,463VND. Ref MBVCB.945798001.TRUONG THANH NAM chuyen tien.CT tu 0011000412973 TRUONG THANH NAM ...", new Date().getTime());
        Bill bill = new Bill(sam6, new Date(2021, 1, 17, 11, 48).getTime());

        try {
            SyncHttpClient client = new SyncHttpClient();
            StringEntity entity = new StringEntity("date=" + formatter.format(new Date(bill.getTimestamp())) +
                    "&key=" + bill.getKey() +
                    "&amount=" + bill.getAmount() +
                    "&type=" + (bill.isALike() ? "ALIKE" : bill.isAFarm() ? "AFARM" : "") +
                    "&success=" + (bill.isSuccess() ? "OK" : "Failed") +
                    "&smsbody=" + bill.getSmsBody() +
                    "&sheet_name=" + ((bill.isALike() || bill.isAFarm())? "gicungduoc":"M1"));
            entity.setContentType("application/x-www-form-urlencoded");
            client.post(appContext, "https://script.google.com/macros/s/AKfycbyYdD0A0koNfGuN4PU4cocGwtTQkA9-3lrGdRVK2OlM6gbicYSaGf6q0Q/exec",
                    entity,
                    "application/x-www-form-urlencoded",
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.println(new String(responseBody));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            try {
                                System.out.println(new String(errorResponse));
                            } catch (Exception ex) {
                                bill.sendReport(appContext);
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
            );
        } catch (Exception e) {
        }
    }
}