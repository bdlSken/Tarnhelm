import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.ac.lz233.tarnhelm.extension.api.ExtContext;
import cn.ac.lz233.tarnhelm.extension.api.ExtService;
import cn.ac.lz233.tarnhelm.extension.api.ITarnhelmExt;

public class TarnhelmExt implements ITarnhelmExt {

    @Override
    public ExtInfo extensionInfo() {
        return new ExtInfo() {
            @Override
            public String id() {
                return "cn.ac.lz233.tarnhelm.ext.example";
            }

            @Override
            public String author() {
                return "Tarnhelm";
            }

            @Override
            public String name() {
                return "Example Extension";
            }

            @Override
            public String description() {
                return "Rewrites bilibili.com links to b23.tv";
            }

            @Override
            public String extensionURL() {
                return "https://github.com/lz233/Tarnhelm";
            }

            @Override
            public int versionCode() {
                return 1;
            }

            @Override
            public String versionName() {
                return "1.0";
            }

            @Override
            public boolean hasConfigurationPanel() {
                return false;
            }

            @Override
            public int minTarnhelmSdkVersion() {
                return 1;
            }

            @Override
            public int minAndroidSdkVersion() {
                return 27;
            }

            @Override
            public String[] regexes() {
                return new String[]{"bilibili\\.com", "b23\\.tv"};
            }
        };
    }

    @Override
    public ExtService createExtensionService(ExtContext extContext) {
        return new ExampleExtService(extContext);
    }
}
