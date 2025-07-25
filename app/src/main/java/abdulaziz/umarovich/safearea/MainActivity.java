package abdulaziz.umarovich.safearea;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        SafeArea.apply(findViewById(R.id.scroll_content), SafeArea.EDGE_ALL, SafeArea.InsetType.PADDING);
    }
}