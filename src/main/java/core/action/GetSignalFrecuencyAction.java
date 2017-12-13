package core.action;

import java.util.Map;
import java.util.stream.Collectors;

public class GetSignalFrecuencyAction {

    public String execute(Map<String, String> data) {

        String frecuency = "0";

        try {

            double period = Double.valueOf(data.entrySet().stream().limit(2).collect(Collectors.toList())
                    .get(1).getKey().split(" ")[0].replace("'", ""));

            frecuency = String.valueOf(1 / period);

        }catch (Exception e) {
            e.printStackTrace();
        }

        return frecuency;
    }
}
