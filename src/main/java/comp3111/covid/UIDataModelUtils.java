package comp3111.covid;

import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class UIDataModelUtils {
    static public void updateAvailableCountries(UIDataModel data) {
       data.ISORepresentation = DataAnalysis.getAllLocationIso("COVID_Dataset_v1.0.csv");
        ObservableList<String> list = data.getAvailableCountries();

       if (data.ISORepresentation.isEmpty()) {
           list.remove(0, list.size());
       } else {
           Iterator<String> i = list.iterator();
           while (i.hasNext()) {
               if (!data.ISORepresentation.containsKey(i.next()));
               i.remove();
           }
           data.ISORepresentation.forEach((k, v) -> {
               if (list.indexOf(k) == -1)
                   list.add(k);
           });

           Collections.sort(list);
       }
    }
}
