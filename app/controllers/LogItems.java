package controllers;

import models.Device;
import models.LogItem;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.logItemsViews.indexView;

import java.util.List;

/**
 * Created by Luuk on 26/01/15.
 */
public class LogItems extends Controller {

    public static Result index() {
        List<LogItem> logItems = LogItem.find.all();
        return ok(indexView.render(logItems));
    }

    public static Result delete(Long id) {
        Device device = Device.find.byId(id);
        device.delete();
        return redirect(routes.LogItems.index());
    }


}
