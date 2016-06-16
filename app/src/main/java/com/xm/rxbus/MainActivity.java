package com.xm.rxbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Produce;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //  注册时会自动从当前类里面拿取注释为@Produce 和 @Subscribe，解析处理，会发送出来消息
        //创建的地方注册进来，需要销毁的时候撤销，否则内存泄漏，所以你可以自己封装下baseActivity,完成注册和销毁动作。
        RxBus.get().register(this);
        //也可以自己使用
        RxBus.get().post("hello");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁
        RxBus.get().unregister(this);
    }

    static class  BusAction
    {
       public static final String EAT_MORE="eat_more";
        public static final String EAT_TEST="eat_test";
    }


    //接收一个消息，消息区别是通过参数，因此这里接收一个post事件，参数为字串时，这里就可以收到。
    @Subscribe
    public void eat(String food) {
        Toast.makeText(this,food,Toast.LENGTH_SHORT).show();
    }

    //接收一个列表参数，并且限制这个接收线程是IO,标记为BusAction.EAT_MORE ,标记需要和发送者一致，线程不要需要
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(BusAction.EAT_TEST)
            }
    )
    public void eatMore(ArrayList<String> foods) {
        // purpose
        Toast.makeText(this,"eatMore",Toast.LENGTH_SHORT).show();
    }


//    @Produce
//    public String produceFood() {
//        return "This is bread!";
//    }

    @Produce(
            thread = EventThread.IO,
            tags = {
                    @Tag(BusAction.EAT_TEST)
            }
    )
    public ArrayList<String> produceMoreFood() {
        ArrayList<String> list =new ArrayList<>();
        list.add("This");
        list.add("is");
        list.add("breads!");
        return list;
    }
}
