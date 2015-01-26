package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Created by Luuk on 25/01/15.
 */

@Entity
public class Action extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public float tempLow;

    @Constraints.Required
    public float tempHigh;

    public int port;

    @ManyToOne
    public Sensor trigger;

    public boolean actionUp;
    public boolean triggerCv;
    public boolean isCv;

    public boolean status;

}
