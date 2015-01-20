# What changed?

 * `DeviceRole` was changed to the Ebean model
 * Unfortunately handling forms with checkboxes requires using transient field like `rolesIds` in `Device` model... If I'll find better way I'll let you know
 * Now you can use it as `ManyToMany` relation, also search in both directions, see `http://localhost:9000/roles/`
 * Check `Global` class to see how roles are copied from enum to DB, anyway using the MM relation you can resign from this enum at all and maybe add some edit form to manage role(s)
 * finally take a look how to reuse `editView` and `Devices.update()` action for both - adding and editing the devices (no need for repeating ie. error handling etc)