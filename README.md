# RxBusDemo
Rxbus事件交互
源码地址：
https://github.com/luxiaoming/RxBus
目标：
使得处理传递数据，不需要接口，直接使用注释关联起来。
使用方法：
第一步
在你的项目gradle文件里面，引入compile 'com.hwangjr.rxbus:rxbus:1.0.3' 导入这个包
有时会出现和其他包冲突，使用：
compile ('com.hwangjr.rxbus:rxbus:1.0.3') {
    exclude group: 'com.hwangjr.utils', module: 'timber'
}
排除掉里面的冲突包，具体这个语法，可以搜索 gradle 教程。
第二步
写入一个实例，用来构造Rxbus实例，这个是单例模式存在，一般这个实现是放在app创建里面，然后通过接口获取，全局可以使用。

单例代码：
public final class RxBus {
    private static Bus sBus;
    public synchronized static Bus get() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }
}
然后我们如下使用：
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
美女如云：

幽默笑话：
1、今天刚把熊孩子放到学校，我刚出大门熊孩子就蹦蹦跳跳的追了出来。我：你怎么不去上课？熊孩子：l老师说不想上课的小朋友可以出去，我就出来了！
 2、熊孩子特别调皮，总爱欺负别的小朋友。为了净化他的心灵，我给他讲了白雪公主的故事。结果第二天我下班回来的时候，这熊孩子正拿个针管网苹果里注水...... 
3、今天送孩子去上学。听到两个熊孩子在旁边说话，一女孩：你爱我吗？熊孩子：我麻麻一天给我两块钱，我给你花一块五，你说我爱你吗？
 4、我家熊孩子生病了，带着熊孩子去医院输液。护士给熊孩子扎针的时候，熊孩子说：粑粑，这个姐姐看起来好亲切！我：为啥这么说呢？熊孩子大声说：麻麻昨晚不是穿的这种衣服吗？旁边的人都用异样的眼神看着我...... 
5、在客厅看电视的时候放了个屁。熊孩子来了句：粑粑，你这屁可真臭，勾点芡粉就成屎了！
