package li4ngorange.com.httpurlconnectionandroidofficial;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by liangorange on 10/30/15.
 */
public class CustomApater extends ArrayAdapter<HashMap> {

    ArrayList<HashMap> messageAdapter=new ArrayList<>();


    // context: the information(background information)
    // resource: the data source
    public CustomApater(Context context, ArrayList<HashMap> resource) {
        super(context, R.layout.custom_row, resource);
        this.messageAdapter = resource;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HashMap currentMap = new HashMap();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customView = inflater.inflate(R.layout.custom_row, parent, false);

        TextView cityName = (TextView) customView.findViewById(R.id.textCityName);
        TextView currentTemp = (TextView) customView.findViewById(R.id.textCurrent);
        TextView maxTemp = (TextView) customView.findViewById(R.id.textMax);
        TextView minTemp = (TextView) customView.findViewById(R.id.textMin);

        currentMap = messageAdapter.get(position);

        HashMap mainField = (HashMap)currentMap.get("main");

        cityName.setText(currentMap.get("name").toString());

        currentTemp.setText("Current Temperature: " + mainField.get("temp"));
        maxTemp.setText("Lowest Temperature: " + mainField.get("temp_max"));
        minTemp.setText("Highest Temperature: " + mainField.get("temp_min"));


        return customView;
    }
}
