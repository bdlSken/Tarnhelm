package cn.ac.lz233.tarnhelm.ext.example;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.ac.lz233.tarnhelm.extension.api.ExtContext;
import cn.ac.lz233.tarnhelm.extension.api.ExtService;

public class ExampleExtService extends ExtService {

    public ExampleExtService(ExtContext extContext) {
        super(extContext);
    }

    @Override
    public void onExtInstall() {
    }

    @Override
    public void onExtUninstall() {
    }

    @Override
    public String onCheckUpdate() {
        return "https://github.com/lz233/Tarnhelm";
    }

    @Override
    public String onHandleString(CharSequence charSequence) {
        return charSequence.toString().replace("www.bilibili.com", "b23.tv")
                .replace("bilibili.com", "b23.tv");
    }

    @Override
    public View onRequestConfigurationPanel(Context context) {
        return new TextView(context);
    }
}
