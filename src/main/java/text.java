import com.crossrank.Serialize;

import java.util.HashMap;
import java.util.Map;

public class text {
    public static void main(String[] args) {
        Map<String, Integer> rankings = new HashMap<>() {{
            put("cake", 1);
        }};

        Serialize.Save(rankings);
    }
}
