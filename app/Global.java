import models.DeviceRole;
import play.Application;
import play.GlobalSettings;


public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        if (DeviceRole.find.findRowCount() != DeviceRole.RoleName.values().length) {
            for (DeviceRole.RoleName roleName : DeviceRole.RoleName.values()) {
                DeviceRole newRole = new DeviceRole();
                newRole.name = roleName.toString();
                newRole.save();
            }


        }

        super.onStart(app);
    }
}
